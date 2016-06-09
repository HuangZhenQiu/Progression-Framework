package edu.uci.eecs.wukong.edge.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 9007)
public class ICSDemoTVPrClass extends PipelinePrClass {

	@WuProperty(name = "tv_onoff", id = 0, type = PropertyType.Output)
	private short tv_onoff;
	
	@WuProperty(name = "tv_mute", id = 1, type = PropertyType.Output)
	private short tv_mute;
	
	public ICSDemoTVPrClass(PrClassMetrics metrics) {
		super("ICSDemoTVPrClass", metrics);
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

	public short getTVOnOff() {
		return tv_onoff;
	}
	
	public void setTVOnOff(short tv_onoff) {
		if(tv_onoff > 1 || tv_onoff < 0) return;
		this.support.firePropertyChange("tv_onoff", this.tv_onoff, tv_onoff);
		this.tv_onoff = tv_onoff;
	}
	
	public short getTVMute(){
		return tv_mute;
	}
	public void setTVMute(short tv_mute) {
		if(tv_mute > 1 || tv_mute < 0) return;
		this.support.firePropertyChange("tv_mute", this.tv_mute, tv_mute);
		this.tv_mute = tv_mute;
	}
}
