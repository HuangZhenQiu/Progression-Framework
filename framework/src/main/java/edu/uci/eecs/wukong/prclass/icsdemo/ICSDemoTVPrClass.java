package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class ICSDemoTVPrClass extends PrClass {

	@WuProperty(name = "tv_onoff", id = 0, type = PropertyType.Output)
	private short tv_onoff;
	
	@WuProperty(name = "tv_mute", id = 1, type = PropertyType.Output)
	private short tv_mute;
	
	public ICSDemoTVPrClass() {
		super("ICSDemoTVPrClass");
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

	public short setTVStatus() {
		return tv_onoff;
	}

	public void setTVState(String tv_onoff) {
//		this.tv_onoff = tv_onoff;
	}
}
