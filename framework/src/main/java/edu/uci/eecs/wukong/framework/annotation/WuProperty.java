package edu.uci.eecs.wukong.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface WuProperty {
	// Defines the name of the input property, if it is empty system use the property name directly
	String name() default "";
	
	// Define the type of property, either input or output
	String type() default "";
	
	// Define the data type of the output property, either buffer or channel
	String dtype() default "";
	
	// Define the id of the property
	int id();
}
