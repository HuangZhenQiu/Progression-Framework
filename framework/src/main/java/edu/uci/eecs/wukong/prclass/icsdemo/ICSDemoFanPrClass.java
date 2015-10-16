package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

@WuClass(id = 10003)
public class ICSDemoFanPrClass extends PrClass {

	@WuProperty(name = "fan_speed", id = 1, type = PropertyType.Output)
	private short fan_speed;
	@WuProperty(name = "fan_rotation", id = 2, type = PropertyType.Output)
	private short fan_rotation;
	
	public ICSDemoFanPrClass() {
		super("ICSDemoFanPrClass");
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
	
	public short getFanSpeed() {
		return fan_speed;
	}

	public void setFanSpeed(short fan_speed) {
		this.fan_speed = fan_speed;
	}
	
	public short getFanRotation() {
		return fan_rotation;
	}

	public void setFanRotation(short fan_rotation) {
		this.fan_rotation = fan_rotation;
	}

}
