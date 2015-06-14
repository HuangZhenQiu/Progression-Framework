package edu.uci.eecs.wukong.framework.wkpf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.wkpf.Model.WuClass;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuObject;
import edu.uci.eecs.wukong.framework.manager.PluginManager;

public class WKPF implements WKPFMessageListener{
	private MPTN mptn;
	private String location;
	private List<WuClass> wuclasses;
	private List<WuObject> wuobjects;
	private PluginManager pluginManager;

	public WKPF(PluginManager pluginManager) {
		this.wuclasses = new ArrayList<WuClass>();
		this.wuobjects = new ArrayList<WuObject>();
		this.mptn = new MPTN();
		this.mptn.addWKPFMessageListener(this);
		this.pluginManager = pluginManager;
	}
	
	public void enterLearn() throws IOException {
		
	}
	
	public void addWuClass(WuClass wuClass) {
		this.wuclasses.add(wuClass);
	}
	
	public void addWuObject(WuObject wuObject) {
		this.wuobjects.add(wuObject);
	}

	public void onWKPFRemoteProgram(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFGetWuClassList(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFGetWuObjectList(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFReadProperty(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFWriteProperty(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFMonitoredData(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFSetLocation(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFGetLocation(byte[] message) {
		mptn.send();
	}
}
