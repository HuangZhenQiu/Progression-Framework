package edu.uci.eecs.wukong.framework;

import scala.Int;

import java.io.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class MutualInfoMatrix {
    private int sensor_count;
    private int[][] mut_info_matrix;
    private boolean flag_activity_start;
    private String last_act;
    private int activity_window_num;
    private Map<String, ActivityWindow> window_map;

    public MutualInfoMatrix(){
        this.sensor_count = SensorClass.values().length;
        this.mut_info_matrix = new int[sensor_count][sensor_count];
        this.flag_activity_start = false;
        this.last_act = ActivityClass.Other.toString();
        this.window_map = new HashMap<String, ActivityWindow>();
    }

    public void updateByRawLine(String raw){
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
        }

        if (substrs.length > 4){
            String act_label = substrs[4];
            int suffix_index = act_label.indexOf("_begin");
            String activity_class;
            ActivityWindow window;
            if (suffix_index != -1){ // it's activity _begin
                activity_class = act_label.substring(0, suffix_index);
                window = new ActivityWindow(substrs[0], substrs[1], this.last_act, substrs[2]);
                this.window_map.put(activity_class, window);
                this.last_act = activity_class;
                this.flag_activity_start = true;
            }
            else { // it's activity _end
                suffix_index = act_label.indexOf("_end");
                if (suffix_index == -1)
                    return;
                activity_class = act_label.substring(0, suffix_index);
                window = this.window_map.get(activity_class);
                if (window != null){
                    this.activity_window_num++;
                    for (String sensor_id_row : window.sensor_id_to_count.keySet()){
                        int row = SensorClass.valueOf(sensor_id_row).ordinal();
                        for (String sensor_id_col : window.sensor_id_to_count.keySet()){
                            int col = SensorClass.valueOf(sensor_id_col).ordinal();
                            this.mut_info_matrix[row][col] += 1;
                        }
                    }
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
                }
            }

        }
    }

    public void setValue(int row, int col, int value){
        this.mut_info_matrix[row][col] = value;
    }


    public double[][] getMatrix(){
        double[][] ret_matrix = new double[sensor_count][sensor_count];
        for (int i = 0; i < sensor_count; ++i){
            for (int j = 0; j < sensor_count; ++j){
                ret_matrix[i][j] = (double)(this.mut_info_matrix[i][j]) / (double)(this.activity_window_num);
            }
        }
        return ret_matrix;
    }

    public void setActivityWindowNum(int value){
        this.activity_window_num = value;
    }

    public int getActivityWindowNum(){
        return this.activity_window_num;
    }

    public void writeToFile(String path) throws IOException {
        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
        outputWriter.write(Integer.toString(activity_window_num));
        outputWriter.newLine();

        for (int[] row : this.mut_info_matrix) {
            for (int item : row){
                outputWriter.write(Integer.toString(item) + ",");
            }
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public static MutualInfoMatrix readFromFile(String filename) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        MutualInfoMatrix matrix = new MutualInfoMatrix();

        String line;
        if ((line = bufferedReader.readLine()) != null){
            matrix.setActivityWindowNum(Integer.parseInt(line));
        }

        int row = 0;
        while ((line = bufferedReader.readLine()) != null) {
            String[] tokens = line.split(",");
            int col = 0;
            for (String token : tokens){
                matrix.setValue(row, col, Integer.parseInt(token));
                col++;
            }
            row++;
        }
        return matrix;
    }

    public static void main(String[] args) throws Exception {
        MutualInfoMatrix matrix;
        String path = MutualInfoMatrix.class.getClassLoader().getResource("mutualinfomatrix.txt").toURI().toString().split(":")[1];
        String line;

        boolean toGenerateModel = false;

        if (toGenerateModel) {

            matrix = new MutualInfoMatrix();
            InputStream inputStream = MutualInfoMatrix.class.getClassLoader().getResourceAsStream("data.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = bufferedReader.readLine()) != null) {
                matrix.updateByRawLine(line);
            }
            System.out.println(path);
            matrix.writeToFile(path);
        } else {
            matrix = MutualInfoMatrix.readFromFile(path);
            System.out.println(matrix.getActivityWindowNum());
        }

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
