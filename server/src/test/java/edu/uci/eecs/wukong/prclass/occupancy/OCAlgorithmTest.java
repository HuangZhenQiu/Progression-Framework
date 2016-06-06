package edu.uci.eecs.wukong.prclass.occupancy;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import edu.uci.eecs.wukong.edge.occupancy.HMMBasedLearningExtension;
import edu.uci.eecs.wukong.edge.occupancy.ODProgressionExtension;
import edu.uci.eecs.wukong.edge.occupancy.OccupancyDetection;
import edu.uci.eecs.wukong.edge.occupancy.OccupancyDetection.Occupancy;

/**
 * A simple test tool to compare the performance of schedule based algorithm and HMM based algorithm.
 */
public class OCAlgorithmTest {
	private String resourcePath = "data/occupancy/bedroom/";
	private int hours = 8; // from 12 am - 8 am
	private HMMBasedLearningExtension hmmExtension;
	private ODProgressionExtension hammingExtension;
	private OccupancyDetection occupancyDetection;
	
	public OCAlgorithmTest() {
		// Create a mock prClass for extension intialization
		occupancyDetection = new OccupancyDetection((short)8, (short)5, (short)15);
		hammingExtension = new ODProgressionExtension(occupancyDetection);
		hmmExtension = new HMMBasedLearningExtension(occupancyDetection);
	}
	
	private static class PIRData {
		private byte hour;
		private byte minutes;
		private boolean isDetected;
		
		public PIRData(byte hour, byte minutes, boolean isDetected) {
			this.hour = hour;
			this.minutes = minutes;
			this.isDetected = isDetected;
		}
		
		public PIRData(int timestamp,  boolean isDetected) {
			this.hour = (byte) (timestamp / 10000);
			this.minutes = (byte) ((timestamp % 10000) / 100);
			this.isDetected = isDetected;
		}
		
		public int getMinutes() {
			return hour * 60 + minutes;
		}
		
		public boolean isDetected() {
			return isDetected;
		}
	}
	
