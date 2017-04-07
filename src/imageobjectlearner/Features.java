package imageobjectlearner;

public class Features {
    /**
     * Fill in x with image features
     * @param x vector of image features
     * @param gray grayscale image
     */
    public static void allFeatures(double[] x, int[][] gray) {
            int i = 0;  // index into the x vector of feature values
            double[] a = histogramOfOrientedGradients(gray, 8, 4, 4);
            for (double v : a) {
                    x[i++] = v;
            }
            a = localBinaryPattern(gray);
            for (double v : a) {
                    x[i++] = v;
            }
    }
	
    /**
     * For a 64x64 image this will return a double array with length
     * numberOfBins*(64/cellSize)*(64/cellSize) IF 64%(cellSize*blockSize) == 0
     * 
     * It will consist of (64%(cellSize*blockSize))^2 unit vectors.
     * 
     * @param gray grayscale image
     * @param numberOfBins number of gradients per cell
     * @param blockSize blocksize*blocksize cells in each block
     * @param cellSize cellSize*cellSize pixels in each cell
     * @return the histogram of oriented gradients (look it up)
     */
    public static double[] histogramOfOrientedGradients(int[][] gray, int numberOfBins, int blockSize, int cellSize) {
        double binWidth = (2.0 * Math.PI) / numberOfBins; // 0 to 360

        int width = gray[0].length;
        int height = gray.length;

        // 1. Calculate partial differences
        float[][] direction = new float[height][width];
        float[][] magnitude = new float[height][width];

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {

                int p1 = gray[i - 1][j + 1];
                int p2 = gray[i][j + 1];
                int p3 = gray[i + 1][j + 1];
                int p4 = gray[i - 1][j - 1];
                int p5 = gray[i][j - 1];
                int p6 = gray[i + 1][j - 1];
                int p7 = gray[i + 1][j];
                int p8 = gray[i - 1][j];

                float h = ((p1 + p2 + p3) - (p4 + p5 + p6)) * 0.166666667f;
                float v = ((p6 + p7 + p3) - (p4 + p8 + p1)) * 0.166666667f;

                direction[i][j] = (float) Math.atan2(v, h);
                magnitude[i][j] = (float) Math.sqrt(h * h + v * v);
            }
        }

        // 2. Compute cell histograms
        int cellCountX = (int) Math.floor(height / (double) cellSize);
        int cellCountY = (int) Math.floor(width / (double) cellSize);
        double[][][] histograms = new double[cellCountX][cellCountY][];

        for (int i = 0; i < cellCountX; i++) {
            for (int j = 0; j < cellCountY; j++) {
                // Compute the histogram
                double[] histogram = new double[numberOfBins];

                int startCellX = i * cellSize;
                int startCellY = j * cellSize;

                // for each pixel in the cell
                for (int x = 0; x < cellSize; x++) {
                    for (int y = 0; y < cellSize; y++) {
                        double ang = direction[startCellY + y][startCellX + x];
                        double mag = magnitude[startCellY + y][startCellX + x];

                        // Get its angular bin
                        int bin = (int) Math.floor((ang + Math.PI) * binWidth);

                        histogram[bin] += mag;
                    }
                }

                histograms[i][j] = histogram;
            }
        }

        // 3. Group the cells into larger, normalized blocks
        int blocksCountX = (int) Math.floor(cellCountX / (double) blockSize);
        int blocksCountY = (int) Math.floor(cellCountY / (double) blockSize);

        double[] blocks = new double[blocksCountX * blocksCountY * blockSize * blockSize * numberOfBins];
        int d = 0; // index into blocks
        for (int i = 0; i < blocksCountX; i++) {
            for (int j = 0; j < blocksCountY; j++) {
                double[] block = new double[blockSize * blockSize * numberOfBins];

                int startBlockX = i * blockSize;
                int startBlockY = j * blockSize;
                int c = 0; // index into block
                double ss = 0; // sum of squared values

                // for each cell in the block
                for (int x = 0; x < blockSize; x++) {
                    for (int y = 0; y < blockSize; y++) {
                        double[] histogram = histograms[startBlockX + x][startBlockY + y];

                        // Copy all histograms to the block vector
                        for (int k = 0; k < histogram.length; k++) {
                            block[c++] = histogram[k];
                            ss += histogram[k] * histogram[k];
                        }
                    }
                }

                // normalize the values in this block
                ss = Math.sqrt(ss + 1e-10);
                for (double v : block) {
                    blocks[d++] = v / ss;
                }
            }
        }

        return blocks;
    }
    
    /**
     * This will return a double array with length 256.
     * The double array will be a unit vector.
     * 
     * @param gray grayscale image
     * @return the histogram of local binary patterns (look it up)
     */
    public static double[] localBinaryPattern(int[][] gray) {
        int width = gray[0].length;
        int height = gray.length;
        
        double[] g = new double[256];
        for (int x = 1; x < height - 1; x++) {
            for (int y = 1; y < width - 1; y++) {
                int pixel = gray[x][y];
                int sum = 0;
                if (pixel < gray[x - 1][y - 1])    sum += 128;
                if (pixel < gray[x - 1][y])        sum += 64;
                if (pixel < gray[x - 1][y + 1])    sum += 32;
                if (pixel < gray[x][y + 1])        sum += 16;
                if (pixel < gray[x + 1][y + 1])    sum += 8;
                if (pixel < gray[x + 1][y])        sum += 4;
                if (pixel < gray[x + 1][y - 1])    sum += 2;
                if (pixel < gray[x][y - 1])        sum += 1;
                g[sum]++;
            }
        }
        
        // normalize g
        double ss = 0;
        for (double v : g) {
            ss += v * v;
        }
        ss = Math.sqrt(ss);
        for (int i = 0; i < 256; i++) {
            g[i] = g[i] / ss;
        }
        
        return g;
    }
}
