import javax.imageio.ImageIO;
import javax.swing.plaf.synth.Region;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * PS-1, Dartmouth CS 10, Spring 2022
 *
 * @author Henry Morris
 */
public class RegionFinder {
    // how similar a pixel color must be to target color, to belong to a region
    private static final int maxColorDiff = 20;
    private static final int minRegion = 50; 				// how many points in a region to be worth considering

    private BufferedImage image;                            // the image in which to find regions
    private BufferedImage recoloredImage;                   // the image with identified regions recolored

    private BufferedImage visited;
    private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
    // so the identified regions are in a list of lists of points

    public RegionFinder() {
        this.image = null;
    }

    public RegionFinder(BufferedImage image) {
        this.image = image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getRecoloredImage() {
        return recoloredImage;
    }

    /**
     * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
     * @param targetColor       color trying to find
     */
    public void findRegions(Color targetColor) {
        // create blank image that is all black and has dimensions of of instance variable image
        visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        // create array list to hold each region in image that has similar color to targetColor
        regions = new ArrayList<ArrayList<Point>>();
        // check every point
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // set c to color of pixel
                Color c = new Color(image.getRGB(x, y));
                // check if the point has been visited and colorMatch is true,
                // otherwise move to the next point in nested loop
                if (visited.getRGB(x, y) == 0 && colorMatch(targetColor, c)) {
                    // create array list for the region and for and the neighbors of each point in the region
                    // add point to each array list and mark it as visited by changing the rgb of that pixel in visited
                    ArrayList<Point> region = new ArrayList<Point>();
                    ArrayList<Point> neighbors = new ArrayList<Point>();
                    Point point = new Point(x, y);
                    region.add(point);
                    neighbors.add(point);
                    visited.setRGB(point.x, point.y, 1);
                    // continue in loop until the array list neighbors has a length of zero
                    while (neighbors.size() > 0) {
                        // pop the last point in neighbors and use nested loop to create point for all its neighbors
                        Point p = neighbors.remove(neighbors.size() - 1);
                        for (int j = Math.max(0, p.y - 1); j < Math.min(image.getHeight(), p.y + 2); j++) {
                            for (int i = Math.max(0, p.x - 1); i < Math.min(image.getWidth(), p.x + 2); i++) {
                                Point neighborPoint = new Point(i, j);
                                Color neighborColor = new Color(image.getRGB(i, j));
                                // if a neighborPoint has not been visited and colorMatch is true,
                                // add it to region and neighbors array lists
                                if (visited.getRGB(i, j) == 0 && colorMatch(targetColor, neighborColor)) {
                                    visited.setRGB(i, j, 1);
                                    region.add(neighborPoint);
                                    neighbors.add(neighborPoint);
                                }

                            }
                        }

                    }
                    // if the size of region is greater than or equal to minRegion, add region to regions
                    if (region.size() >= minRegion) {
                        regions.add(region);
                    }

                }

            }

        }
    }




    /**
     * Tests whether the two colors are "similar enough" (subject to the maxColorDiff threshold)
     * returns true or false
     * @param c1        // one of the two colors being compared
     * @param c2        // the other color being compared
     */
     private static boolean colorMatch(Color c1, Color c2) {
         // finds the absolute value difference between the red, green, and blue values of c1 and c2
         int r = Math.abs(c1.getRed() - c2.getRed());
         int g = Math.abs(c1.getGreen() - c2.getGreen());
         int b = Math.abs(c1.getBlue() - c2.getBlue());
         if(r <= maxColorDiff && g <= maxColorDiff && b <= maxColorDiff)
            return true;
         else {
             return false;
         }
     }

     /**
      * Returns the largest region detected (if any region has been detected)
      */
     public ArrayList<Point> largestRegion() {
         // check if regions is empty or not
         if (regions.size() > 0) {
             ArrayList<Point> largestR;
             largestR = regions.get(0);
             // iterate through every regions in regions and change largestR if a region has a greater size than it
             for (ArrayList<Point> region: regions) {
                 if (region.size() > largestR.size()) {
                     largestR = region;
                 }
             }
             return largestR;
         } else {
             return null;
         }
     }



     /**
      * Sets recoloredImage to be a copy of image, but with each region a uniform random color
      */
     public void recolorImage() {
         // First copy the original and name into the instance variable recoloredImage and check if regions is empty
         recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null),
             image.getColorModel().isAlphaPremultiplied(), null);
         if (largestRegion() != null) {
             // assign each region a random color
             // for each point in each region, set the corresponding point in recolored image to have that random color
             for (ArrayList<Point> region : regions) {
                 int v = (int)(Math.random() * 16777216);
                 Color randV = new Color(v);
                 for (Point point : region) {
                     recoloredImage.setRGB(point.x, point.y, randV.getRGB());
                 }
             }
         }
     }

    /**
     * Sets recoloredImage to be a copy of image, but the array list of points returned from largestRegion()
     * has a uniform color
     *
     * @param color             color to be used to fill in recoloredImage
     */
    public void recolorLargestRegionImage(Color color) {
        // First copy image into the instance variable recoloredImage
        recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null),
                image.getColorModel().isAlphaPremultiplied(), null);
        // check if largestRegion() returns null
        if (largestRegion() != null) {
            // for each point in the array list returned from largestRegion(), set the corresponding point
            // in recoloredImage with the parameter color
            for (Point point : largestRegion()) {
                recoloredImage.setRGB(point.x, point.y, color.getRGB());
            }
        }
    }
}
