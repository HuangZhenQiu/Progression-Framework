package edu.uci.eecs.wukong.framework;

import com.google.common.collect.Lists;
import quickml.data.*;
import quickml.data.instances.ClassifierInstance;

import quickml.supervised.ensembles.randomForest.randomDecisionForest.*;
import quickml.supervised.tree.attributeIgnoringStrategies.*;
import quickml.supervised.tree.decisionTree.*;
import scala.Int;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;

public class RandomForest implements Serializable{
    static final long serialVersionUID = 12345665478925L;
    private RandomDecisionForest randomForest;

    public RandomForest(){
        InputStream modelStream = RandomForest.class.getClassLoader().getResourceAsStream("featurestream.txt");
        randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()).buildPredictiveModel(getSvmInstances(modelStream));
    }

    public RandomForest(List<ClassifierInstance> irisDataset, double prob){
        randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()
                // The default isn't desirable here because this dataset has so few attributes
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(prob)))
                .buildPredictiveModel(irisDataset);
    }

    public RandomDecisionForest getModel(){
        return randomForest;
    }

    public int predictByFinalFeatures(double[] finalFeatures){
        AttributesMap attributes = new AttributesMap();
        for (int i = 0; i < finalFeatures.length; ++i){
            attributes.put(Integer.toString(i+1), finalFeatures[i]);
        }
        int ret = Integer.parseInt(randomForest.getClassificationByMaxProb(attributes).toString());
//        System.out.println("Prediction: " + ret);
        return ret;
    }

    public List<ClassifierInstance> getSvmInstances(InputStream inputStream) {
        List<ClassifierInstance> instances = Lists.newArrayList();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            int count = 0;
            for (String line; (line = br.readLine()) != null; ) {
                List<String> rawInstance = Arrays.asList(line.split("\t"));
                System.out.println(Integer.toString(count++) + " LABEL "+rawInstance.get(0));
                Integer label = Integer.valueOf(rawInstance.get(0));
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

    public void writeToFile(String modelPathToWrite){
        try {
            if (modelPathToWrite != null) {
                ObjectOutputStream obj_out = new ObjectOutputStream(new FileOutputStream( new File(modelPathToWrite), false));
                obj_out.writeObject(this);
                obj_out.flush();
                obj_out.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static RandomForest createByDefault(){

        RandomForest rf = null;
        try {
            InputStream modelStream = RandomForest.class.getClassLoader().getResourceAsStream("trainedrandomforest.txt");
            ObjectInputStream stream = new ObjectInputStream(modelStream);
            rf = (RandomForest) stream.readObject();

        }catch (Exception e) {
            e.printStackTrace();
        }

        return rf;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {

//        String model_path = RandomForest.class.getClassLoader().getResource("trainedrandomforest.txt").toURI().toString().split(":")[1];
//        long startTime = System.nanoTime();
//        RandomForest m = new RandomForest();
//        long endTime = System.nanoTime();
//        m.writeToFile( model_path);
//
//        long duration = (endTime - startTime);
//        System.out.println("Time duration" + Long.toString(duration) + "nano second");
//        System.exit(0);


//        RandomForest m = RandomForest.createByDefault();
//        System.exit(0);



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
        File yourFile = new File("/home//myobject.data");

        RandomForest rf = new RandomForest(irisDataset, 0.2);

        // Write out
//        yourFile.createNewFile();
//        ObjectOutputStream obj_out = new ObjectOutputStream (new FileOutputStream(yourFile, false));
//        obj_out.writeObject ( rf );
//
//        System.out.println();
//        System.exit(0);


        // Read back
//        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(yourFile));
//        RandomForest rf = (RandomForest) stream.readObject();

        // Get Model
        RandomDecisionForest randomForest = rf.getModel();

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
