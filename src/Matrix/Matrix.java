package Matrix;

public class Matrix {
    public int numOfRows;
    public int numOfCols;
    public float[][] matrix;
    // init matrix with given rows
    public Matrix(float[]... rows) {
        numOfRows = rows.length;
        numOfCols = rows[0].length;
        matrix = new float[numOfRows][numOfCols];
        for (int i = 0; i < numOfRows; i++) {
            if (numOfCols != rows[i].length) {
                throw new IllegalArgumentException("Number of columns doesn't match with given arrays");
            };

            matrix[i] = new float[numOfCols];
            for (int j = 0; j < numOfCols; j++) {
                matrix[i][j] = rows[i][j];
            }
        }
    }

    // inits matrix with zeros
    public Matrix(int rows, int cols) {
        numOfRows = rows;
        numOfCols = cols;
        matrix = new float[numOfRows][numOfCols];
        for (int i = 0; i < numOfRows; i++) {
            matrix[i] = new float[numOfCols];
            for (int j = 0; j < numOfCols; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    private Matrix performScalarOperation(float scalar, String operation) {
        if (operation != "multiply" && operation != "add" && operation != "subtract") {
            throw  new IllegalArgumentException("Illegal operation");
        }

        Matrix newMatrix = new Matrix(numOfRows, numOfCols);
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                if (operation == "multiply") newMatrix.matrix[i][j] = matrix[i][j] * scalar;
                if (operation == "add") newMatrix.matrix[i][j] = matrix[i][j] + scalar;
                if (operation == "subtract") newMatrix.matrix[i][j] = matrix[i][j] - scalar;
            }
        }
        return newMatrix;
    }

    private Matrix performScalarOperation(Matrix secondMatrix, String operation) {
        if (numOfCols != secondMatrix.numOfCols || numOfRows != secondMatrix.numOfRows) {
            throw new IllegalArgumentException("Matrix dimensions don't match");
        }

        if (operation != "add" && operation != "subtract") {
            throw  new IllegalArgumentException("Illegal operation");
        }

        Matrix newMatrix = new Matrix(numOfRows, secondMatrix.numOfCols);
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                if (operation == "add") newMatrix.matrix[i][j] = matrix[i][j] + secondMatrix.matrix[i][j];
                if (operation == "subtract") newMatrix.matrix[i][j] = matrix[i][j] - secondMatrix.matrix[i][j];
            }
        }

        return newMatrix;
    }

    public Matrix add(float scalar) {
        return performScalarOperation(scalar, "add");
    }

    public Matrix add(Matrix secondMatrix) {
        return performScalarOperation(secondMatrix, "add");
    }

    public Matrix subtract(float scalar) {
        return performScalarOperation(scalar, "subtract");
    }

    public Matrix subtract(Matrix secondMatrix) {
        return performScalarOperation(secondMatrix, "subtract");
    }

    public Matrix multiply(Matrix secondMatrix) {
        if (numOfCols != secondMatrix.numOfRows) {
            throw new IllegalArgumentException("Number of columns of the first array doesn't match with number of rows of the second array");
        }

        Matrix newMatrix = new Matrix(numOfRows, secondMatrix.numOfCols);

        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < secondMatrix.numOfCols; j++) {
                float sum = 0;
                for (int k = 0; k < numOfCols; k++) {
                    sum += matrix[i][k] * secondMatrix.matrix[k][j];
                }
                newMatrix.matrix[i][j] = sum;
            }
        }

        return newMatrix;
    }
    // multiply matrix element wise
    public Matrix multiplyEW(Matrix secondMatrix) {
        if (secondMatrix.numOfRows != numOfRows || secondMatrix.numOfCols != numOfCols) {
            throw new IllegalArgumentException("Second matrix has different dimensions");
        }

        Matrix newMatrix = new Matrix(numOfRows, numOfCols);

        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                newMatrix.matrix[i][j] = matrix[i][j] * secondMatrix.matrix[i][j];
            }
        }

        return newMatrix;
    }

    public Matrix multiply(float scalar) {
        return performScalarOperation(scalar, "multiply");
    }

    public void display() {
        for (int i = 0; i < numOfRows; i++) {
            System.out.print("|");
            for (int j = 0; j < numOfCols; j++) {
                String tab = j != numOfCols - 1 ? "  " : "|";
                System.out.print(matrix[i][j] + tab);
            }
            System.out.println("");
        }
    }

    public Matrix transpose() {
        Matrix newMatrix = new Matrix(numOfCols, numOfRows);
        for (int i = 0; i < numOfCols; i++) {
            for (int j = 0; j < numOfRows; j++) {
                newMatrix.matrix[i][j] = matrix[j][i];
            }
        }
        return newMatrix;
    }

    public interface Function<T, R> {
        R apply(T t);
    }

    public Matrix map(Function<Float, Float> mappingFunction) {
        Matrix newMatrix = new Matrix(numOfRows, numOfCols);
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                newMatrix.matrix[i][j] = mappingFunction.apply(matrix[i][j]);
            }
        }
        return newMatrix;
    }

    public void randomize() {
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                matrix[i][j] =  (float)(Math.random() * 2 - 1); // random number between -1 and 1
            }
        }
    }

    static public Matrix fromArray(float[] array) {
        Matrix outputMatrix = new Matrix(array.length, 1);
        for (int i = 0; i < array.length; i++) {
            outputMatrix.matrix[i][0] = array[i];
        }
        return outputMatrix;
    }

    public float[] toArray() {
        float[] outputArray = new float[numOfCols * numOfRows];
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                outputArray[i+j] = matrix[i][j];
            }
        }
        return outputArray;
    }
}


