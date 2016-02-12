package edu.uci.eecs.wukong.prclass.loadtester;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.property.Response;
import edu.uci.eecs.wukong.framework.property.Location;

@WuClass(id = 20114)
public class LocalizationLoadTester extends PipelinePrClass {
	@WuProperty(id = 0, name = "response", type = PropertyType.Input, dtype = DataType.Channel)
	private Response response;
	@WuProperty(id = 1, name = "location", type = PropertyType.Output)
	private Location location;

	public LocalizationLoadTester() {
		super("LoadTester", false);
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new LocalizationLoadTestProgressionExtension(this));
		return null;
	}

	@Override
	public List<String> registerContext() {
		List<String> context = new ArrayList<String> ();
		return context;
	}
	
	public void setLocation(Location location) {
		this.support.firePropertyChange("location", this.location, location);
		this.location = location;
	}
}
