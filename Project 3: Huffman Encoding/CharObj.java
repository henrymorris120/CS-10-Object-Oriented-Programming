/**
 * Simple class that can create object that holds 2 instance variables
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */

public class CharObj {
    private int frequency;
    private char data;

    public CharObj(char data, int frequency) {
        this.frequency = frequency;
        this.data = data;
    }

    public char getData() {
        return data;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "(" + data + ": " + frequency + ")";
    }
}
