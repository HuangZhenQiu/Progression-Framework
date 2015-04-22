package edu.uci.eecs.wukong.framework.annotation;

public @interface Input {
	String name() default "";
	int interval() default 5;
}
