import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Editor
 * PS6
 * @author Henry Morris and Izzy Axinn, Dartmouth CS 10, Spring 2022
 */

public class Editor extends JFrame {
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged


	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};

		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});

		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {return sketch;}

	/**
	 * Sets to the current server sketch
	 * @param c
	 */
	public void setSketch(Sketch c) {sketch = c;}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		for(Shape s : sketch.getShapes()) {
			if (!s.getIdentifier().equals("deleted"))
				s.draw(g);
		}
		if (curr != null) {
			curr.draw(g);
		}
	}

	// Helpers for event handlers

	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {
		// check if in the moving mode and then check the shape
		// create new shape from the mouse press
		if (mode == Mode.DRAW) {
			if (shapeType.equals("freehand")) {
				ArrayList<Segment> lines = new ArrayList<Segment>();
				lines.add(new Segment(p.x, p.y, color, String.valueOf(sketch.getShapes().size())));
				curr = new Polyline(String.valueOf(sketch.getShapes().size()), color, lines);
				repaint();
			}
			if (shapeType.equals("segment")) {
				curr = new Segment(p.x, p.y, color, String.valueOf(sketch.getShapes().size()));
				drawFrom = p;
				moveFrom = p;
				repaint();
			}
			if (shapeType.equals("rectangle")) {
				curr = new Rectangle(p.x, p.y, color, String.valueOf(sketch.getShapes().size()));
				drawFrom = p;
				moveFrom = p;
				repaint();

			}
			if (shapeType.equals("ellipse")) {
				curr = new Ellipse(p.x, p.y, color, String.valueOf(sketch.getShapes().size()));
				drawFrom = p;
				moveFrom = p;
				repaint();
			}

		}
		// mark the shape clicked on as deleted
		// send message with information on what shape has been deleted using identifier
		if (mode == Mode.DELETE) {
			for (Shape shape: sketch.getShapes()) {
				if (shape.contains(p.x, p.y)) {
					if (!shape.getIdentifier().equals("deleted")) {
						comm.send("-d " + shape.getIdentifier());
					}
				}
			}

		}

		//  send message with information about what shape needs to be recolored and with what color
		if (mode == Mode.RECOLOR) {
			for (Shape shape: sketch.getShapes()) {
				if(shape.contains(p.x,p.y)) {
					if (!shape.getIdentifier().equals("deleted")) {
						comm.send("-r " + shape.getIdentifier() + " " + color.getRGB());
					}
				}
			}

		}
		// set curr to shape selected and call handle drag
		if (mode == Mode.MOVE) {
			for (Shape shape: sketch.getShapes()) {
				if (shape.contains(p.x,p.y)) {
					if (!shape.getIdentifier().equals("deleted")) {
						if (moveFrom == null) {
							moveFrom = p;
						}
						curr = shape;
						handleDrag(p);
					}
				}
			}
		}


	}

	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		if (mode == Mode.DRAW) {
			if (shapeType.equals("segment")) {
				((Segment)curr).setEnd(p.x, p.y);
			}
			else if (shapeType.equals("rectangle")) {
				((Rectangle)curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
			}
			else if (shapeType.equals("ellipse")) {
				((Ellipse)curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
			}
			else if (shapeType.equals("freehand")) {
				((Polyline)curr).setLines(p);
			}
			repaint();

		}
		else if (mode == Mode.MOVE) {
			if (curr != null) {
				curr.moveBy(p.x -moveFrom.x, p.y - moveFrom.y);
				moveFrom = p;
				repaint();
			}
		}
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it
	 */
	private void handleRelease() {
		// send message indicating a new shape has been created or an existing shape has been moved
		// set curr and moveFrom to null
		if(curr != null) {
			if (mode == Mode.DRAW) {
				comm.send("-s " + curr.toString());
			} else if (mode == Mode.MOVE) {
				comm.send("-m " + curr.toString());
			}
			moveFrom = null;
			curr = null;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});
	}
}
