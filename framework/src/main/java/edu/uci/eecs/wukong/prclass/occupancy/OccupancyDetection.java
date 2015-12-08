package edu.uci.eecs.wukong.prclass.occupancy;

import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;

import com.google.common.annotations.VisibleForTesting;
@WuClass(id = 10113)
public class OccupancyDetection extends PrClass {
	@WuProperty(name = "pir", id = 1, type = PropertyType.Input, dtype = DataType.Buffer)
	private byte pir;
	@WuProperty(name = "days", id = 2, type = PropertyType.Input, dtype = DataType.Init_Value)
	private short days;
	@WuProperty(name = "topK", id = 3, type = PropertyType.Input, dtype = DataType.Init_Value)
	private short topK;
	@WuProperty(name = "interval", id = 4, type = PropertyType.Input, dtype = DataType.Init_Value)
	private short interval;
	@WuProperty(name = "occupancy", id = 4, type = PropertyType.Output)
	private boolean occupancy;
	
	public enum Occupancy {
		YES, NO;
		
		public ObservationDiscrete<Occupancy> observation() {
			return new ObservationDiscrete<Occupancy>(this);
		}
	};
	
	@VisibleForTesting
	public OccupancyDetection(short days, short topK, short interval) {
		super("OccupancyDetection", true);
		this.days = days;
		this.topK = topK;
		this.interval = interval;
	}

	public OccupancyDetection() {
		super("OccupancyDetection");
	}

	@Override
	public List<Extension> registerExtension() {
		return null;
	}

	@Override
	public List<String> registerContext() {
		return null;
	}

	public byte getPir() {
		return pir;
	}

	public void setPir(byte pir) {
		this.pir = pir;
	}

	public short getTopK() {
		return topK;
	}

	public void setTopK(short topK) {
		this.topK = topK;
	}
	
	public void setOccupancy(boolean occupancy) {
		this.support.firePropertyChange("occupancy", this.occupancy, occupancy);
		this.occupancy = occupancy;
	}

	public short getInterval() {
		return interval;
	}

	public void setInterval(short interval) {
		this.interval = interval;
	}
	
	public short getDays() {
		return days;
	}
}
