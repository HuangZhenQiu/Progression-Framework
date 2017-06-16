package edu.uci.eecs.wukong.framework.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sensor Type defines category of sensors. It is used in 
 * 
 * 
 * @author peter
 *
 */
public enum SensorType {
	Default("Default"),
	LightSensor("LightSensor"),
	PIRSensor("PIRSensor"),
	TemperatureSensor("TemperatureSensor"),
	HumiditySensor("HumiditySensor"),
	MagneticSensor("MagneticSensor"),
	PressureSensor("PressureSensor"),
	GhSensor("GhSensor"),
	IrSensor("IrSensor"),
	UltrasoundSensor("UltrasoundSensor"),
	SoundSensor("SoundSensor"),
	TouchSensor("TouchSensor");

	private final String name;
	private static Map<Integer, SensorType> wuClassMap = new HashMap<Integer, SensorType>();
	
	static {
		wuClassMap.put(1001, LightSensor);
		wuClassMap.put(1003, PIRSensor);
		wuClassMap.put(1013, TemperatureSensor);
		wuClassMap.put(1005, HumiditySensor);
		wuClassMap.put(1007, MagneticSensor);
		wuClassMap.put(1008, PressureSensor);
		wuClassMap.put(1009, GhSensor);
		wuClassMap.put(1010, IrSensor);
		wuClassMap.put(1011, UltrasoundSensor);
		wuClassMap.put(1014, SoundSensor);
		wuClassMap.put(1015, TouchSensor);
	}
	
	public List<SensorType> getAllTypes() {
		return new ArrayList(wuClassMap.values());
	}
	
	public SensorType lookUpSensorType(Integer wuClassId) {
		if (wuClassMap.containsKey(wuClassId)) {
			return wuClassMap.get(wuClassId);
		}
		
		return Default;
	}
	
	private SensorType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
}
