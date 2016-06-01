package edu.uci.eecs.wukong.framework.util;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class OperatorUtil {
	
	/**
	 * Determine whether one vector is the range of another vector
	 * 
	 * The two vectors should have equal length.
	 * 
	 * 
	 * @param template one of two lists being compared
	 * @param scroll one of two lists being compared
	 * @param d two list match if their distance is less than d
	 * @return
	 */
	public static boolean inRange(List<Float> template, List<Float> scroll, double d) {
		if (template == null || scroll == null || template.size() != scroll.size()) {
			return false;
		}
		
		for (int i =0; i< template.size(); i++) {
			if (Math.abs(template.get(i) - scroll.get(i)) > d) {
				return false;
			}
		}
		
		return true;
	}
	
	public static double sum(List<Double> data) {
		double sum = 0;
		for (int i = 0; i < data.size(); i++) {
			sum += data.get(i);
		}
		
		return sum;
	}
}
