import java.awt.*;

/**
 * A line segment-shaped Shape
 * PS6
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */
public class Segment implements Shape {
	private int x1, y1, x2, y2;		// two endpoints
	private Color color;			// color
	private String identifier;		// identifier

	/**
	 * Initial 0-length segment at a point
	 */
	public Segment(int x1, int y1, Color color, String identifier) {
		this.x1 = x1; this.x2 = x1;
		this.y1 = y1; this.y2 = y1;
		this.color = color;
		this.identifier = identifier;
	}

	/**
	 * Complete segment from one point to the other
	 */
	public Segment(int x1, int y1, int x2, int y2, Color color, String identifier) {
		this.x1 = x1; this.y1 = y1;
		this.x2 = x2; this.y2 = y2;
		this.color = color;
		this.identifier = identifier;
	}

	/**
	 * Update the start (first point) of the segment
	 */
	public void setStart(int x1, int y1) {
		this.x1 = x1; this.y1 = y1;
	}
	
	/**
	 * Update the end (second point) of the segment
	 */
	public void setEnd(int x2, int y2) {
		this.x2 = x2; this.y2 = y2;
	}

	/**
	 * move segment based on parameters
	 * @param dx		amount to move in x direction
	 * @param dy		amount to move in y direction
	 */
	@Override
	public void moveBy(int dx, int dy) {
		x1 += dx; y1 += dy;
		x2 += dx; y2 += dy;
	}

	/**
	 * returns color
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
	}

	/**
	 * returns true or false depending on if the x,y coordinate is with 3 of segment
	 * @param x		x coordinate
	 * @param y		y coordinate
	 * @return		boolean indicating whether point is contained in segment within distance of 3
	 */
	@Override
	public boolean contains(int x, int y) {
		return pointToSegmentDistance(x, y, x1, y1, x2, y2) <= 3;
	}

	/**
	 * Helper method to compute the distance between a point (x,y) and a segment (x1,y1)-(x2,y2)
	 * http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
	 */
	public static double pointToSegmentDistance(int x, int y, int x1, int y1, int x2, int y2) {
		double l2 = dist2(x1, y1, x2, y2);
		if (l2 == 0) return Math.sqrt(dist2(x, y, x1, y1)); // segment is a point
		// Consider the line extending the segment, parameterized as <x1,y1> + t*(<x2,y2> - <x1,y1>).
		// We find projection of point <x,y> onto the line. 
		// It falls where t = [(<x,y>-<x1,y1>) . (<x2,y2>-<x1,y1>)] / |<x2,y2>-<x1,y1>|^2
		double t = ((x-x1)*(x2-x1) + (y-y1)*(y2-y1)) / l2;
		// We clamp t from [0,1] to handle points outside the segment.
		t = Math.max(0, Math.min(1, t));
		return Math.sqrt(dist2(x, y, x1+t*(x2-x1), y1+t*(y2-y1)));
	}

	/**
	 * Euclidean distance squared between (x1,y1) and (x2,y2)
	 */
	public static double dist2(double x1, double y1, double x2, double y2) {
		return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
	}


	/**
	 * set color and draw shape
	 * @param g
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.drawLine(x1, y1, x2, y2);
	}

	/**
	 * return identifier
	 * @return identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * set identifier
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * get the endpoint of the segment and return it
	 * @return	 endpoint
	 */
	public Point getEndPoint() {
		Point p = new Point(x2,y2);
		return p;
	}

	/**
	 * string form of shape
	 * used to reconstruct segment elsewhere in program
	 * @return
	 */
	@Override
	public String toString() {
		return "segment "+x1+" "+y1+" "+x2+" "+y2+" "+color.getRGB() + " " + identifier;
	}
}
