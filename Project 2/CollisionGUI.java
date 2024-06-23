import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 *
 * @author Henry Morris, Spring 2022, CS 10
 * Problem Set 2
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}


	/**
	 * test collisions 0
	 * hardcoded point locations for 800x600
	 */
	private void test0() {
		int bad = 0;
		// set blobs to  new empty array list
		// add 5 blob objects to blobs, 2 of which will be colliding with each other
		blobs = new ArrayList<>();
		Blob blob1 = new Wanderer(50, 50, 25); // collides with blob2
		blobs.add(0, blob1);
		Blob blob2 = new Wanderer(70, 50, 30); // collides with blob1
		blobs.add(0, blob2);
		Blob blob3 = new Wanderer(150, 150, 10); // collides with no blob
		blobs.add(0, blob3);
		Blob blob4 = new Wanderer(300, 200, 30); // collides with no blob
		blobs.add(0, blob4);
		Blob blob5 = new Wanderer(500, 30, 25); // collides with no blob
		blobs.add(0, blob5);
		findColliders();
		if (colliders.size() != 2) bad ++;
		if (bad==0) System.out.println("test 0 passed!");
	}



	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else if (k =='0') {
			test0();
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// Ask all the blobs to draw themselves in black
		// Ask the colliders to draw themselves in red

		if (blobs != null) {
			g.setColor(Color.black);
			for (Blob blob: blobs) {
				blob.draw(g);
			}
		}
		if (colliders != null) {
			g.setColor(Color.red);
			for (Blob blob: colliders) {
				blob.draw(g);
			}
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// Set colliders equal to a new array list
		colliders = new ArrayList<Blob>();
		// if the blobs array list is not empty
		if (blobs != null) {
			// create quadtree called collisions with the first blob in blobs
			PointQuadtree<Blob> collisions = new PointQuadtree<Blob>(blobs.get(0), 0, 0, width, height);
			// insert every other blob in blobs into the quadtree collisions
			for (int i = 1; i < blobs.size(); i++) {
				collisions.insert(blobs.get(i));
			}
			// Iterates through every blob in blobs
			// Set coll to the the list returned when using the quadtree method findInCircle
			// with the x, y, and r of the blob being interated as parameters
			// If the size of coll is greater than 1, add blob to colliders
			for (Blob blob: blobs) {
				List<Blob> coll = collisions.findInCircle(blob.getX(), blob.getY(), blob.getR());
				if (coll.size() > 1) {
					colliders.add(0, blob);
				}
			}

		}

	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}
