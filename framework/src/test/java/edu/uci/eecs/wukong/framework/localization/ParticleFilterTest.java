package edu.uci.eecs.wukong.framework.localization;

import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;

public class ParticleFilterTest extends TestCase{

	@Test
	public void test() {
		boolean[][] values = new boolean[100][100];
		for (int i = 0; i < values.length; i ++) {
			values[i] = new boolean[100];
			Arrays.fill(values[i], true);
		}
		double[] sensors = {1.0, 1.0, 1.0};
		Map map = new Map(values, 99, 99);
		ParticleFilter filter = new ParticleFilter(map, 1000, sensors, 2, 2, 2, 1);
		filter.step(sensors, 1, 0.2, 2.0);
	}
}
