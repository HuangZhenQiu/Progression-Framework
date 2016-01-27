package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.extension.SequenceProcessExtension;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;


/**
 * An sequential data processing extension point.
 * 
 * 
 * 
 * @author peter
 *
 */
public class SequenceProcessExtensionPoint extends ExtensionPoint<SequenceProcessExtension<? extends PipelinePrClass>> {

	public SequenceProcessExtensionPoint(Pipeline pipeline) {
		super(pipeline);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
