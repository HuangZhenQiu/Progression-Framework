package edu.uci.eecs.wukong.edge.occupancy;

import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import edu.uci.eecs.wukong.edge.occupancy.OccupancyDetection;
import edu.uci.eecs.wukong.edge.occupancy.OccupancyDetection.Occupancy;
import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;

public class HMMBasedProgressionExtension extends AbstractExecutionExtension<OccupancyDetection> 
	implements Executable, Activatable{
	private boolean isActivated;
	private Hmm<ObservationDiscrete<Occupancy>> hmm;
	
	public HMMBasedProgressionExtension(OccupancyDetection plugin) {
		super(plugin);
		this.isActivated = false;
	}

	@Override
	public void execute(List data, ExecutionContext context) {
		if (isActivated) {
			
		}
	}

	@Override
	public void activate(Object model) {
		hmm = (Hmm<ObservationDiscrete<Occupancy>>) model;
	}

}
