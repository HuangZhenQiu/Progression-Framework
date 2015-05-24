package edu.uci.eecs.wukong.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface Output {
	// Defines the name of the input property, if it is empty system use the property name directly
	String name() default "";
	
	// Defines the how many seconds to wait before sending updated value.
	int delay() default 0;
}
