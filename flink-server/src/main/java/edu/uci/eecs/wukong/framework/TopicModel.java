package edu.uci.eecs.wukong.framework;

import cc.mallet.util.*;
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

    public TopicModel() throws IOException {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        pipeList.add(new SvmLight2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("lda.txt");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(.*)$"), 1, 0, 0));

        this.numTopics = ActivityClass.values().length;
        this.model = new ParallelTopicModel(numTopics, alpha * numTopics, beta);

        model.addInstances(instances);

        model.setNumThreads(1);

        model.setNumIterations(1500);
        model.setRandomSeed(1);
        model.estimate();

        activityArray = getActivityArray(instances);
        topicWordDist = getTopicToWordDistribution();

//        pipe = instances.getPipe();
//        inferencer = model.getInferencer();
    }

    public double[] inferHighLevelFeatures(int[] basicFeature){
        double[] highLevelFeature = new double[basicFeature.length];


        int index = 0;
        for (double[] row : topicWordDist){
            int productIndex = 0;
            double innerProduct = 0.0;
            for (double product: row){
                innerProduct += (basicFeature[productIndex] * Math.log1p(row[productIndex]));
                productIndex++;
            }
            highLevelFeature[index] = innerProduct * activityArray[index];
            index++;
        }
        return highLevelFeature;
    }



//    public double[] predict(ActivityDataStream.ActivityWindow activityWindow) {
//        InstanceList newDoc = new InstanceList(pipe);
//        newDoc.addThruPipe(new Instance(activityWindow.toString(), null, "test", null));
//        return inferencer.getSampledDistribution(newDoc.get(0), 10, 1, 5);
//    }

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

//    public static void main(String[] args) throws Exception {
//        TopicModel m = new TopicModel();
//
//    }


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
}

class SvmLight2FeatureSequence extends Pipe{
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

        // the rest are feature-value pairs
//        ArrayList<Integer> indices = new ArrayList<Integer>();
//        ArrayList<Integer> values = new ArrayList<Integer>();
        FeatureSequence featureSequence =
                new FeatureSequence ((Alphabet) getDataAlphabet(), terms.length);
        for (int termIndex = 1; termIndex < terms.length; termIndex++) {
            if (!terms[termIndex].equals("")) {
                String[] s = terms[termIndex].split(":");
                if (s.length != 2) {
                    throw new RuntimeException("invalid format: " + terms[termIndex] + " (should be feature:value)");
                }
                String feature = s[0];
//                int index = getDataAlphabet().lookupIndex(feature, true);

                for (int times = Integer.parseInt(s[1]); times > 0; times--){
                    featureSequence.add(feature);
                }
            }
        }
        carrier.setData(featureSequence);

//        assert(indices.size() == values.size());
//        int[] indicesArr = new int[indices.size()];
//        double[] valuesArr = new double[values.size()];
//        for (int i = 0; i < indicesArr.length; i++) {
//            indicesArr[i] = indices.get(i);
//            valuesArr[i] = values.get(i);
//        }
//
//        FeatureVector fv = new FeatureVector(getDataAlphabet(), indicesArr, valuesArr);
//        carrier.setData(fv);
        return carrier;
    }
}