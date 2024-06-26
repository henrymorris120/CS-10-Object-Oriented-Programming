# Collaborative Graphical Editor
This project builds a collaborative graphical editor — akin to Google Docs' ability to have multiple simultaneous editors of the same document. In both cases, multiple clients connect to a server, and whatever editing any of them does, the others see. This code handles multiple objects at a time, and can draw rectangles, line segments, and "freehand" shapes in addition to ellipses. The architecture involves each client editor having a thread for talking to the sketch server, along with a main thread for user interaction. The server has a main thread to get the incoming requests to join the shared sketch, along with separate threads for communicating with the clients. The client tells the server about its user's drawing actions. The server then tells all the clients about all the drawing actions of each of them. To make this work nicely, a client doesn't just do the drawing actions on its own. Instead, it requests the server for permission to do an action, and the server then tells all the clients (including the requester) to do it. So rather than actually recoloring a shape, the client tells the server "I'd like to color this shape that color", and the server then tells all the clients to do just that. That way, the server has the one unified, consistent global view of the sketch, and keeps all the clients informed.

**Editor**

        Handles GUI-based drawing interaction
        
**EditorCommunicator**

        For messages to/from the server
        
**SketchServer**

        Central keeper of the master sketch; synchronizing the various Editors
        
**SketchServerCommunicator**

        For messages to/from a single editor (one for each such client)
        
**Shape**

        Interface for a graphical shape (with color), with implementations Ellipse, Polyline, Rectangle, and Segment
    
