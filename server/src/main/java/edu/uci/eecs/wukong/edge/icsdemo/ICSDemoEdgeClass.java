package edu.uci.eecs.wukong.edge.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.edge.icsdemo.ContextExecutionExtension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 10003)
public class ICSDemoEdgeClass extends EdgePrClass {
	
	@WuProperty(name = "uid", id = 0, type = PropertyType.Input, dtype = DataType.Channel)
	private short uid;
	@WuProperty(name = "music", id = 1, type = PropertyType.Output)
	private short music;
	@WuProperty(name = "fan", id = 2, type = PropertyType.Output)
	private short fan;
	@WuProperty(name = "light", id = 3, type = PropertyType.Output)
	private short light;
	

	public ICSDemoEdgeClass(PrClassMetrics metrics) {
		super("ICSDemoPrClass", metrics);
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

	public short getMusic() {
		return music;
	}

	public void setMusic(short music) {
		this.music = music;
	}

	public short getFan() {
		return fan;
	}

	public void setFan(short fan) {
		this.fan = fan;
	}

	public short getLight() {
		return light;
	}

	public void setLight(short light) {
		this.light = light;
	}
}
