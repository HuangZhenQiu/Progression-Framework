package edu.uci.eecs.wukong.framework;

/**
 * Progression Key is the key to identify the data source.
 * 
 * A Physical Key is the identifier of a sensor, actuator or a virtual component on a physical device.
 * The physical object identified by the key is a real data generator. When we monitor the system,
 * the data is labeled by 
 * 
 * A Logical Key is the identifier of a component on a flow based application. It is the identifier of 
 * a progression buffer.
 * 
 * 
 * @author Peter Huang
 *
 */
public class ProgressionKey {
	public static class PhysicalKey {
		private String deviceId;
		private String portId;
		public PhysicalKey(String deviceId, String portId) {
			this.deviceId = deviceId;
			this.portId = portId;
		}
		
		public String getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		public String getPortId() {
			return portId;
		}
		public void setPortId(String portId) {
			this.portId = portId;
		}
	}
	
	public static class LogicalKey {
		private String appId; 
		private String componentId;
		
		public LogicalKey(String appId, String componentId) {
			this.appId = appId;
			this.componentId = componentId;
		}
		
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public String getComponentId() {
			return componentId;
		}
		public void setComponentId(String componentId) {
			this.componentId = componentId;
		}
	}
}
