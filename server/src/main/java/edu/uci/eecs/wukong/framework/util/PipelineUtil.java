package edu.uci.eecs.wukong.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.prclass.SimplePrClass;

public class PipelineUtil {
	public final static int DEFAULT_INTERVAL = 5;

	public static float getIntervalFromMethodAnnotation(TimerExecutable object) {
		try {
			Method method = object.getClass().getDeclaredMethod("execute");
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(WuTimer.class)) {
					WuTimer timer = (WuTimer) annotation;
					return timer.interval();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return DEFAULT_INTERVAL;
	}
	
	public static float getIntervalFromClassAnnotation(SimplePrClass prClass) {
		try {
			Method method = prClass.getClass().getDeclaredMethod("update");
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(WuTimer.class)) {
					WuTimer timer = (WuTimer) annotation;
					return timer.interval();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return DEFAULT_INTERVAL;
	}
}
