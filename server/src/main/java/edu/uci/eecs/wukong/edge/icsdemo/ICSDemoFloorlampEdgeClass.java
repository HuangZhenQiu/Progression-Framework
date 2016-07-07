package edu.uci.eecs.wukong.edge.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 9000)
public class ICSDemoFloorlampEdgeClass extends EdgePrClass {
	
	@WuProperty(name = "hue", id = 0, type = PropertyType.Output, dtype = DataType.Short)
	private short hue;
	@WuProperty(name = "saturation", id = 1, type = PropertyType.Output, dtype = DataType.Short)
	private short saturation;
	@WuProperty(name = "brightness", id = 2, type = PropertyType.Output, dtype = DataType.Short)
	private short brightness;
	@WuProperty(name = "x", id = 3, type = PropertyType.Output, dtype = DataType.Short)
	private short x;
	@WuProperty(name = "y", id = 4, type = PropertyType.Output, dtype = DataType.Short)
	private short y;
	@WuProperty(name = "on_off", id = 5, type = PropertyType.Output, dtype = DataType.Short)
	private short on_off;
	
	private ICSDemoHueEdgeClass hueObj;

	public ICSDemoFloorlampEdgeClass(PrClassMetrics metrics) {
		super("ICSDemoFloorlampPrClass", metrics);
		hueObj = new ICSDemoHueEdgeClass("ICSDemoFloorlampPrClass", "LCT001");
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
	
	public void setColorFromRGB(short red, short green, short blue)
	{
		hueObj.setColorFromRGB(red, green, blue);
		this.setX(hueObj.getX());
		this.setY(hueObj.getY());
		this.setHue(hueObj.getHue());
		this.setSaturation(hueObj.getSaturation());
//		this.setBrightness(hueObj.getBrightness());
	}
	
	public short getX(){
		return x;
	}
	
	public void setX(short x){
		if(x > 10000 || x < 0) return;
		this.support.firePropertyChange("x", this.x, x);
		this.x = x;
	}
	
	public short getY(){
		return y;
	}
	
	public void setY(short y){
		if(y > 10000 || y < 0) return;
		this.support.firePropertyChange("y", this.y, y);
		this.y = y;
	}

	public short getHue() {
		return hue;
	}

	public void setHue(short hue) {
		if(hue > 255 || hue < 0) return;
		this.support.firePropertyChange("hue", this.hue, hue);
		this.hue = hue;
	}

	public short getSaturation() {
		return saturation;
	}

	public void setSaturation(short saturation) {
		if(saturation > 255 || saturation < 0) return;
		this.support.firePropertyChange("saturation", this.saturation, saturation);
		this.saturation = saturation;
	}

	public short getBrightness() {
		return brightness;
	}

	public void setBrightness(short brightness) {
		if(brightness > 255 || brightness < 0) return;
		this.support.firePropertyChange("brightness", this.brightness, brightness);
		this.brightness = brightness;
	}
	
	public short getOnOff(){
		return on_off;
	}
	public void setOnOff(short on_off){
		if(on_off > 1 || on_off < 0) return;
		this.support.firePropertyChange("on_off", this.on_off, on_off);
		this.on_off = on_off;
	}
}
