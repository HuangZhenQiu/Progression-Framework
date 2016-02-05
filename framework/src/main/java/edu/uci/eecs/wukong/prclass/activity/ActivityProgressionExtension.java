package edu.uci.eecs.wukong.prclass.activity;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;

public class ActivityProgressionExtension extends AbstractProgressionExtension<ActivityRecgonitionPrClass>
	implements Executable<Short> {

	public ActivityProgressionExtension(ActivityRecgonitionPrClass plugin) {
		super(plugin);
	}

	@Override
	public void execute(List<Short> features, ExecutionContext context) {
		
	}

}