	/**
	 * Read sensor data from file
	 * @param path the file name to process
	 * @return
	 */
	public List<PIRData> loadData(String path) {
		List<PIRData> list = new ArrayList<PIRData> ();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath + path);
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while((line = bufferReader.readLine()) != null) {
				if (!line.isEmpty()) {
					int index = line.indexOf(",");
					int timestamp = Integer.parseInt(line.substring(0, index));
					int detected = Integer.parseInt(line.substring(index + 1));
					PIRData data = new PIRData(timestamp, detected == 1? true : false);
					list.add(data);
				}
			}
		} catch (Exception e) {
			System.out.println("Can't load activity data " + path);
		}
		
		return list;
	}
	
	/**
	 * Generate occupancy observation value from PIR sensor reading 
	 * 
	 * @param data List of sensor data values
	 * @param minutes  internals that 
	 * @return
	 */
	public List<Byte> generateObservation(List<PIRData> data, int minutes) {
		int n = hours * 60 / minutes;
		int slot = 0;
		List<Byte> list = new ArrayList<Byte> ();
		for (int i = 0; i < data.size() && slot < n; ) {
			byte value = 0;
			if (data.get(i).isDetected) {
				if ((slot * minutes <= data.get(i).getMinutes() && data.get(i).getMinutes() < (slot + 1) * minutes)) {
					i++;
					value = 1;
					slot++;
					list.add(value);
				} else if (data.get(i).getMinutes() < slot * minutes){
					i ++;
				} else {
					list.add(value);
					slot ++;
				}
			} else {
				i ++;
			}
		}
		
		while (slot < n) {
			list.add((byte)0);
			slot ++;
		}
		return list;
	}
	
	private void printObservation(List<Byte> observation) {
		for (int i = 0; i< observation.size(); i++) {
			System.out.print(observation.get(i));
		}
		
		System.out.println();
	}
	
	private List<List<ObservationDiscrete<Occupancy>>> buildOccupancyList(List<List<Byte>> observations) {
		List<List<ObservationDiscrete<Occupancy>>> occupancies =
				new ArrayList<List<ObservationDiscrete<Occupancy>>>();
		
		for (List<Byte> observation : observations) {
			occupancies.add(convent(observation));
		}
		
		return occupancies;
	}
	
	private List<ObservationDiscrete<Occupancy>> convent(List<Byte> observation){
		List<ObservationDiscrete<Occupancy>> occupany =  new ArrayList<ObservationDiscrete<Occupancy>> ();
		for (Byte value : observation) {
			occupany.add(value == 1? Occupancy.YES.observation() : Occupancy.NO.observation());
		}
		
		return occupany;
	}
	
	/**
	 * In order randomize the original observations one by one. It is because observations are from a period.
	 * Observations in period should have pattern in sequence. 
	 * 
	 * @param observations  real observations
	 * @param slots  how many slot to flip (randomize)
	 * @param size  how many days to generate
	 * @return
	 */
	private List<List<Byte>> ramdomize(List<List<Byte>> observations, int slots, int size) {
		List<List<Byte>> randoms = new ArrayList<List<Byte>> ();
		int round = size / observations.size();
		Random rn = new Random(); 
		for (int i = 0; i < round; i++) {
			for (int j = 0; j < observations.size(); j++) {
				List<Byte> copy = deepCopy(observations.get(j));
				for (int k = 0; k < slots ; k++) {
					int ron = rn.nextInt(observations.get(j).size());
					copy.set(ron, (byte) (1- copy.get(ron)));
				}
				randoms.add(copy);
			}
		}
		
		return randoms;
	}
	
	private List<Byte> deepCopy(List<Byte> observation) {
		List<Byte> copy = new ArrayList<Byte>();
		for (Byte  b : observation) {
			copy.add(b);
		}
		
		return copy;
	}
	
	private void printRatio(List<Double> ratio, int total) {
		for (Double r : ratio) {
			System.out.print(r / total);
			System.out.print(",");
		}
		System.out.println();
	}
	
	private List<Byte> merge(List<Byte> a, List<Byte> b) {
		List<Byte> merged = new ArrayList<Byte>();
		
		int max = a.size() > b.size() ? a.size() : b.size();
		for (int i = 0; i< max; i++) {
			if ( i < a.size() && i < b.size()) {
				merged.add((byte) (a.get(i) | b.get(i)));
			} else if (i < a.size()) {
				merged.add(a.get(i));
			} else {
				merged.add(b.get(i));
			}
		}
		
		return merged;
	}

	public static void main(String[] arg) {
		OCAlgorithmTest test = new OCAlgorithmTest();
		List<List<Byte>> observations = new ArrayList<List<Byte>>();
		List<Double> hammingRatio = new ArrayList<Double>();
		List<Double> hmmRatio = new ArrayList<Double>();
		
		
		for (int s = 1; s <= 1; s++) {
			for (int i = 19; i<=26; i++) {
				String path = "nov-" + i + "-node-" + s + ".txt";
				List<PIRData> pir = test.loadData(path);
				List<Byte> observation = test.generateObservation(pir, 15);
				test.printObservation(observation);
				if (s > 1) {
					observations.set(i - 19, test.merge(observations.get(i - 19), observation));
				} else {
					observations.add(observation);
				}
			}
		}
		
		for (int i = 0; i< observations.get(0).size(); i++) {
			hammingRatio.add(0.0);
			hmmRatio.add(0.0);
		}
	
		List<List<Byte>> trained = test.ramdomize(observations, 3, 32);
		// Build Schedule model
		test.hammingExtension.loadData(trained);
		// Build HMM model
		List<List<ObservationDiscrete<Occupancy>>> occupancies = test.buildOccupancyList(trained);
		test.hmmExtension = new HMMBasedLearningExtension(occupancies, test.occupancyDetection);
		
		
		int round = 10;
		for (int j = 0; j < round; j ++) {
			List<List<Byte>> rondomized = test.ramdomize(observations, 3, 8 * 100);
			// Evaluate the prediction correctness ratio
			for (List<Byte> observation : rondomized) {
				for (int i = 0; i < observation.size() - 1; i++) {
					
					// Prediction by using hamming distance
					boolean result = test.hammingExtension.predict(observation.subList(0, i), observation.size());
					boolean real = observation.get(i + 1) == 1;
					if (result == real) {
						hammingRatio.set(i, hammingRatio.get(i) + 1.0);
					}
					
					// Prediction by using hmm
					List<ObservationDiscrete<Occupancy>> subsequence = test.convent(observation.subList(0, i));
					subsequence.add(Occupancy.YES.observation());
					double yp = test.hmmExtension.predict(subsequence);
					subsequence.set(subsequence.size() - 1, Occupancy.NO.observation());
					double np = test.hmmExtension.predict(subsequence);
					if ((yp >= np && observation.get(i + 1) == 1) || (yp <= np && observation.get(i + 1) == 0)) {
						hmmRatio.set(i, hmmRatio.get(i) + 1.0);
					}
				}
			}
		}
		
		test.printRatio(hammingRatio, 800 * round);
		test.printRatio(hmmRatio, 800 * round);
	}
}
