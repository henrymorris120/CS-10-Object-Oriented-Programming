import javax.sound.sampled.Line;
import java.awt.*;
import java.util.ArrayList;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * PS6
 * @author Henry Morris and Izzy Axinn, Dartmouth CS 10, Spring 2022
 */
public class Polyline implements Shape {
	private String identifier;				// identifier for the shape
	private Color color;					// color of the shape
	private ArrayList<Segment> lines;		// array list of lines that make up polyline

	/**
     * create a polyline from a list of points
	 */
	public Polyline (String identifier, Color color, ArrayList<Segment> lines) {
		this.lines = lines;
		this.identifier = identifier;
		this.color = color;
	}

	/**
	 * move poly line
 	 * @param dx	amount to move in x direction
	 * @param dy	amount to move in y direction
	 */
	@Override
	public void moveBy(int dx, int dy) {
		for (Segment line: lines) {
			line.moveBy(dx,dy);
		}
	}

	/**
	 * returns the color
	 * @return color
	 */
	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * sets the color
	 * @param color The shape's color
	 */
	@Override
	public void setColor(Color color) {
		this.color = color;
		for (Segment line: lines) {
			line.setColor(color);
		}
	}

	/**
	 * check if a point is contained in polyline
	 * @param x		x coordinate
	 * @param y		y coordinate
	 * @return		boolean
	 */
	@Override
	public boolean contains(int x, int y) {
		// returns true if true for any line in poly line
		for (Segment line: lines) {
			if (line.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * draw polyline
	 * @param g
	 */
	@Override
	public void draw(Graphics g) {
		// draw each line
		for (Segment line: lines) {
			line.draw(g);
		}
	}

	/**
	 * string form of shape
	 * used to reconstruct poly line elsewhere in program
	 * @return
	 */
	@Override
	public String toString() {
		String str = "polyline " + identifier + " " + color.getRGB() + " " + lines.size();
		for (Segment point: lines) {
			str = str + " " + point.toString();
		}
		return str;
	}

	/**
	 * used to add a new line to polyline from the endpoint of line before
	 * @param p point
	 */
	public void setLines(Point p) {
		lines.add(new Segment(lines.get(lines.size()-1).getEndPoint().x, lines.get(lines.size()-1).getEndPoint().y, p.x, p.y, color, identifier));
	}

	/**
	 * gets the identifier
	 * @return identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * sets the identifier
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
