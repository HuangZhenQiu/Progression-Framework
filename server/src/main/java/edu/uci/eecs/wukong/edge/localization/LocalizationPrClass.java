package edu.uci.eecs.wukong.edge.localization;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.property.Location;
import edu.uci.eecs.wukong.framework.property.Response;

@WuClass(id = 10115)
public class LocalizationPrClass extends PipelinePrClass{
	@WuProperty(id = 0, name = "signal", type = PropertyType.Input, dtype = DataType.Channel)
	private Location signal;
	@WuProperty(id = 1, name = "particleCount", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short particleCount;
	@WuProperty(id = 2, name = "movNoise", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short movNoise;
	@WuProperty(id = 3, name = "rotNoise", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short rotNoise;
	@WuProperty(id = 4, name = "senseNoise", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short senseNoise;
	@WuProperty(id = 5, name = "maxr", type = PropertyType.Input, dtype = DataType.Init_Value)
	private short maxr;
	@WuProperty(id = 6, name = "output", type = PropertyType.Output)
	private Response output;
	private double[] sensors = {1.0, 1.0, 1.0};
	
	public LocalizationPrClass(PrClassMetrics metrics) {
		super("LocalizationPrClass", metrics);
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new LocalizationExecutionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> topics = new ArrayList<String>();
		topics.add("Map");
		return topics;
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

	public Response getOutput() {
		return output;
	}

	public void setOutput(Response output) {
		this.support.firePropertyChange("output", this.output, output);
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
