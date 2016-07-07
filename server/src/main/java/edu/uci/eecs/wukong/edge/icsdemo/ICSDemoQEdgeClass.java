package edu.uci.eecs.wukong.edge.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 9009)
public class ICSDemoQEdgeClass extends EdgePrClass {

	@WuProperty(name = "question", id = 0, type = PropertyType.Output)
	private boolean question;
	
	public ICSDemoQEdgeClass(PrClassMetrics metrics) {
		super("ICSDemoQPrClass", metrics);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new ContextExecutionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> topics = new ArrayList<String> ();
		topics.add(ICSContext.TOPIC);
		return topics;
	}

	public boolean getQuestion() {
		return question;
	}

	public void setQuestion(boolean question) {
		this.support.firePropertyChange("question", this.question, question);
		this.question = question;
	}

}
