package edu.uci.eecs.wukong.framework;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ActivityDataStream {
    private int sensor_count;
    private boolean flag_activity_start;
    private String last_act;
    private Map<String, ActivityWindow> window_map;

    private final String OTHER_CLASS = ActivityClass.Other.toString();
    private boolean flag_other_activity_start;

    public ActivityDataStream(){
        this.sensor_count = SensorClass.values().length;
        this.flag_activity_start = false;
        this.last_act = ActivityClass.Other.toString();
        this.window_map = new HashMap<String, ActivityWindow>();
    }

    public String parseRawLine(String raw){
        String[] substrs = raw.split("\t");

        if (this.flag_activity_start){
            String sensor_id = new String(substrs[2]);
            ActivityWindow window = this.window_map.get(this.last_act);
            Integer count = window.sensor_id_to_count.get(sensor_id);
            if (count == null){
                window.sensor_id_to_count.put(sensor_id, 1);
            } else {
                window.sensor_id_to_count.put(sensor_id, count + 1);
            }

            if (substrs.length <= 4){
                return new String(substrs[0]) + "\t" + new String(substrs[1]) + "\t" + new String(substrs[2]) + "\t" + new String(substrs[3]) + "\t" + this.last_act;
            }
        } else {
            String sensor_id = new String(substrs[2]);
            ActivityWindow window = this.window_map.get(OTHER_CLASS);
            if (window == null){
                window = new ActivityWindow(substrs[0], substrs[1], this.last_act, substrs[2]);
                this.window_map.put(OTHER_CLASS, window);
                this.flag_other_activity_start = true;
            } else {
                Integer count = window.sensor_id_to_count.get(sensor_id);
                if (count == null) {
                    window.sensor_id_to_count.put(sensor_id, 1);
                } else {
                    window.sensor_id_to_count.put(sensor_id, count + 1);
                }
            }
            return new String(substrs[0]) + "\t" + new String(substrs[1]) + "\t" + new String(substrs[2]) + "\t" + new String(substrs[3]) + "\t" + OTHER_CLASS;
        }

        if (substrs.length > 4){ // activity label comes, either _begin or _end
            String act_label = substrs[4];
            int suffix_index = act_label.indexOf("_begin");
            String activity_class;
            ActivityWindow window;
            if (suffix_index != -1){ // it's activity _begin
                if (flag_other_activity_start){ // it's OTHER begin
                    flag_other_activity_start = false;
                    // ... corresponding useless lines are generating window.txt
                }
                activity_class = act_label.substring(0, suffix_index);
                window = new ActivityWindow(substrs[0], substrs[1], this.last_act, substrs[2]);
                this.window_map.put(activity_class, window);
                this.last_act = activity_class;
                this.flag_activity_start = true;

                return new String(substrs[0]) + "\t" + new String(substrs[1]) + "\t" + new String(substrs[2]) + "\t" + new String(substrs[3]) + "\t" + activity_class;
            }
            else { // it's activity _end
                suffix_index = act_label.indexOf("_end");
                if (suffix_index == -1)
                    return null;
                activity_class = act_label.substring(0, suffix_index);
                window = this.window_map.get(activity_class);
                if (window != null){


                    this.last_act = window.previous_act;
                    this.window_map.remove(activity_class);
                    window = null;
                    if (this.window_map.size() < 1){
                        this.flag_activity_start = false;
                        this.last_act = activity_class;
                    } else if (this.window_map.get(this.last_act) == null){
                        Timestamp ts_max = Timestamp.valueOf("1990-01-01 00:00:00.0000"), ts_current;
                        for (Map.Entry<String, ActivityWindow> entry : this.window_map.entrySet()){
                            ts_current = Timestamp.valueOf(entry.getValue().daystamp_start + " " + entry.getValue().timestamp_start);
                            if (ts_max.compareTo(ts_current) < 0){
                                ts_max = ts_current;
                                this.last_act = entry.getKey();
                            }
                        }
                    }

                    return new String(substrs[0]) + "\t" + new String(substrs[1]) + "\t" + new String(substrs[2]) + "\t" + new String(substrs[3]) + "\t" + activity_class;
                }
            }

        }
        return null;
    }

    public class ActivityWindow {
        public String daystamp_start;
        public String timestamp_start;
        public String previous_act;
        public String start_sensor_id;
        public Map<String, Integer> sensor_id_to_count;
        public ActivityWindow(String daystamp, String timestamp, String parent_act, String sensor_id){
            this.daystamp_start = new String(daystamp);
            this.timestamp_start = new String(timestamp);
            this.previous_act = new String(parent_act);
            this.start_sensor_id = new String(sensor_id);
            this.sensor_id_to_count = new HashMap<String, Integer>();
            this.sensor_id_to_count.put(sensor_id, 1);
        }
    }
}
