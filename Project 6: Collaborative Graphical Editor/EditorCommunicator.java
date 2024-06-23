import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles communication to/from the server for the editor
 *
 * PS6
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;        // to server
	private BufferedReader in;        // from server
	protected Editor editor;        // handling communication for
	Scanner console;

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		console = new Scanner(System.in);
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		} catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
		//Asks for current sketch
		send("-g");
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			String line;
			while ((line = in.readLine()) != null) {//Reads current line
				String[] strings = line.split(" ");
				System.out.println(line);
				if(strings[0].equals("-g")) {
					Sketch c = new Sketch();
					String[] shapes;
					shapes = line.substring(3).split(",");//Splits line into different strings of shape parameters
					for(String shape : shapes) {
						shape = shape.strip();
						c.add(makeShape(shape.split(" "))); //Makes each shape and adds it to local sketch
					}
					editor.setSketch(c); //Setting the sketch
				}
				else if (strings[0].equals("-m")) { //Move shape
					String[] shapes = line.substring(3).split(" ");
					int ID = Integer.parseInt(strings[7]);
					if(strings[1].equals("polyline")) {
						ID = Integer.parseInt(strings[2]);
					}
					editor.getSketch().getShapes().set(ID, makeShape(shapes));
				}
				else if (strings[0].equals("-r")) {//Recolors a shape
					editor.getSketch().getShapes().get(Integer.parseInt(strings[1])).setColor(new Color(Integer.parseInt(strings[2])));
				}
				else if (strings[0].equals("-d")) {//Deletes a shape
					editor.getSketch().getShapes().get(Integer.parseInt(strings[1])).setIdentifier("deleted");
				}
				else if (strings[0].equals("-s")) {//Adds a shape
					String[] shapes = line.substring(3).split(" ");
					editor.getSketch().add(makeShape(shapes));
				}
				// repaint
				editor.repaint();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("server hung up");
		}
	}

	/**
	 * Takes a array of strings that serve as the parameter for each shape and creates the shape
	 * @param strings
	 * @return shape
	 */
	public synchronized Shape makeShape(String[] strings) {
		if (strings[0].equals("rectangle")) {
			Rectangle rect = new Rectangle(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), new Color(Integer.parseInt(strings[5])), strings[6]);
			return rect;
		} else if (strings[0].equals("ellipse")) {
			Ellipse ellip = new Ellipse(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), new Color(Integer.parseInt(strings[5])), strings[6]);
			return ellip;

		} else if (strings[0].equals("segment")) {
			Segment seg = new Segment(Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), new Color(Integer.parseInt(strings[5])), strings[6]);
			return seg;

		} else if (strings[0].equals("polyline")) {
			ArrayList<Segment> lines = new ArrayList<Segment>();
			for (int i = 1; i < Integer.parseInt(strings[3]); i++) {
				int j = i * 7;
				Segment seg = new Segment(Integer.parseInt(strings[5 + j]), Integer.parseInt(strings[6 + j]), Integer.parseInt(strings[7 + j]), Integer.parseInt(strings[8 + j]), new Color(Integer.parseInt(strings[2])), strings[1]);
				lines.add(seg);
			}
			Polyline poly = new Polyline(strings[1], new Color(Integer.parseInt(strings[2])), lines);
			return poly;
		}
		return null;
	}


}
