package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

@WuClass(id = 9005)
public class ICSDemoAromaPrClass extends PrClass {

	@WuProperty(name = "aroma_onoff", id = 1, type = PropertyType.Output)
	private short aroma_onoff;
	
	public ICSDemoAromaPrClass() {
		super("ICSDemoAromaPrClass");
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

	public short getAromaOnOff() {
		return aroma_onoff;
	}

	public void setAromaOnOff(short aroma_onoff) {
		if(aroma_onoff > 1 || aroma_onoff < 0) return;
		this.support.firePropertyChange("aroma_onoff", this.aroma_onoff, aroma_onoff);
		this.aroma_onoff = aroma_onoff;
	}

}
