package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.prclass.icsdemo.ContextProgressionExtension;

@WuClass(id = 10003)
public class ICSDemoHuePrClass extends PrClass {
	
	@WuProperty(name = "floorlamp_color_lux", id = 1, type = PropertyType.Output)
	private short floorlamp_color_lux;
	@WuProperty(name = "bloom_color_lux", id = 2, type = PropertyType.Output)
	private short bloom_color_lux;
	@WuProperty(name = "go_color_lux", id = 3, type = PropertyType.Output)
	private short go_color_lux;
	@WuProperty(name = "strip_color_lux", id = 4, type = PropertyType.Output)
	private short strip_color_lux;
	

	public ICSDemoHuePrClass() {
		super("ICSDemoHuePrClass");
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

	public short getFloorLamp() {
		return floorlamp_color_lux;
	}

	public void setFloorLamp(short light) {
		this.floorlamp_color_lux = light;
	}

	public short getBloom() {
		return bloom_color_lux;
	}

	public void setBloom(short light) {
		this.bloom_color_lux = light;
	}

	public short getGo() {
		return go_color_lux;
	}

	public void setGo(short light) {
		this.go_color_lux = light;
	}

	public short getStrip() {
		return strip_color_lux;
	}

	public void setStrip(short light) {
		this.strip_color_lux = light;
	}
}
