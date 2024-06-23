The purpose of this project is to implement a point quadtree data structure to efficiently detect if a point was clicked on or if two points collided into each other - the premise is similar to a binary search but now in 2 dimensions. The exact variation of this data structure is an extension of the classic approach described by Finkel and Bentley in "Quad Trees: A Data Structure for Retrieval on Composite Keys", *Acta Informatica* 4, 1—9, 1974.

**PointQuadTree** 

    Stores points in a tree that enables fast detection if a point is within a circle around the mouse 
    for DotTreeGUI or if points collide for CollisionGUI.

**Geometry** 

    Helper functions for testing point-in-circle and circle-intersects-rectangle

**InteractiveGUI** 

    Extends our ImageGUI from a prior lecture, but adds a timer (to drive animation for 
    CollisionGUI) and some call back methods that can be overrided. See description in file.

**MovingGUI** 

    Extends the Java-provided Point class for CollisionGUI. See descripti

**DotTreeGUI**

    Extends InteractiveGUI.
