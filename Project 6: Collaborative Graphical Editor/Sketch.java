import java.util.ArrayList;
/**
 * Sketch
 * PS6
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */

public class Sketch {
    private ArrayList<Shape> shapes;        // list holding all the shapes drawn

    /**
     * Constructor for sketch, sets shapes to new array list
     */
    public Sketch() {
        this.shapes = new ArrayList<Shape>();
    }

    /**
     * Returns the array list holding the shapes
     * @return     array list shapes
     */
    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    /**
     * Returns the string form of the list of shapes
     * @return string of array list shapes
     */
    @Override
    public String toString() {
        String str = "";
        // go through every shape
        for (int i = 0; i < shapes.size(); i++) {
            // check if on the last shape in list
            if (i!= shapes.size()-1) {
                str = str + shapes.get(i).toString() + ", ";
            }
            else {
                str = str + shapes.get(i).toString();
            }
        }
        return str;
    }

    /**
     * add shape to the end of shapes
     * @param s         shape
     */
    public void add(Shape s) {
        shapes.add(s);
    }

}
