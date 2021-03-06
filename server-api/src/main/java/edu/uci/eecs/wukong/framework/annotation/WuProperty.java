package edu.uci.eecs.wukong.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.SensorType;
import edu.uci.eecs.wukong.framework.model.WKPFMessageType;
import edu.uci.eecs.wukong.framework.model.DataType;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface WuProperty {
	// Defines the name of the input property, if it is empty system use the property name directly
	String name() default "";
	
	// Define the id of the property, it has to be unique for each property
	byte id() default 0;
	
	// Define the type of property, either input or output
	PropertyType type() default PropertyType.Input;
	
	// Define the data type of the input property, either buffer or channel
	DataType dtype() default DataType.Channel;
	
	// Define the message type of the input global channel, only used when data type is global channel
	WKPFMessageType mtype() default WKPFMessageType.GetDeviceStatusReturn;
	
	// Define the category of sensor, only used when data type is SystemBuffer
	SensorType stype() default SensorType.Default;
	
	// Defines the size of buffer, only used for buffer typed input property
	int capacity() default 1000;
	
	// Defines the how long to build the time index in milliseconds, only used for buffer typed input property
	int interval() default 1000;
	
	// Defines the size of time index, only used for buffer typed input property
	int timeUnit() default 10;
}
