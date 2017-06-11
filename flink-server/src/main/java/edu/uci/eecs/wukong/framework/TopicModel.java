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

    public TopicModel() throws IOException {
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes
        pipeList.add( new SvmLight2FeatureVectorAndLabel() );
        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("lda.txt");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)$"),1, 0, 0));

        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        int numTopics = ActivityClass.values().length;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, alpha*numTopics, beta);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(1);

        // Run the model for 50 iterations and stop (this is for testing only,
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(1500);
        model.setRandomSeed(1);
        model.estimate();
        pipe = instances.getPipe();
        inferencer = model.getInferencer();


//        // Show the words and topics in the first instance
//
//        // The data alphabet maps word IDs to strings
//        Alphabet dataAlphabet = instances.getDataAlphabet();
//
//        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
//        LabelSequence topics = model.getData().get(0).topicSequence;
//
//        Formatter out = new Formatter(new StringBuilder(), Locale.US);
//        for (int position = 0; position < tokens.getLength(); position++) {
//            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
//        }
//        System.out.println(out);
//
//        // Estimate the topic distribution of the first instance,
//        //  given the current Gibbs state.
//        double[] topicDistribution = model.getTopicProbabilities(0);
//
//        // Get an array of sorted sets of word ID/count pairs
//        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
//
//        // Show top 5 words in topics with proportions for the first document
//        for (int topic = 0; topic < numTopics; topic++) {
//            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
//
//            out = new Formatter(new StringBuilder(), Locale.US);
//            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
//            int rank = 0;
//            while (iterator.hasNext() && rank < 5) {
//                IDSorter idCountPair = iterator.next();
//                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
//                rank++;
//            }
//            System.out.println(out);
//        }
//
//        // Create a new instance with high probability of topic 0
//        StringBuilder topicZeroText = new StringBuilder();
//        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
//
//        int rank = 0;
//        while (iterator.hasNext() && rank < 5) {
//            IDSorter idCountPair = iterator.next();
//            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
//            rank++;
//        }
//
//        // Create a new instance named "test instance" with empty target and source fields.
//        InstanceList testing = new InstanceList(instances.getPipe());
//        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
//
//        TopicInferencer inferencer = model.getInferencer();
//        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
//        System.out.println("0\t" + testProbabilities[0]);
    }

    public double[] predict(ActivityDataStream.ActivityWindow activityWindow) {
        InstanceList newDoc = new InstanceList(pipe);
        newDoc.addThruPipe(new Instance(activityWindow.toString(), null, "test", null));
        return inferencer.getSampledDistribution(newDoc.get(0), 10, 1, 5);
    }
}
