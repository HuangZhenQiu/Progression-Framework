package edu.uci.eecs.wukong.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface Input {
	// Defines the name of the input property, if it is empty system use the property name directly
	String name() default "";
	
	// Defines the how long to get data from the buffer, if realtime = true, it is useless.
	int interval() default 5;
	
	// Defines whether the data is for buffer (false) or channel (true).
	boolean realtime() default false;
}
