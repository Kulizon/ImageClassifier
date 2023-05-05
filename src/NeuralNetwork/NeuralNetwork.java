package NeuralNetwork;

import Matrix.Matrix;

public class NeuralNetwork {
    int inputNodes;
    int hiddenNodes;
    int outputNodes;

    Matrix inputToHiddenWeights;
    Matrix hiddenToOutputWeights;

    Matrix hiddenBias;
    Matrix outputBias;
    float lr = 0.5f;

    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;

        inputToHiddenWeights = new Matrix(hiddenNodes, inputNodes);
        hiddenToOutputWeights = new Matrix(outputNodes, hiddenNodes);
        inputToHiddenWeights.randomize();
        hiddenToOutputWeights.randomize();

        hiddenBias = new Matrix(hiddenNodes, 1);
        outputBias = new Matrix(outputNodes, 1);
        hiddenBias.randomize();
        outputBias.randomize();
    }

    private float sigmoid(float x) {
        return (float)(1 / (1 + Math.exp(-x)));
    }
    private float dsigmoid(float y) {
        return y * (1 - y);
    }

    public Matrix[] feedForward(float[] inputs) {
        if (inputs.length != inputNodes) {
            throw new IllegalArgumentException("Inputs length doesn't match the given number of input nodes");
        }

        Matrix inputsMatrix = Matrix.fromArray(inputs);

        Matrix outputHiddenMatrix = (inputToHiddenWeights.multiply(inputsMatrix)).add(hiddenBias);
        outputHiddenMatrix = outputHiddenMatrix.map(x -> sigmoid(x));

        Matrix outputMatrix = (hiddenToOutputWeights.multiply(outputHiddenMatrix)).add(outputBias);
        outputMatrix = outputMatrix.map(x -> sigmoid(x));

        return new Matrix[]{outputMatrix, outputHiddenMatrix};
    }

    public Matrix guess(float[] inputs) {
        Matrix[] outputs = this.feedForward(inputs);
        return outputs[0];
    }

    public void train(float[] inputs, float[] userTargets) {
        Matrix[] outputsArray = this.feedForward(inputs);
        Matrix outputs = outputsArray[0];
        Matrix hiddenOutputs = outputsArray[1];
        Matrix inputsMatrix = Matrix.fromArray(inputs);

        Matrix targets = Matrix.fromArray(userTargets);
        Matrix outputErrorsMatrix = targets.subtract(outputs);
        Matrix hiddenErrorsMatrix = this.hiddenToOutputWeights.transpose().multiply(outputErrorsMatrix);

        Matrix gradient = outputs.map(y -> dsigmoid(y));
        gradient = gradient.multiplyEW(outputErrorsMatrix).multiply(this.lr);
        Matrix hiddenToOutputWeightDeltas = gradient.multiply( hiddenOutputs.transpose());
        this.outputBias = this.outputBias.add(gradient);
        this.hiddenToOutputWeights = this.hiddenToOutputWeights.add(hiddenToOutputWeightDeltas);

        Matrix hiddenGradient = hiddenOutputs.map(y -> dsigmoid(y));
        hiddenGradient = hiddenGradient.multiplyEW(hiddenErrorsMatrix).multiply(this.lr);
        Matrix inputToHiddenWeightDeltas = hiddenGradient.multiply(inputsMatrix.transpose());
        this.hiddenBias = this.hiddenBias.add(hiddenGradient);
        this.inputToHiddenWeights = this.inputToHiddenWeights.add(inputToHiddenWeightDeltas);
    }
}
