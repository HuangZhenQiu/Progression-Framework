package edu.uci.eecs.wukong.framework;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class TopicModel {
    private ParallelTopicModel model;
    private TopicInferencer inferencer;
    private Pipe pipe;
    private double alpha = 0.1;
    private double beta = 0.01;
    private int numTopics;
    private double[] activityArray;
    private double[][] topicWordDist;

    public TopicModel (InputStream modelStream, InputStream activityStream) throws IOException {
        numTopics = ActivityClass.values().length;

        if (modelStream != null){
            try{
                model = WuKongParallelTopicModel.read(modelStream);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        topicWordDist = getTopicToWordDistribution();

        if (activityStream != null){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(activityStream));
            activityArray = new double[numTopics];

            String line;
            int index = 0;
            if ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(",");
                for (String item : tokens){
                    activityArray[index++] = Double.parseDouble(item);
                }
            }
        }

//        pipe = instances.getPipe();
//        inferencer = model.getInferencer();
    }

    public TopicModel(){
        numTopics = ActivityClass.values().length;
        model = new ParallelTopicModel(this.numTopics, this.alpha * this.numTopics, this.beta);
        trainTopicModel();
        topicWordDist = getTopicToWordDistribution();
    }

    public void trainTopicModel() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add(new TopicModel.SvmLight2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        InputStream inputStream = TopicModel.class.getClassLoader().getResourceAsStream("lda.txt");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(.*)$"), 1, 0, 0));

        model.addInstances(instances);
        model.setNumThreads(1);
        model.setNumIterations(1500);
        model.setRandomSeed(1);
        try {
            model.estimate();
        } catch (IOException e){
            e.printStackTrace();
        }

        activityArray = getActivityArray(instances);
    }

    public void writeToFile(String modelPathToWrite, String activityArrayPath) throws IOException{

        if(modelPathToWrite != null) {
            model.write(new File(modelPathToWrite));
        }

        if(activityArrayPath != null){
            BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(activityArrayPath)));

            for (double item : activityArray) {
                outputWriter.write(Double.toString(item) + ",");
            }
            outputWriter.flush();
            outputWriter.close();
        }
    }

    public double[] inferHighLevelFeatures(int[] basicFeature){
        double[] highLevelFeature = new double[topicWordDist.length];

        int index = 0;
        for (double[] row : topicWordDist){
            int productIndex = 0;
            double innerProduct = 0.0;
            for (double item: row){
                innerProduct += (basicFeature[productIndex] * Math.log1p(item));
                productIndex++;
            }
            highLevelFeature[index] = innerProduct * activityArray[index];
            index++;
        }
        return highLevelFeature;
    }

    public double[] predict(ActivityDataStream.ActivityWindow activityWindow) {
        InstanceList newDoc = new InstanceList(pipe);
        newDoc.addThruPipe(new Instance(activityWindow.toString(), null, "test", null));
        return inferencer.getSampledDistribution(newDoc.get(0), 10, 1, 5);
    }

    private double[] getActivityArray(InstanceList instances){
        int numSample = instances.size();
        int [] ytrain = new int[numSample];
        int [] topic_array = new int[numTopics];
        for (int i = 0; i < instances.size(); ++i) {
            Instance instance = instances.get(i);
            int topic = Integer.parseInt(instance.getTarget().toString());
            ytrain[i] = topic;
            topic_array[topic]++;
        }

        double[][] docTopics = this.getDocTopicDistribution();
        int[][] topic_act = new int[numTopics][numTopics];
        for (int i = 0; i < docTopics.length; ++i){
            double[] elems = docTopics[i];
            int bestIdx = -1;
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < elems.length; j++) {
                double elem = elems[j];
                if (elem > max) {
                    max = elem;
                    bestIdx = j;
                }
            }
            topic_act[bestIdx][ytrain[i]]++;
        }

        int[] map1 = new int[numTopics];
        for (int i = 0 ; i < topic_act.length; ++i){
            int[] elems = topic_act[i];
            int bestIdx = -1;
            int max = Integer.MIN_VALUE;
            for (int j = 0; j < elems.length; j++) {
                int elem = elems[j];
                if (elem > max) {
                    max = elem;
                    bestIdx = j;
                }
            }
            map1[i] = bestIdx;
        }

        double[] act_array = new double[numTopics];
        for (int i = 0; i < numTopics; i++){
            act_array[i] = (double)(topic_array[map1[i]]) / numSample;
        }

        ytrain = topic_array = map1 = null;
        docTopics = null;
        return act_array;
    }

    private double[][] getTopicToWordDistribution(){
        int[][] typeTopicCounts = this.model.getTypeTopicCounts();
        int topicMask = this.model.topicMask, topicBits = this.model.topicBits;
        int numTypes = typeTopicCounts.length;
        double[][] topicWords = new double [numTopics][numTypes];


        // Initialize the tree sets
        for (int topic = 0; topic < numTopics; topic++) {
            double sum = 0.0;
            for (int type = 0; type < numTypes; type++) {

                int[] topicCounts = typeTopicCounts[type];

                double weight = beta;

                int index = 0;
                while (index < topicCounts.length &&
                        topicCounts[index] > 0) {

                    int currentTopic = topicCounts[index] & topicMask;


                    if (currentTopic == topic) {
                        weight += topicCounts[index] >> topicBits;
                        break;
                    }

                    index++;
                }

                topicWords[topic][type] = weight;
                sum += weight;
            }
            for (int type = 0; type < numTypes; type++){
                topicWords[topic][type] /= sum;
            }
        }

        typeTopicCounts = null;
        return topicWords;
    }

    private double[][] getDocTopicDistribution(){

        int numDocs = this.model.getData().size();
        double[][] docTopics = new double [numDocs][];


        // Initialize the tree sets
        for (int doc = 0; doc < numDocs; doc++) {
            docTopics[doc] = this.model.getTopicProbabilities(doc);
        }

        return docTopics;
    }

    public static class SvmLight2FeatureSequence extends Pipe{
        private static final long serialVersionUID = 1L;

        public SvmLight2FeatureSequence () {
            super (new Alphabet(), new LabelAlphabet());
        }

        @Override public Instance pipe(Instance carrier) {
            // we expect the data for each instance to be
            // a line from the SVMLight format text file
            String dataStr = (String)carrier.getData();

            // ignore comments at the end
            if (dataStr.contains("#")) {
                dataStr = dataStr.substring(0, dataStr.indexOf('#'));
            }

            String[] terms = dataStr.split("\\s+");

            String classStr = terms[0];
            // In SVMLight +1 and 1 are the same label.
            // Adding a special case to normalize...
            if (classStr.equals("+1")) {
                classStr = "1";
            }
            Label label = ((LabelAlphabet)getTargetAlphabet()).lookupLabel(classStr, true);
            carrier.setTarget(label);

            FeatureSequence featureSequence =
                    new FeatureSequence (getDataAlphabet(), terms.length);
            for (int termIndex = 1; termIndex < terms.length; termIndex++) {
                if (!terms[termIndex].equals("")) {
                    String[] s = terms[termIndex].split(":");
                    if (s.length != 2) {
                        throw new RuntimeException("invalid format: " + terms[termIndex] + " (should be feature:value)");
                    }
                    String feature = s[0];

                    for (int times = Integer.parseInt(s[1]); times > 0; times--){
                        featureSequence.add(feature);
                    }
                }
            }
            carrier.setData(featureSequence);

            return carrier;
        }
    }

    public int[] getTopicToWordMatrixDimension(){
        int[] ret = new int[2];
        ret[0] = topicWordDist.length;
        ret[1] = topicWordDist[0].length;
        return ret;
    }

    public static TopicModel createByDefault(){
        TopicModel tm = null;
        try {
            InputStream modelStream = TopicModel.class.getClassLoader().getResourceAsStream("trainedtopicmodel.txt");
            InputStream activityStream = TopicModel.class.getClassLoader().getResourceAsStream("activityarray2topic.txt");
            tm  = new TopicModel(modelStream, activityStream);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return tm;
    }

    public static void main(String[] args) throws Exception {
//        String model_path = TopicModel.class.getClassLoader().getResource("trainedtopicmodel.txt").toURI().toString().split(":")[1];
//        String act_array_path = TopicModel.class.getClassLoader().getResource("activityarray2topic.txt").toURI().toString().split(":")[1];
//        long startTime = System.nanoTime();
//        TopicModel m = new TopicModel();
//        long endTime = System.nanoTime();
//        m.writeToFile( model_path, act_array_path);
//
//        long duration = (endTime - startTime);
//        System.out.println("Time duration" + Long.toString(duration) + "nano second");
        TopicModel m = TopicModel.createByDefault();
        int[] row_col = m.getTopicToWordMatrixDimension();
        System.out.println("Row " + Integer.toString(row_col[0])+" Col " + Integer.toString(row_col[1]));

    }

}

