package edu.uci.eecs.wukong.prclass.occupancy;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.prclass.occupancy.OccupancyDetection;
import edu.uci.eecs.wukong.prclass.occupancy.OccupancyDetection.Occupancy;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import com.google.common.annotations.VisibleForTesting;

/**
 * Use stationary Hidden Markov Model to model the occupancy observations as a first order Hidden Markov Chain.
 * We use the jahhm lib to train the model from observation sequences.
 * 
 * The jahhm firstly use k-means to cluster the observations. Then use 
 *
 */
public class HMMBasedLearningExtension extends LearningExtension<Byte> {
	// Number of hidden states for a day
	private int states;
	private OpdfDiscreteFactory<Occupancy> factory;
	private BaumWelchLearner bwl = new BaumWelchLearner();
	private Hmm<ObservationDiscrete<Occupancy>> hmm;
	
	@VisibleForTesting
	public HMMBasedLearningExtension(List<List<ObservationDiscrete<Occupancy>>> sequences, OccupancyDetection prClass) {
		super(prClass);
		this.factory = new OpdfDiscreteFactory<Occupancy>(Occupancy.class);
		this.hmm = new Hmm<ObservationDiscrete<Occupancy>>(5, factory);
		this.hmm = bwl.learn(this.hmm, sequences);
	}
	
	@VisibleForTesting
	public double predict(List<ObservationDiscrete<Occupancy>> subsequence) {
		return hmm.lnProbability(subsequence);
	}
	
	public HMMBasedLearningExtension(OccupancyDetection plugin) {
		super(plugin);
		this.states = 24 * 60 / plugin.getInterval();
		this.factory = new OpdfDiscreteFactory<Occupancy>(Occupancy.class);
		this.hmm = new Hmm<ObservationDiscrete<Occupancy>>(states, factory);
		
	}

	@Override
	public void apply(List<Byte> data, ExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object train() throws Exception {
		
		return null;
	}
}
