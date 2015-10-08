package edu.uci.eecs.wukong.framework.prclass;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;

import edu.uci.eecs.wukong.prclass.switcher.SwitchPrClass;
import junit.framework.TestCase;

public class PrClassTest extends TestCase {
	private static final String TEST_PROPERTY = "threshold";
	
	@Test
	public void testPropertyUpdate() {
		SwitchPrClass plugin = new SwitchPrClass();
		plugin.addPropertyChangeListener(TEST_PROPERTY, new PropertyChangeListener() {
			 public void propertyChange(PropertyChangeEvent evt) {
				 System.out.println(evt.getPropertyName());
			 }
		});
		plugin.setThreshold(0.2);
	}
}