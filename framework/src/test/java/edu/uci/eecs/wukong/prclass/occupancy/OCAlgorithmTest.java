package edu.uci.eecs.wukong.prclass.occupancy;

import edu.uci.eecs.wukong.prclass.occupancy.HMMBasedLearningExtension;
import edu.uci.eecs.wukong.prclass.occupancy.ODProgressionExtension;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

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

	public static void main(String[] arg) {
		OCAlgorithmTest test = new OCAlgorithmTest();
		List<List<Byte>> observations = new ArrayList<List<Byte>>();
		
		for (int i = 19; i<=26; i++) {
			String path = "nov-" + i + "-node-1.txt";
			List<PIRData> pir = test.loadData(path);
			List<Byte> observation = test.generateObservation(pir, 15);
			test.printObservation(observation);
			observations.add(observation);
		}
		
		test.hammingExtension.loadData(observations);
	}
}
