package edu.uci.eecs.wukong.prclass.smarthue;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.regression.LogisticRegression;

public class SmartHueProgressionExtension extends AbstractProgressionExtension implements
	Activatable, Executable {
	private LogisticRegression regression = null;

	public SmartHueProgressionExtension(PrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List data, ExecutionContext context) {
		if (regression != null) {
		}
	}

	@Override
	public void activate(Object model) {
		regression = (LogisticRegression) model;
	}
}
