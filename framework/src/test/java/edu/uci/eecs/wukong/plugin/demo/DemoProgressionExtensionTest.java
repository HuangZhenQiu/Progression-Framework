package edu.uci.eecs.wukong.plugin.demo;

import junit.framework.TestCase;
import org.junit.Test;
import edu.uci.eecs.wukong.framework.context.DemoContext;

public class DemoProgressionExtensionTest extends TestCase {

	@Test
	public void testEnterRoom() {
		DemoProgressionExtension extension = new DemoProgressionExtension(null);
		DemoContext context1 =  new DemoContext(0,0,0,0,0,0);
		context1.setTimestamp(1000000);
		DemoContext context2 =  new DemoContext(0,1,0,0,0,0);
		context2.setTimestamp(1000001);
		//assertEquals(0,  extension.execute(context1).size());
		//assertEquals(4,  extension.execute(context2).size());
	}
	
	@Test
	public void testStayAtKichenNotLongEnough() {
		DemoProgressionExtension extension = new DemoProgressionExtension(null);
		DemoContext context1 =  new DemoContext(0,0,3,0,0,0);
		context1.setTimestamp(1000000);
		DemoContext context2 =  new DemoContext(0,0,3,0,0,0);
		context2.setTimestamp(1000002);
		//assertEquals(0,  extension.execute(context1).size());
		//assertEquals(0,  extension.execute(context2).size());
	}
	
	@Test
	public void testStayAtKichen() {
		DemoProgressionExtension extension = new DemoProgressionExtension(null);
		DemoContext context1 =  new DemoContext(0,0,3,0,0,0);
		context1.setTimestamp(1000000);
		DemoContext context2 =  new DemoContext(0,0,3,0,0,0);
		context2.setTimestamp(1000006);
		//assertEquals(0,  extension.execute(context1).size());
		//assertEquals(4,  extension.execute(context2).size());
	}
}
