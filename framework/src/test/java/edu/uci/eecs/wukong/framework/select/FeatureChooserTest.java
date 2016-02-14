package edu.uci.eecs.wukong.framework.select;
 
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.operator.AbstractOperator;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.operator.MaxOperator;
import edu.uci.eecs.wukong.framework.operator.MinOperator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

import junit.framework.JUnit4TestAdapter;

public class FeatureChooserTest {
	private Map<AbstractOperator<?>, Map<NPP, Integer>> operators;
	private BufferManager manager;
	private PipelinePrClass prClass;
	private NPP nppA = new NPP(1l, (byte)1, (byte)1);
	private NPP nppB = new NPP(2l, (byte)2, (byte)2);
	private NPP nppC = new NPP(3l, (byte)3, (byte)3);
	private MinOperator<Float> minOperator = new MinOperator<Float>(Float.class);
	private MaxOperator<Float> maxOperator = new MaxOperator<Float>(Float.class);
	private AverageOperator<Float> avrOperator = new AverageOperator<Float>(Float.class);
	private List<DataPoint<Object>> dataListA;
	private List<DataPoint<Object>> dataListB;
	private List<DataPoint<Object>> dataListC;
	
	
	@Before
	public void setup() {
		operators = new HashMap<AbstractOperator<?>, Map<NPP, Integer>> ();
		dataListA = new ArrayList<DataPoint<Object>> ();
		dataListB = new ArrayList<DataPoint<Object>> ();
		dataListC = new ArrayList<DataPoint<Object>> ();
		
		dataListA.add(new DataPoint<Object>(nppA, 0, 1.0f));
		dataListA.add(new DataPoint<Object>(nppA, 0, 2.0f));
		dataListA.add(new DataPoint<Object>(nppA, 0, 3.0f));
		
		dataListB.add(new DataPoint<Object>(nppB, 0, 1.0f));
		dataListB.add(new DataPoint<Object>(nppB, 0, 2.0f));
		dataListB.add(new DataPoint<Object>(nppB, 0, 3.0f));
		
		dataListC.add(new DataPoint<Object>(nppC, 0, 1.0f));
		dataListC.add(new DataPoint<Object>(nppC, 0, 2.0f));
		dataListC.add(new DataPoint<Object>(nppC, 0, 3.0f));
		
		// Prepare operators
		minOperator.addDataSource(1, 1);
		maxOperator.addDataSource(2, 1);
		avrOperator.addDataSource(3, 1);
		// Prepare NPP map
		Map<NPP, Integer> minMap = new HashMap<NPP, Integer>();
		minMap.put(nppA, 1);
		Map<NPP, Integer> maxMap = new HashMap<NPP, Integer>();
		maxMap.put(nppB, 2);
		Map<NPP, Integer> avrMap = new HashMap<NPP, Integer>(); 
		avrMap.put(nppC, 3);
		
		// prepare operators;
		operators.put(minOperator, minMap);
		operators.put(maxOperator, maxMap);
		operators.put(avrOperator, avrMap);
		
		manager = Mockito.mock(BufferManager.class);
		prClass = Mockito.mock(PipelinePrClass.class);
	}
	
	@Test
	public void testChooseFeature() {
		Mockito.when(manager.getData(nppA, 1)).thenReturn(dataListA);
		Mockito.when(manager.getData(nppB, 2)).thenReturn(dataListB);
		Mockito.when(manager.getData(nppC, 3)).thenReturn(dataListC);
		FeatureChooser featureChooser = new FeatureChooser(prClass, manager, operators); 
		List<Object> features = featureChooser.choose();
		assertEquals(3, features.size());
		assertEquals(true, features.contains(1.0f));
		assertEquals(true, features.contains(2.0f));
		assertEquals(true, features.contains(3.0f));
	}
	
	public static junit.framework.Test suit() {
		return new JUnit4TestAdapter(FeatureChooserTest.class);
	}
}