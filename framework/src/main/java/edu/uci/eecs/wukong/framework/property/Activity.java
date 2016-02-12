package edu.uci.eecs.wukong.framework.property;

public class Activity {
	private long timestamp;
	private short deviceId;
	private float value;
	private int sequence;
	
	public Activity(long timestamp, short deviceId, float value) {
		this.timestamp = timestamp;
		this.deviceId = deviceId;
		this.value = value;
	}
	
	public long getTimeStamp() {
		return timestamp;
	}
	
	public short getDeviceId() {
		return deviceId;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public int getSequence() {
		return sequence;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Activity) {
			Activity activity = (Activity) object;
			if (this.timestamp == activity.timestamp
					&& this.deviceId == activity.deviceId
					&& this.value == activity.value
					&& this.sequence == activity.sequence) {
				return true;
			}
		}
		
		return false;
	}
}