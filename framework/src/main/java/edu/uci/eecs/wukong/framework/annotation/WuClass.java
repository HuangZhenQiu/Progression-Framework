package edu.uci.eecs.wukong.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface WuClass {
	// Wuclass number, should be synchronized with the number defined in standard library.
	short id() default 0;
	
	// Interested topics of the WuClass, for example "User, Location, Model".
	String topics() default "";
}
