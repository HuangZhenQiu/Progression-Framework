package edu.uci.eecs.wukong.framework.model;

import java.util.HashMap;
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
		wuClassMap.put(1005, value)
	}
	
	private SensorType lookUpSensorType(Integer wuClassId) {
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
