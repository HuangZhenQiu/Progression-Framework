package edu.uci.eecs.wukong.prclass.localization;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

@WuClass(id = 10114)
public class LocalizationPrClass extends PipelinePrClass{
	@WuProperty(id = 0, name = "signalX", type = PropertyType.Input, dtype = DataType.Channel)
	private short signalX;
	@WuProperty(id = 1, name = "signalY", type = PropertyType.Input, dtype = DataType.Channel)
	private short signalY;
	@WuProperty(id = 2, name = "signalZ", type = PropertyType.Input, dtype = DataType.Channel)
	private short signalZ;
	@WuProperty(id = 3, name = "particleCount", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short particleCount;
	@WuProperty(id = 4, name = "movNoise", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short movNoise;
	@WuProperty(id = 5, name = "rotNoise", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short rotNoise;
	@WuProperty(id = 6, name = "senseNoise", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short senseNoise;
	@WuProperty(id = 7, name = "maxr", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short maxr;
	@WuProperty(id = 8, name = "output", type = PropertyType.Output)
	private short output;
	private double[] sensors = {1.0, 1.0, 1.0};
	

	protected LocalizationPrClass() {
		super("LocalizationPrClass");
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new LocalizationProgressionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> topics = new ArrayList<String>();
		topics.add("Map");
		return topics;
	}

	public short getSignalX() {
		return signalX;
	}

	public void setSignalX(short signalX) {
		this.signalX = signalX;
	}

	public short getSignalY() {
		return signalY;
	}

	public void setSignalY(short signalY) {
		this.signalY = signalY;
	}

	public short getSignalZ() {
		return signalZ;
	}

	public void setSignalZ(short signalZ) {
		this.signalZ = signalZ;
	}

	public short getParticleCount() {
		return particleCount;
	}

	public void setParticleCount(byte particleCount) {
		this.particleCount = particleCount;
	}

	public short getMovNoise() {
		return movNoise;
	}

	public void setMovNoise(short movNoise) {
		this.movNoise = movNoise;
	}

	public short getRotNoise() {
		return rotNoise;
	}

	public void setRotNoise(short rotNoise) {
		this.rotNoise = rotNoise;
	}

	public short getMaxr() {
		return maxr;
	}

	public void setMaxr(short maxr) {
		this.maxr = maxr;
	}

	public short getOutput() {
		return output;
	}

	public void setOutput(short output) {
		this.output = output;
	}
	
	public short getSenseNoise() {
		return senseNoise;
	}

	public void setSenseNoise(short senseNoise) {
		this.senseNoise = senseNoise;
	}

	public double[] getSensors() {
		return sensors;
	}
}
