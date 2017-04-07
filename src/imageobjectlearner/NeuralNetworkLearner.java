package imageobjectlearner;

public class NeuralNetworkLearner {
    public static int makePrediction(double[][] w, double[] x) {
        int[] y = new int[w.length];
        
        for(int i = 0; i < y.length; i++)
        {
            int sum = 0;
            for(int j = 0; j < x.length; j++)
            {
                sum += w[i][j]*x[j];
            }
            y[i] = sum;
        }
        
        int max = y[0];
        for(int i = 1; i < y.length; i++)
        {
            if(y[i] > max)
            {
                max = y[i];
            }
        }
        
        return max;
    }

    public static void updateWeights(double[][] w, double[] x, int prediction, int target) {
        if(prediction != target)
        {
            for(int j = 0; j < 2304; j++)
            {
                w[target][j] += x[j];
                w[prediction][j] -= x[j];
            }
        }
    }
}
