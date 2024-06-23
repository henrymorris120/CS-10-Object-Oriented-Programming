import java.awt.*;

/**
 * A rectangle-shaped Shape
 * Defined by an upper-left corner (x1,y1) and a lower-right corner (x2,y2)
 * with x1<=x2 and y1<=y2
 * PS6
 * @author Henry Morris and Izzy Axinn, Dartmouth CS 10, Spring 2022
 */
public class Rectangle implements Shape {
	private int x1, x2, y1, y2; // upper left and lower right
	private Color color;		// color
	private String identifier;	// identifier

	/**
	 * An "empty" ellipse, with only one point set so far
	 */
	public Rectangle(int x1, int y1, Color color, String identifier) {
		setCorners(x1, y1, x1, y1);
		this.identifier = identifier;
		this.color = color;
	}

    /**
     * Rectangle defined by 2 corners
	 */
	public Rectangle(int x1, int y1, int x2, int y2, Color color, String identifier) {
		setCorners(x1, y1, x2, y2);
		this.color = color;
		this.identifier = identifier;
	}

	/**
	 * Redefines the rectangles based on new corners
	 */
	public void setCorners(int x1, int y1, int x2, int y2) {
		// Ensure correct upper left and lower right
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
	}

	/**
	 * move rectangle based on parameters
	 * @param dx		amount to move in x direction
	 * @param dy		amount to move in y direction
	 */
	@Override
	public void moveBy(int dx, int dy) {
		x1 = x1 + dx;
		y1 = y1+ dy;
		x2 = x2 + dx;
		y2 = y2 + dy;
	}

	/**
	 * @return color
	 */
	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * set color
	 * @param color The shape's color
	 */
	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * returns true or false depending on if point is in rectangle
	 * @param x 		x coordinate
	 * @param y			y coordinate
	 * @return			boolean indicating if x,y coordinate is in rectangle
	 */
	@Override
	public boolean contains(int x, int y) {
		Boolean containX = (x >= x1) && (x<= x2);
		Boolean containY = (y >= y1) && (y<= y2);
		return (containX && containY);
	}

	/**
	 * set color and draw rectangle
	 * @param g
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x1, y1, x2- x1, y2 - y1);
	}

	/**
	 * string form of shape
	 * used to reconstruct rectangle elsewhere in program
	 * @return
	 */
	public String toString() {
		return "rectangle "+x1+" "+y1+" "+x2+" "+y2+" "+ color.getRGB() + " " + identifier;
	}

	/**
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
}
