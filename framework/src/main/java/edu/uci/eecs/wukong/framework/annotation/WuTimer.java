package edu.uci.eecs.wukong.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/**
 * A progression extension can be triggered by timer. By default, the execute method
 * is triggered by timer every 10 seconds. But PrClass developer can customize the 
 * internal by using the annotation.
 */
public @interface WuTimer {
	// Timer period in seconds for TimerExecutable interface
	int internal() default 10;
}
