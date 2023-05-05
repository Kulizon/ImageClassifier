import NeuralNetwork.NeuralNetwork;
import processing.core.PApplet;
import processing.core.PImage;
import static processing.core.PApplet.loadBytes;
import static processing.core.PConstants.RGB;
import Matrix.Matrix;
import java.util.Collections;

import java.io.File;
import java.util.Arrays;

public class Main extends PApplet {
    int imgBytes = 784;
    int imgWidth = 28;
    int imgHeight = 28;
    int trainingNumber = 800;
    int testNumber = 200;

    NeuralNetwork brain = new NeuralNetwork(imgBytes, 64, 3);
    public static void main(String[] args) {
        PApplet.main("Main");
    }

    float[] loadDoodles(String path) {
        File file = new File(path);
        byte[] data = loadBytes(file);
        float[] outData = new float[imgBytes * (trainingNumber + testNumber)];

        int outIndex = 0;
        for (int n = 0; n < trainingNumber + testNumber; n++) {
            int start = 80 + n * imgBytes;

            for (int i = 0; i < imgBytes; i++) {
                int index = i + start;
                outData[outIndex] = data[index] / 255.0f;
                outIndex++;
            }
        }

        return outData;
    }

    DataInput[] concatenateArrays(DataInput[] arr1, DataInput[] arr2, DataInput[] arr3) {
        DataInput[] result = new DataInput[arr1.length + arr2.length + arr3.length];
        int index = 0;
        for (int i = 0; i < arr1.length; i++) {
            result[index] = arr1[i];
            index++;
        }
        for (int i = 0; i < arr2.length; i++) {
            result[index] = arr2[i];
            index++;
        }
        for (int i = 0; i < arr3.length; i++) {
            result[index] = arr3[i];
            index++;
        }
        return result;
    }

    DataInput[][] separateData(float[] data, int label) {
        DataInput[] dataTraining = new DataInput[trainingNumber];
        DataInput[] dataTest = new DataInput[testNumber];
        for (int i = 0; i < trainingNumber + testNumber; i++) {
            int offset = i * imgBytes;
            if ( i < trainingNumber) {
                dataTraining[i] = new DataInput(Arrays.copyOfRange(data, offset, offset + imgBytes), label);
            } else {
                dataTest[i - trainingNumber] = new DataInput(Arrays.copyOfRange(data, offset, offset + imgBytes), label);
            }
        }

        return new DataInput[][]{dataTraining, dataTest};
    }

    void train(DataInput[] trainingData) {
        // train neural network
        for (int i = 0; i < trainingData.length; i++) {
            float[] targets = new float[]{0,0,0};
            targets[trainingData[i].label] = 1;
            brain.train(trainingData[i].inputs, targets);
        }
    }

    public int findMaxIndex(float[] arr) {
        int maxIndex = 0;
        float maxValue = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxValue) {
                maxValue = arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    void test(DataInput[] testData) {
        int c = 0;
        for (int i = 0; i < testData.length; i++) {
            Matrix guess = brain.guess(testData[i].inputs);
            float[] outputs = guess.toArray();
            int maxIndex = findMaxIndex(outputs);

            PImage img = new PImage(imgWidth, imgHeight, RGB);
            img.loadPixels();
            for (int j = 0; j < testData[i].inputs.length; j++) {
                img.pixels[j] = color( (int)(testData[i].inputs[j] * 255) & 0xff);
            }
            img.updatePixels();
            int x = (i % 30) * imgWidth;
            int y = (int)(i / 30) * imgHeight;
            image(img, x, y);
            noStroke();
            if (maxIndex == testData[i].label) {
                c++;
                fill(color(0, 255, 0));
            } else {
                fill(color(255, 0, 0));
            };
            ellipse(x + 8, y + 8, 12, 12);



        }
        float percent = ((float)c / testData.length) * 100;
        System.out.println("Correctly guessed: " + percent + "%");
    }

    public void settings() {
        // 600 tests
        size(imgWidth * 30, imgHeight * 20);
    }

    public void setup() {
        float[] catsData = loadDoodles("res/cat.npy");
        float[] rainbowsData = loadDoodles("res/rainbow.npy");
        float[] trainsData = loadDoodles("res/train.npy");

        DataInput[][] catsOutput = separateData(catsData, 0);
        DataInput[] catsTraining = catsOutput[0];
        DataInput[] catsTest = catsOutput[1];

        DataInput[][] rainbowsOutput = separateData(rainbowsData, 1);
        DataInput[] rainbowsTraining = rainbowsOutput[0];
        DataInput[] rainbowsTest = rainbowsOutput[1];

        DataInput[][] trainsOutput = separateData(trainsData, 2);
        DataInput[] trainsTraining = trainsOutput[0];
        DataInput[] trainsTest = trainsOutput[1];

        DataInput[] trainingData = concatenateArrays(catsTraining, rainbowsTraining, trainsTraining);
        Collections.shuffle(Arrays.asList(trainingData));

        DataInput[] testData = concatenateArrays(catsTest, rainbowsTest, trainsTest);
        Collections.shuffle(Arrays.asList(testData));


        // test(testData);
        train(testData);
        test(testData);
    }

}