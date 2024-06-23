import jdk.jfr.Frequency;
import org.bytedeco.javacv.FrameFilter;
import org.w3c.dom.CharacterData;

import javax.xml.stream.events.Characters;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;


/**
 * Huffman encoding to compress and decompress files
 * PS3
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */

public class Huffman {
    BinaryTree<CharObj> huffmanTree;        // Huffman tree
    ArrayList<Character> characters;        // list of the character in order in original file
    Map<Character, String> codeMap;         // map with keys and values to find characters on Huffman tree


    /**
     * Reads a file and returns an array of the characters from the file in the order they are written
     * @param filename          file to be read using BufferedReader
     * @return                  array list of characters from filename
     * @throws Exception
     */
    public ArrayList<Character> getFileCharacters(String filename) throws Exception {
        // set characters to a new empty array list
        characters = new ArrayList<Character>();
        BufferedReader input = new BufferedReader(new FileReader(filename));
        // convert character into an integer
        int charInt = input.read();
        // continue reverting charInt into a char and adding to characters until read whole file
        while (charInt != -1) {
            char character = (char) charInt;
            characters.add(character);
            charInt = input.read();
        }
        input.close();
        return characters;
    }


    /**
     * Creates a frequency map using the parameter array list characters
     * @param characters        array list of characters
     * @return                  frequency map of each character in characters
     */
    public Map<Character, Integer> frequencies(ArrayList<Character> characters) {
        // create new frequency map
        Map<Character, Integer> frequencyMap = new TreeMap<Character, Integer>();
        // add new key to frequencyMap with value 1 if character isn't part of the map yet, else add one to its value
        for (Character character: characters) {
            if (frequencyMap.containsKey(character)) {
                frequencyMap.put(character, frequencyMap.get(character)+1);
            }
            else {
                frequencyMap.put(character, 1);
            }
        }
        return frequencyMap;
    }

    /**
     * creates a priority queue of binary trees containing a CharObj
     * @param frequencyMap  frequency map of characters to be used as basis for priority queue
     * @return              priority queue
     */
    public PriorityQueue<BinaryTree<CharObj>> createPriority(Map<Character, Integer> frequencyMap) {
        class TreeComparator implements Comparator<BinaryTree<CharObj>> {
            public int compare(BinaryTree<CharObj> bt1, BinaryTree<CharObj> bt2) {
                return bt1.data.getFrequency() - bt2.data.getFrequency();
            }
        }
        // creates comparator to be used in the priority queue construction
        Comparator<BinaryTree<CharObj>> treeComparator = new TreeComparator();
        PriorityQueue<BinaryTree<CharObj>> pq = new PriorityQueue<BinaryTree<CharObj>>(treeComparator);
        // iterate through all the keys in frequencyMap, create a CharObject and then create binary tree with it
        // add to the priority queue
        for (Character key : frequencyMap.keySet()) {
            CharObj obj = new CharObj(key, frequencyMap.get(key));
            BinaryTree<CharObj> tree = new BinaryTree<CharObj>(obj);
            pq.add(tree);
        }
        return pq;
    }

    /**
     * create a Huffman tree using the priority queue parameter
     * @param pq        priority queue
     * @return
     */
    public BinaryTree<CharObj> createTree(PriorityQueue<BinaryTree<CharObj>> pq) {
        if (pq.size() > 0) {
            while (pq.size() > 1) {
                BinaryTree<CharObj> t1 = pq.remove();
                BinaryTree<CharObj> t2 = pq.remove();
                int sum = t1.data.getFrequency() + t2.data.getFrequency();
                CharObj root = new CharObj('\0', sum);
                BinaryTree<CharObj> organizedTree = new BinaryTree<CharObj>(root, t1, t2);
                pq.add(organizedTree);
            }
            return pq.remove();
        }
        else {
            BinaryTree<CharObj> emptyHuffmanTree = new BinaryTree<CharObj>(null, null, null);
            return emptyHuffmanTree;
        }
    }

    /**
     * create Huffman Tree using above methods and their returns
     * @param filename
     * @throws Exception
     */
    public void putAllTogether(String filename) throws Exception {
        huffmanTree = createTree(createPriority(frequencies(getFileCharacters(filename))));
    }

    /**
     * Set codeMap to a new tree map
     * Pair each character with a string that of 1's and 0's that can be used as a path on the Huffman tree to find it
     */
    public void setCodeMap() {
        codeMap = new TreeMap<>();
        String path = "";
        // if the file has only one type of character
        if (huffmanTree.size() == 1 && characters.size() > 0) {
            codeMap.put(characters.get(0), "1");
        }
        if (characters.size() > 1 && huffmanTree.size() > 1) {
            codeMapHelper(codeMap, huffmanTree, path);
        }
    }

    /**
     * Uses recursion to add each character and corresponding path to codeMap
     * @param codeMap           tree map which is added to when current is a leaf
     * @param current           current location on the Huffman tree
     * @param path              path to current location on the Huffman tree
     */
    public void codeMapHelper(Map<Character, String> codeMap, BinaryTree<CharObj> current, String path) {
        if (current != null) {
            if (current.hasRight()) {
                codeMapHelper(codeMap, current.getRight(), path + '1');
            }
            if (current.hasLeft()) {
                codeMapHelper(codeMap, current.getLeft(), path + '0');
            }
            if (current.isLeaf()) {
                codeMap.put(current.data.getData(), path);
            }
        }
    }


