import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing
 * PS-1
 *
 * @author Henry Morris, CS 10, Spring 2022
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor;	            // the color to put into painting from the "brush" or in the recolored image
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('p', 'r',)
	 * if display variable is neither 'p' or 'r,' it is assumed display variable equals 'w' for webcam
	 */
	@Override
	public void draw(Graphics g) {
		// process image and draw painting if displayMode = 'p'
		if (displayMode == 'p') {
			processImage();
			g.drawImage(painting, 0, 0, null);
		}
		// process image and draw recolored image if displayMode = 'r' and targetColor does not equal null
		else if (displayMode == 'r' && targetColor != null) {
			processImage();
			g.drawImage(finder.getRecoloredImage(), 0, 0, null);
		}
		else {
			// draws just the webcam image if displayMode equals 'w' or no targetColor has been when
			// displayMode = 'r', meaning there can be no recoloredImage
			super.draw(g);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// check if a targetColor has been chosen
		if (targetColor != null) {
			// create finder with image parameter
			finder = new RegionFinder(image);
			// find regions using targetColor as parameter
			finder.findRegions(targetColor);
			// sets finder.recoloredImage to the image of the webcam with the largest region of the targetColor
			// filled in with paintColor
			finder.recolorLargestRegionImage(paintColor);
			// update painting if displayMode equals 'p' and there is a least one region found
			if (displayMode == 'p' && finder.largestRegion() != null) {
				for (Point point : finder.largestRegion()) {
					painting.setRGB(point.x, point.y, paintColor.getRGB());
				}
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set targetColor
	 * randomly sets paintColor
	 * sets target color to the color of the pixel the mouse presses on
	 *
	 * @param x				x coordinate where the mouse presses
	 * @param y				y coordinate where the mouse presses
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (displayMode == 'w') {
			int v = (int) (Math.random() * 16777216);
			paintColor = new Color(v);
			targetColor = new Color(image.getRGB(x, y));
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		// save the recolored image when the 'o' key is pressed if there is a targetColor and in the correct displayMode
		else if (k == 'o' && displayMode == 'r' && targetColor != null) {
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		// save the painting when the 's' key is pressed if in the correct displayMode
		else if (k == 's' && displayMode == 'p') {
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
