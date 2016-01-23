package edu.uci.eecs.wukong.framework.util;

import java.lang.annotation.Annotation;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;

public class PipelineUtil {
	public final static int DEFAULT_INTERVAL = 5;
	
	public static int getIntervalFromAnnotation(Object object) {
		Annotation[] annotations = object.getClass().getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(WuTimer.class)) {
				WuTimer timer = (WuTimer) annotation;
				return timer.interval();
			}
		}
		
		return DEFAULT_INTERVAL;
	}
}
