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

        File file = new File("...file location...");
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

        // compute the prediction and update the weights (for neural network based classification)
        //...
        //
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
}
