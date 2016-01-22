package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

@WuClass(id = 9009)
public class ICSDemoQPrClass extends PipelinePrClass {

	@WuProperty(name = "question", id = 0, type = PropertyType.Output)
	private boolean question;
	
	public ICSDemoQPrClass() {
		super("ICSDemoQPrClass");
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new ContextProgressionExtension(this));
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
