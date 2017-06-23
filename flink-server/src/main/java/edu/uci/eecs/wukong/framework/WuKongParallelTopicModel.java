package edu.uci.eecs.wukong.framework;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.LabelAlphabet;

import java.io.InputStream;
import java.io.ObjectInputStream;

public class WuKongParallelTopicModel extends ParallelTopicModel {

    public WuKongParallelTopicModel(int numberOfTopics) {
        super(numberOfTopics);
    }

    public WuKongParallelTopicModel(int numberOfTopics, double alphaSum, double beta) {
        super(numberOfTopics, alphaSum, beta);
    }

    public WuKongParallelTopicModel(LabelAlphabet topicAlphabet, double alphaSum, double beta) {
        super(topicAlphabet, alphaSum, beta);
    }

    public static ParallelTopicModel read(InputStream inputStream) throws Exception {

        ObjectInputStream ois = new ObjectInputStream (inputStream);
        WuKongParallelTopicModel topicModel = (WuKongParallelTopicModel) ois.readObject();
        ois.close();

        topicModel.initializeHistograms();
        return topicModel;
    }

    public void initializeHistograms() {
        int maxTokens = 0;
        totalTokens = 0;
        int seqLen;

        for (int doc = 0; doc < data.size(); doc++) {
            FeatureSequence fs = (FeatureSequence) data.get(doc).instance.getData();
            seqLen = fs.getLength();
            if (seqLen > maxTokens)
                maxTokens = seqLen;
            totalTokens += seqLen;
        }

        logger.info("max tokens: " + maxTokens);
        logger.info("total tokens: " + totalTokens);

        docLengthCounts = new int[maxTokens + 1];
        topicDocCounts = new int[numTopics][maxTokens + 1];
    }
}
