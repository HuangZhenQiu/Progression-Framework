package edu.uci.eecs.wukong.framework;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Features {
    public Timestamp daystamp;//, daystamp_start, daystamp_end;
    public int sensor_id; //, sensor_start_id, sensor_last_id;
    public int current_act; //, parent_act;

    public Features(String raw){
        String[] substrs = raw.split("\t");
        daystamp = Timestamp.valueOf(substrs[0] + " " + substrs[1]);
        sensor_id = SensorClass.valueOf(substrs[2]).ordinal();
        current_act = ActivityClass.valueOf(substrs[4]).ordinal();
    }

//    public Features(){
//
//    }

//    public static Features reduce(Features first, Features second){
//        Features ret = new Features();
//        if (first.daystamp.compareTo(second.daystamp) > 0) {
//            Features tmp = first;
//            first = second;
//            second = tmp;
//        }
//        ret.daystamp = second.daystamp;
//        ret.daystamp_start = first.daystamp;
//        ret.daystamp_end = second.daystamp;
//        ret.sensor_id = second.sensor_id;
//        ret.sensor_start_id = first.sensor_id;
//        ret.sensor_last_id = second.sensor_id;
//        ret.current_act = second.current_act;
//        ret.matrix = first.matrix;
//        return ret;
//    }

}
