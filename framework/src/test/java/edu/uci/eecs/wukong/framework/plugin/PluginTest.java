package edu.uci.eecs.wukong.framework.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;

import edu.uci.eecs.wukong.plugin.switcher.SwitchPlugin;
import junit.framework.TestCase;

public class PluginTest extends TestCase {
	private static final String APP_ID = "1";
	private static final String TEST_PROPERTY = "threshold";
	
	@Test
	public void testPropertyUpdate() {
		SwitchPlugin plugin = new SwitchPlugin(APP_ID);
		plugin.addPropertyChangeListener(TEST_PROPERTY, new PropertyChangeListener() {
			 public void propertyChange(PropertyChangeEvent evt) {
				 System.out.println(evt.getPropertyName());
			 }
		});
		plugin.setThreshold(0.2);
	}
}
