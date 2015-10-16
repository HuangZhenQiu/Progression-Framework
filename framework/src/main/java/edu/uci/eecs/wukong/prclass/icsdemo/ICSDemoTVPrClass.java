package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class ICSDemoTVPrClass extends PrClass {

	@WuProperty(name = "tv_onoff", id = 1, type = PropertyType.Output)
	private short tv_onoff;
	
	@WuProperty(name = "tv_mute", id = 2, type = PropertyType.Output)
	private short tv_mute;
	
	@WuProperty(name = "tv_unmute", id = 3, type = PropertyType.Output)
	private short tv_unmute;
	
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

	public short getTVOnOff() {
		return tv_onoff;
	}

	public void setTVOnOff(short tv_onoff) {
		this.tv_onoff = tv_onoff;
	}

	public short getTVMute() {
		return tv_mute;
	}

	public void setTVMute(short tv_mute) {
		this.tv_mute = tv_mute;
	}

	public short getTVUnmute() {
		return tv_unmute;
	}

	public void setTVUnmute(short tv_unmute) {
		this.tv_unmute = tv_unmute;
	}

}
