package edu.uci.eecs.wukong.framework;

import com.google.common.collect.Lists;
import quickml.data.*;
import quickml.data.instances.ClassifierInstance;

import quickml.supervised.ensembles.randomForest.randomDecisionForest.*;
import quickml.supervised.tree.attributeIgnoringStrategies.*;
import quickml.supervised.tree.decisionTree.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class RandomForest {
    private RandomDecisionForest randomForest;

    public RandomForest(){
        String filename = "featurestream.txt";
        randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()).buildPredictiveModel(getSvmInstances(filename));
    }

    public int predictByFinalFeatures(double[] finalFeatures){
        AttributesMap attributes = new AttributesMap();
        for (int i = 0; i < finalFeatures.length; ++i){
            attributes.put(Integer.toString(i+1), finalFeatures[i]);
        }
        int ret = Integer.parseInt(randomForest.predict(attributes).toString());
//        System.out.println("Prediction: " + ret);
        return ret;
    }

    public List<ClassifierInstance> getSvmInstances(String filename) {
        List<ClassifierInstance> instances = Lists.newArrayList();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filename);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            for (String line; (line = br.readLine()) != null; ) {
                List<String> rawInstance = Arrays.asList(line.split("\t"));
//                System.out.println("LABEL "+rawInstance.get(0));
                Double label = Double.valueOf(rawInstance.get(0));
                AttributesMap map = AttributesMap.newHashMap();

                for (String rawAttributeAndValue : rawInstance.subList(1, rawInstance.size())) {
                    String[] attributeAndValue = rawAttributeAndValue.split(":");
                    String attribute = attributeAndValue[0];
                    String value = attributeAndValue[1];
                    try {
                        //add numeric variable as Double
                        map.put(attribute, Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        //add categorical variable as String
                        map.put(attribute, value);
                    }

                }
                instances.add(new ClassifierInstance(map, label));
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return instances;
    }

    public static void main(String[] args) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(RandomForest.class.getClassLoader().getResourceAsStream("iris.data.gz")))));
        final List<ClassifierInstance> irisDataset = Lists.newLinkedList();

        String[] headings = new String[]{"sepal-length", "sepal-width", "petal-length", "petal-width"};

        String line = br.readLine();
        while (line != null) {
            String[] splitLine = line.split(",");

            AttributesMap attributes = AttributesMap.newHashMap();
            for (int x = 0; x < splitLine.length - 1; x++) {
                attributes.put(headings[x], Double.valueOf(splitLine[x]));
            }
            irisDataset.add(new ClassifierInstance(attributes, splitLine[splitLine.length - 1]));
            line = br.readLine();
        }

        final RandomDecisionForest randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()
                // The default isn't desirable here because this dataset has so few attributes
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.2)))
                .buildPredictiveModel(irisDataset);

        AttributesMap attributes = new AttributesMap();
        attributes.put("sepal-length", 5.84);
        attributes.put("sepal-width", 3.05);
        attributes.put("petal-length", 3.76);
        attributes.put("petal-width", 1.20);
        System.out.println("Prediction: " + randomForest.predict(attributes));
        for (ClassifierInstance instance : irisDataset) {
            System.out.println("classification: " + randomForest.getClassificationByMaxProb(instance.getAttributes()));
        }
    }
}
