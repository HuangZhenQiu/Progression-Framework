package edu.uci.eecs.wukong.prclass.occupancy;

import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.prclass.occupancy.OccupancyDetection;
import edu.uci.eecs.wukong.prclass.occupancy.OccupancyDetection.Occupancy;

public class HMMBasedProgressionExtension extends AbstractProgressionExtension<OccupancyDetection> 
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
