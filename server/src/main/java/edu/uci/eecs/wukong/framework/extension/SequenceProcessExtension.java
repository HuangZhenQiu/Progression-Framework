package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public abstract class SequenceProcessExtension<T extends PipelinePrClass> extends AbstractExtension<T> {

	public SequenceProcessExtension(T prClass) {
		super(prClass);
	}

}
