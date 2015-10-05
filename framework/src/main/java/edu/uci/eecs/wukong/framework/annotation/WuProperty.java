package edu.uci.eecs.wukong.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.DataType;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface WuProperty {
	// Defines the name of the input property, if it is empty system use the property name directly
	String name() default "";
	
	// Define the type of property, either input or output
	PropertyType type() default PropertyType.Input;
	
	// Define the data type of the output property, either buffer or channel
	DataType dtype() default DataType.Buffer;
	
	// Define the id of the property
	byte id() default 0;
}
