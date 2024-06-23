import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * PS6
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {out.println(msg);}

	/**
	 *Takes in an input from an editor and does the proper command
	 * @param line
	 */
	public synchronized void getAndDoCommand(String line) {
		String[] strings = line.split(" ");
		// check what shape has a command on it
		// Move shape in the sketch for server
		if(strings[0].equals("-m")) {
			if (strings[1].equals("rectangle")) {
				Rectangle rect = new Rectangle(Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), new Color(Integer.parseInt(strings[6])), strings[7]);
				int ID = Integer.parseInt(strings[7]);
				server.getSketch().getShapes().set(ID, rect);
			}
			else if (strings[1].equals("ellipse")) {
				Ellipse ellip = new Ellipse(Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), new Color(Integer.parseInt(strings[6])), strings[7]);
				int ID = Integer.parseInt(strings[7]);
				server.getSketch().getShapes().set(ID, ellip);
			}
			else if (strings[1].equals("segment")) {
				Segment seg = new Segment(Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), new Color(Integer.parseInt(strings[6])), strings[7]);
				int ID = Integer.parseInt(strings[7]);
				server.getSketch().getShapes().set(ID, seg);
			}
			else if (strings[1].equals("polyline")) {
				ArrayList<Segment> lines = new ArrayList<Segment>();
				for (int i=0; i < Integer.parseInt(strings[4]); i ++) {
					int j = i * 7;
					Segment seg = new Segment(Integer.parseInt(strings[6+j]), Integer.parseInt(strings[7+j]), Integer.parseInt(strings[8+j]), Integer.parseInt(strings[9+j]), new Color(Integer.parseInt(strings[3])), strings[2]);
					lines.add(seg);
				}
				Polyline poly = new Polyline(strings[2], new Color(Integer.parseInt(strings[3])), lines);
				server.getSketch().getShapes().set(Integer.parseInt(strings[2]), poly);
			}
			// sends out command line
			server.broadcast(line);
		}
		// recolors shape in the sketch for server
		// sends out command
		if(strings[0].equals("-r")) {
			server.getSketch().getShapes().get(Integer.parseInt(strings[1])).setColor(new Color(Integer.parseInt(strings[2])));
			server.broadcast(line);
		}
		// check if the current sketch is empty
		// gets the current sketch to the editor that has just joined if needed
		if(strings[0].equals("-g")) {
			boolean empty = true;
			for (Shape shape: server.getSketch().getShapes()) {
				if (!shape.getIdentifier().equals("deleted")) {
					empty = false;
					break;
				}
			}
			if(empty) {
				send("There is no current sketch on the server");
				if (server.getSketch().toString().length() != 0) {
					send("-g " + server.getSketch());
				}
			}
			else {
				send("-g " + server.getSketch());
			}


		}
		// deletes a shape in the sketch for the server
		// sends out command to all communicators
		if(strings[0].equals("-d")) {
			server.getSketch().getShapes().get(Integer.parseInt(strings[1])).setIdentifier("deleted");
			server.broadcast(line);
		}
		// check what shape is being added
		// add the shape to sketch for the server
		// send out the command to all the communicators
		if(strings[0].equals("-s")) {//Adds a shape
			if (strings[1].equals("rectangle")) {
				Rectangle rect = new Rectangle(Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), new Color(Integer.parseInt(strings[6])), strings[7]);
				server.addToSketch(rect);
			}
			else if (strings[1].equals("ellipse")) {
				Ellipse ellip = new Ellipse(Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), new Color(Integer.parseInt(strings[6])), strings[7]);
				server.addToSketch(ellip);

			}
			else if (strings[1].equals("segment")) {
				Segment seg = new Segment(Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), new Color(Integer.parseInt(strings[6])), strings[7]);
				server.addToSketch(seg);

			}
			else if (strings[1].equals("polyline")) {
				ArrayList<Segment> lines = new ArrayList<Segment>();
				for (int i=0; i < Integer.parseInt(strings[4]); i ++) {
					int j = i * 7;
					Segment seg = new Segment(Integer.parseInt(strings[6+j]), Integer.parseInt(strings[7+j]), Integer.parseInt(strings[8+j]), Integer.parseInt(strings[9+j]), new Color(Integer.parseInt(strings[3])), strings[2]);
					lines.add(seg);
				}
				Polyline poly = new Polyline(strings[2], new Color(Integer.parseInt(strings[3])), lines);
				server.addToSketch(poly);
			}
			server.broadcast(line);
		}
	}

	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");

			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			out.println("The server has " + server.getNumConnections() + " current connections");

			// Keep getting and handling messages from the client
			String line;
			while ((line = in.readLine()) != null) {
				getAndDoCommand(line);
			}

			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
