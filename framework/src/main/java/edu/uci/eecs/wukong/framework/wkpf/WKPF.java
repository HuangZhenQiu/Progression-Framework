package edu.uci.eecs.wukong.framework.wkpf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.manager.PluginManager;

public class WKPF implements WKPFMessageListener{
	private MPTN mptn;
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
	

	public boolean onWKPFRemoteProgram(byte[] message) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<String> onWKPFGetWuClassList() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> onWKPFGetWuObjectList() {
		// TODO Auto-generated method stub
		return null;
	}

	public int onWKPFReadProperty(int objectId, int portId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int onWKPFWriteProperty(int objectId, int portId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean onWKPFMonitoredData(int deviceId, int portId) {
		// TODO Auto-generated method stub
		return false;
	}

}
