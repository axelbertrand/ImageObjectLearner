package imageobjectlearner;

import java.io.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class ImageObjectLearner {

	
    public static void main (String[] argv) throws IOException {
              
        int[][] red = new int[64][64];
        int[][] green = new int[64][64];
        int[][] blue = new int[64][64];
        int[][] gray = new int[64][64];

        // 2048 is the size of the histogram of oriented gradients.
        // 256 is the size of the histogram of local binary patterns.
        double[] x = new double[2048 + 256];

        // weights to be learned
        // One weight vector for each of 13 categories
        double[][] weights = new double[13][2048 + 256];

        // ################ Learn ###########################
        
        // For all categories, use the learn function to update the weights
        for(int category = 0; category < 1; category++) // ## how can we do this ?
        {
            learn(category, red, green, blue, gray, x, weights);
            // it doesn't work, it's just an example
        }
        
        // ##################################################

        // compute the prediction and update the weights (for neural network based classification)
        //...
        //
        
        int prediction = NeuralNetworkLearner.makePrediction(weights, x);
        //NeuralNetworkLearner.updateWeights(weights, x, prediction, 0); // Need to find what is the target
        // I don't think we need/are able to update the weights here
        
        System.out.println("Catégorie "+prediction);
    }


    /**
     * Fill in the gray 2D array
     * 
     * @param red
     *            the red value of each pixel
     * @param green
     *            the green value of each pixel
     * @param blue
     *            the blue value of each pixel
     * @param gray
     *            the gray value computed from the RBG values
     */
    public static void grayScale(int[][] red, int[][] green, int[][] blue, int[][] gray) {
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                gray[i][j] = (int) (red[i][j] * 0.2125 + green[i][j] * 0.7154 + blue[i][j] * 0.0721);
            }
        }
    }
    
    /**
     * Use all images in the given category of the learning dataset to update the weights
     * 
     * @param category
     * @param red
     * @param green
     * @param blue
     * @param gray
     * @param x
     * @param w 
     * @throws java.io.IOException 
     */
    public static void learn(int category, int[][] red, int[][] green, int[][] blue, int [][] gray, double[] x, double[][] w) throws IOException {
        
        // For all images in the given category, we compute the grayscale and search its features
        for(int a = 0; a < 1; a++) // ## need to change this loop
        {
            File file = new File("../learningDataset/buildings/1315.jpg");
            BufferedImage img = ImageIO.read(file);

            // insert the RGB values into the red green blue arrays
            Color c;
            for (int i = 0; i < 64; i++) {
                    for (int j = 0; j < 64; j++) {
                            c = new Color(img.getRGB(i,j));
                            red[i][j] = c.getRed();
                            green[i][j] = c.getGreen();
                            blue[i][j] = c.getBlue();
                    }
            }

            // At this point, red green blue are filled with the next image.
            grayScale(red, green, blue, gray);
            // Fill in the x vector.
            Features.allFeatures(x, gray);
            
            
            // Update the weights while the prediction doesn't match the desired result
            // because we know the category of the image and we can have a prediction
            while(true) // the loop should be good
            {
                int prediction = NeuralNetworkLearner.makePrediction(w, x);
                
                if(prediction != category) {
                    NeuralNetworkLearner.updateWeights(w, x, prediction, category);
                }
                else {
                    break;
                }
            }
        }
    }
}
