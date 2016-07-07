package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

public abstract class SequenceProcessExtension<T extends EdgePrClass> extends AbstractExtension<T> {

	public SequenceProcessExtension(T prClass) {
		super(prClass);
	}

}
