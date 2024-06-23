import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Henry Morris, Spring 2022, CS 10
 * Problem Set 2
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		double px = p2.getX();
		double py = p2.getY();

		// check if p2 is in quadrant 1
		// if there is a child in quadrant 1, recursively call function with that child, else create new tree and add it
		if (px >= this.point.getX() && py < this.point.getY()) {
			if(this.hasChild(1)){
				this.getChild(1).insert(p2);
			}
			else {
				this.c1 = new PointQuadtree<E>(p2, (int)(this.point.getX()), this.getY1(), this.getX2(), (int)(this.point.getY()));
			}

		}
		// check if p2 is in quadrant 2
		// if there is a child in quadrant 2, recursively call function with that child, else create new tree and add it
		else if (py < this.point.getY() && px < this.point.getX()) {
			if(this.hasChild(2)){
				this.getChild(2).insert(p2);
			}
			else {
				this.c2 = new PointQuadtree<E>(p2, this.getX1(), this.getY1(), (int)(this.point.getX()), (int)(this.point.getY()));
			}
		}
		// check if p2 is in quadrant 3
		// if there is a child in quadrant 3, recursively call function with that child, else create new tree and add it
		else if (px < this.point.getX() && py >= this.point.getY()) {
			if(this.hasChild(3)){
				this.getChild(3).insert(p2);
			}
			else {
				this.c3 = new PointQuadtree<E>(p2, this.getX1(), (int)(this.point.getY()), (int)(this.point.getX()), this.getY2());
			}
		}
		// check if p2 is in quadrant 4
		// if there is a child in quadrant 4, recursively call function with that child, else create new tree and add it
		else if (px >= this.point.getX() && py >= this.point.getY()) {
			if(this.hasChild(4)){
				this.getChild(4).insert(p2);
			}
			else {
				this.c4 = new PointQuadtree<E>(p2, (int)(this.point.getX()), (int)(this.point.getY()), this.getX2(), this.getY2());
			}
		}
		// check if anchor point and replace point if it is
		else if (px == this.point.getX() && py == this.point.getY()) {
			this.point = p2;
		}


	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants) and return size
	 */
	public int size() {
		// set size to 1 for the head of tree
		// recursively call function for each child
		int size = 1;
		if (this.hasChild(1)) {
			size = size + this.getChild(1).size();
		}
		if (this.hasChild(2)) {
			size = size + this.getChild(2).size();
		}
		if (this.hasChild(3)) {
			size = size + this.getChild(3).size();

		}
		if (this.hasChild(4)) {
			size = size + this.getChild(4).size();
		}
		return size;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants) and returns it
	 */
	public List<E> allPoints() {
		// create array list for all the points in the quad tree
		List<E> points = new ArrayList<E>();
		addAllPoints(points);
		return points;

	}

	/**
	 * Adds each point in quadtree to the list parameter points and return points after filled up
	 * @param points 	list to add each point into
	 */
	public void addAllPoints(List<E> points) {
		// add point and then recursively call for each child
		points.add(0, this.point);
		if (this.hasChild(1)) {
			this.getChild(1).addAllPoints(points);
		}
		if (this.hasChild(2)) {
			this.getChild(2).addAllPoints(points);
		}
		if (this.hasChild(3)) {
			this.getChild(3).addAllPoints(points);
		}
		if (this.hasChild(4)) {
			this.getChild(4).addAllPoints(points);
		}
	}

	/**
	 * Uses the quadtree to find all points within the circle and return list with these points
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// create new array list that holds each point in the circle
		List<E> pointsClicked = new ArrayList<E>();
		findClickedPoints(cx, cy, cr, pointsClicked);
		return pointsClicked;
	}

	/**
	 * Adds each point in quadtree clicked on by mouse to the list parameter pointsClicked and return it after filled up
	 * @param cx 			center x coordinate of mouse click
	 * @param cy 			center y coordinate of mouse click
	 * @param cr 			circle radius of mouse click
	 * @param pointsClicked list to add points in the circle into
	 */
	public void findClickedPoints(double cx, double cy, double cr, List<E> pointsClicked) {
		// if the circle is inside or intersects the rectangle that makes up the maximums of a point,
		// check if that point is inside the circle, adding it to pointsClicked if true,
		// and recursively call method for each child
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, this.getX1(), this.getY1(), this.getX2(), this.getY2())){
			if (Geometry.pointInCircle(this.point.getX(), this.point.getY(), cx, cy, cr)) {
				pointsClicked.add(0, this.point);
			}
			if (this.hasChild(1)) {
				this.getChild(1).findClickedPoints(cx, cy, cr, pointsClicked);
			}
			if (this.hasChild(2)) {
				this.getChild(2).findClickedPoints(cx, cy, cr, pointsClicked);
			}
			if (this.hasChild(3)) {
				this.getChild(3).findClickedPoints(cx, cy, cr, pointsClicked);
			}
			if (this.hasChild(4)) {
				this.getChild(4).findClickedPoints(cx, cy, cr, pointsClicked);
			}
		}
	}
}