    /**
     * Creates new file and used BufferedBitWriter to write out the compressed version of original file
     * @param compressedFilename            name of file that is a compressed version of original
     * @throws Exception
     */
    public void compressionOutput(String compressedFilename) throws Exception {
        BufferedBitWriter bitOutput = new BufferedBitWriter(compressedFilename);
        // Iterate through the ordered list of characters that makes up original file
        for (Character character: characters) {
            // Find the encoded string of 1's and 0's for each character
            // Iterate through each char in String and use whether it is one or zero to write
            String encoded = codeMap.get(character);
            char [] enc = encoded.toCharArray();
            for (int i = 0; i < encoded.length(); i++) {
                if (enc[i] == '1') {
                    bitOutput.writeBit(true);
                }
                if (enc[i] == '0') {
                    bitOutput.writeBit(false);
                }
            }
        }
        bitOutput.close();
    }


    /**
     * Decompress the compressed file onto a new file
     * @param compressedFilename            name of file that is compressed
     * @param decompressedFilename          name of file that is decompressed
     * @throws Exception
     */
    public void decompress(String compressedFilename, String decompressedFilename) throws Exception {
        // read compressed file and add each bit to an array list
        BufferedBitReader bitInput = new BufferedBitReader(compressedFilename);
        ArrayList<Boolean> compressedBits = new ArrayList<Boolean>();
        while (bitInput.hasNext()) {
            boolean bit = bitInput.readBit();
            compressedBits.add(bit);
        }
        bitInput.close();
        // create new array list that will hold each decompressed character
        ArrayList<Character> decompressedCharacters = new ArrayList<Character>();
        decompressCompressedBitsIntoCharacters(decompressedCharacters, compressedBits);
        // write out decompressed file using decompressedCharacters
        BufferedWriter output = new BufferedWriter(new FileWriter(decompressedFilename));
        for (Character character: decompressedCharacters) {
            output.write(character);
        }
        output.close();
    }

    /**
     * Decompress a list of bits that are encoded using the Huffman tree into a list of characters that are added
     * to the parameter decompressedCharacters
     * @param decompressedCharacters
     * @param compressedBits
     */
    public void decompressCompressedBitsIntoCharacters(ArrayList<Character> decompressedCharacters, ArrayList<Boolean> compressedBits) {
        BinaryTree<CharObj> current = huffmanTree;
        // if the number of characters in the file is one
        if (compressedBits.size() == 1) {
            decompressedCharacters.add(current.data.getData());
        }
        else {
            // if there is only kind of character in the file
            if (compressedBits.size() > 1 && huffmanTree.isLeaf()) {
                for (Boolean bit: compressedBits) {
                    decompressedCharacters.add(current.data.getData());
                }
            }
            // Iterate through every bit in compressedBits
            // When a path of bits leads to a leaf node, grab the character that is in the object which is its data and
            // adding it the decompressedCharacters and then reseting the path to the head of huffmanTree
            else {
                for (Boolean bit : compressedBits) {
                    if (bit && current.hasRight()) {
                        current = current.getRight();
                        if (current.isLeaf()) {
                            decompressedCharacters.add(current.data.getData());
                            current = huffmanTree;
                        }
                    }
                    if (!bit && current.hasLeft()) {
                        current = current.getLeft();
                        if (current.isLeaf()) {
                            decompressedCharacters.add(current.data.getData());
                            current = huffmanTree;
                        }
                    }
                }
            }
        }
    }

    /**
     * Compress and decompress and file with a small amount of text
     * @throws Exception
     */
    public void test1() throws Exception {
        putAllTogether("inputs/SmallTest.txt");
        setCodeMap();
        compressionOutput("inputs/CompressionTest1.txt");
        decompress("inputs/CompressionTest1.txt", "inputs/DecompressedTest1.txt");
    }

    /**
     * Compress and decompress a file with just one character
     * @throws Exception
     */
    public void test2() throws Exception {
        putAllTogether("inputs/OneCharacter.txt");
        setCodeMap();
        compressionOutput("inputs/CompressionTest2.txt");
        decompress("inputs/CompressionTest2.txt", "inputs/DecompressedTest2.txt");
    }

    /**
     * Compress and decompress an Empty file
     * @throws Exception
     */
    public void test3() throws Exception {
        putAllTogether("inputs/Empty.txt");
        setCodeMap();
        compressionOutput("inputs/CompressionTest3.txt");
        decompress("inputs/CompressionTest3.txt", "inputs/DecompressedTest3.txt");
    }

    /**
     * Compress and decompress a file consisting of a single character repeated
     * @throws Exception
     */
    public void test4() throws Exception {
        putAllTogether("inputs/MultipleOfOneCharacter.txt");
        setCodeMap();
        compressionOutput("inputs/CompressionTest4.txt");
        decompress("inputs/CompressionTest4.txt", "inputs/DecompressedTest4.txt");
    }

    /**
     * Compress and decompress the US Constitution
     * @throws Exception
     */
    public void constitution() throws Exception {
        putAllTogether("inputs/USConstitution.txt");
        setCodeMap();
        compressionOutput("inputs/CompressionConstitution.txt");
        decompress("inputs/CompressionConstitution.txt", "inputs/DecompressedConstitution.txt");
    }

    /**
     * compress and decompress War and Peace
     * @throws Exception
     */
    public void warAndPeace() throws Exception {
        putAllTogether("inputs/WarAndPeace.txt");
        setCodeMap();
        compressionOutput("inputs/CompressionWarAndPeace.txt");
        decompress("inputs/CompressionWarAndPeace.txt", "inputs/DecompressedWarAndPeace.txt");
    }


    public static void main(String[] args) throws Exception {
        Huffman h = new Huffman();
        h.test1();
        h.test2();
        h.test3();
        h.test4();
        h.constitution();
        h.warAndPeace();
    }
}
