import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A server to handle sketches: getting requests from the clients,
 * updating the overall state, and passing them on to the clients
 *
 * PS6
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */
public class SketchServer {
	private ServerSocket listen;						// for accepting connections
	private ArrayList<SketchServerCommunicator> comms;	// all the connections with clients
	private Sketch sketch;								// the state of the world

	public SketchServer(ServerSocket listen) {
		this.listen = listen;
		sketch = new Sketch();
		comms = new ArrayList<SketchServerCommunicator>();
	}

	/**
	 * returns current sketch
	 * @return Sketch
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Synchronized method to add shapes into a sketch
	 * @param shape
	 */
	public synchronized void addToSketch(Shape shape){
		sketch.add(shape);
	}

	/**
	 * The usual loop of accepting connections and firing off new threads to handle them
	 */
	public void getConnections() throws IOException {
		System.out.println("server ready for connections");
		while (true) {
			SketchServerCommunicator comm = new SketchServerCommunicator(listen.accept(), this);
			comm.setDaemon(true);
			comm.start();
			addCommunicator(comm);
		}
	}


	/**
	 * Adds the communicator to the list of current communicators
	 */
	public synchronized void addCommunicator(SketchServerCommunicator comm) {
		comms.add(comm);
	}

	/**
	 * Removes the communicator from the list of current communicators
	 */
	public synchronized void removeCommunicator(SketchServerCommunicator comm) {
		comms.remove(comm);
	}

	/**
	 * Sends the message from the one communicator to all (including the originator)
	 */
	public synchronized void broadcast(String msg) {
		for (SketchServerCommunicator comm : comms) {
			comm.send(msg);
		}
	}

	/**
	 * Returns number of current people on server
	 * @return
	 */
	public int getNumConnections()
	{
		return comms.size();
	}

	public static void main(String[] args) throws Exception {
		new SketchServer(new ServerSocket(4242)).getConnections();
	}
}
