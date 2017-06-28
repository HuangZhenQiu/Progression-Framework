package edu.uci.eecs.wukong.framework.yarn;

public class EventData {
    private String hostID;
    private String event;
    private long timestamp;

    public EventData() {
    }

    public EventData(String hostID, String event, long timestamp) {
        this.hostID = hostID;
        this.event = event;
        this.timestamp = timestamp;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
