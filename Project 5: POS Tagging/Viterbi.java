import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Viterbi
 * PS5
 * @author Henry Morris, Dartmouth CS 10, Spring 2022
 */

public class Viterbi {
    // transitions and observation made in training
    Map<String, Map<String, Double>> transitions, observations;
    // list of all parts of speeches
    String[] str = {"``", "ADJ", "ADV", "CNJ", "ET", "EX", "FW", "MOD", "N", "NP", "NUM", "PRO", "I", "P", "TO", "UH", "V", "VD", "VG", "VN", "WH"};
    double unidentifiedScore = -100.0;
    // file names
    String trainingFileWords, trainingFileTags;


    /**
     * performs training and sets viterbi's observations and transitions maps
     * @param trainingFileWords                 file name of training file with words
     * @param trainingFileTags                  file name of training file with words
     * @throws Exception
     */
    public Viterbi(String trainingFileWords, String trainingFileTags) throws Exception {
        Training t = new Training(trainingFileWords, trainingFileTags);
        t.readFileAndMakeMaps();
        transitions = t.getTransitions();
        observations = t.getObservations();
    }

    /**
     * Tags a user inputted sentence, and continues doing this until user quits
     */
    public void consoleTest(){
        System.out.println("Please enter a sentence, type q to quit");
        Scanner sc = new Scanner(System.in);
        Boolean run = true;
        while (run) {
            String line = sc.nextLine();
            // check if user quits
            if (line.equals("q")) {
                run = false;
            } else {
                // read the line and print out result of the part of speeches
                String[] words = line.split(" ");
                ArrayList<String> sentence = new ArrayList<>();
                for (String word : words) {
                    sentence.add(word.toLowerCase());
                }
                List<String> tags = viterbiAlogrithm(sentence);
                String s = "";
                for (int i = 0; i < tags.size(); i++) {
                    if (i == 0) {
                        s = tags.get(i);
                    }
                    else {
                        s = s + " " + tags.get(i);
                    }
                }
                System.out.println(s);
            }
        }
        System.out.println("You have quit");
    }

    /**
     * test a file and how print out how succesfull the Viterbi decoding is
     * @param fileTestWords         file with test sentences
     * @param fileTestTags          file with tags corrsponding to test sentences
     * @throws Exception
     */
    public void fileTest(String fileTestWords, String fileTestTags) throws Exception {
        ArrayList<ArrayList<String>> wordsList = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> tagsList = new ArrayList<ArrayList<String>>();
        wordsList = readAndCreateListsWords(fileTestWords);
        tagsList = readAndCreateListsTags(fileTestTags);
        tagWords(wordsList, tagsList);
    }




    /**
     * Prints out the success rate of the program
     */
    public void tagWords(ArrayList<ArrayList<String>>  wordsList, ArrayList<ArrayList<String>> tagsList) {
        int successRate =0;
        int total = 0;
        for (int i = 0; i < wordsList.size(); i++) { //All sentences
            int counter = 0;
            total += wordsList.get(i).size();
            List<String> tags = viterbiAlogrithm(wordsList.get(i)); //Creates a list of tags
            List<String> answers = tagsList.get(i); //Reads in answers
            for (int j = 0; j < tags.size(); j++) {
                if (tags.get(j).equals(answers.get(j))) {counter++;} //Checks if tags matches answers
            }
            System.out.println("Sentence " + (i+1) + ": " + counter + "/" + tags.size() + " correct");
            successRate += counter;
        }
        System.out.println("The overall success rate is " + successRate + "/" + total);
    }

    /**
     * Viterbi takes in a sentence and tags each word in the sentence based on a
     * probability based system (Markov model)
     * @param words                     list of words
     * @return List<String>             list of POS
     */
    public List<String> viterbiAlogrithm(List<String> words) {
        Set<String> currStates = new TreeSet<String>();
        Map<String, Double> currentScores = new TreeMap<String, Double>();
        for (String s : observations.keySet()) {
            if (observations.get(s).containsKey(words.get(0))) {currStates.add(s);} //Creates the starting state for a known word
        }
        if (currStates.isEmpty()) {currStates.addAll(List.of(str));} //Puts in all possible states for an unknown word
        List<Map<String, String>> backtrace = new ArrayList<>();
        String lastState = "";
        for (int i = 0; i < words.size(); i++) { //Going through every word
            Set<String> nextStates = new TreeSet<String>();
            Map<String, Double> nextScores = new TreeMap<String, Double>();
            Map<String, String> winners = new TreeMap<>();
            double bestScore = -500;
            for (String state : currStates) { //Going through every state of current word
                if (transitions.containsKey(state)) { //Checking if there are any transitions
                    for (String nextState : transitions.get(state).keySet()) { //Going to the next state
                        double observationScore = unidentifiedScore;
                        double currentScore = 0.0;
                        nextStates.add(nextState);
                        try {observationScore = observations.get(nextState).get(words.get(i + 1).toLowerCase()); //Seeing if there is a value for a word in this particular state
                        } catch (Exception e) {}
                        double transitionScore = unidentifiedScore;
                        if (transitions.get(state).get(nextState) != 0) {
                            transitionScore = transitions.get(state).get(nextState);
                        }
                        if (!currentScores.isEmpty()) {currentScore = currentScores.get(state);} //Making sure scores aren't empty for this state
                        double nextScore = currentScore + transitionScore + observationScore; //Finding next score
                        if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) { //Find the winners for each state
                            nextScores.put(nextState, nextScore);
                            winners.put(nextState, state);
                            if (i == words.size() - 2 && nextScore > bestScore) { //Finding the final state and maximum score
                                bestScore = nextScore;
                                lastState = nextState;
                            }
                        }
                    }
                }
            }
            backtrace.add(winners);
            currStates = nextStates;
            currentScores = nextScores;
        }
        String[] tags = new String[backtrace.size()];
        String value = lastState;
        for (int j = backtrace.size() - 2; j >= 0; j--) { //Going through all maps of winners in backtrace
            Map currentMap = backtrace.get(j);
            tags[j + 1] = value; //Adding the winners
            if (currentMap.get(value) != null) {value = (String) currentMap.get(value);} //Finding the state that led to "next state"
        }
        tags[0] = value;
        System.out.println(List.of(tags));
        return List.of(tags);
    }


    /**
     * creates a list of lists of tags based on file being inputted as parameter and return it
     * @param fileName                  file being read
     * @return                             list of lists based on parameter fileName
     * @throws IOException
     */
    public ArrayList<ArrayList<String>> readAndCreateListsTags(String fileName) throws IOException {
        ArrayList<ArrayList<String>> allSentences = new ArrayList<ArrayList<String>>();
        BufferedReader sentenceReader = new BufferedReader(new FileReader(fileName));
        String currentWordLine = sentenceReader.readLine();
        // reach line until line is empty
        // add each tag in each line to their own list
        while (currentWordLine != null) {
            String[] words = currentWordLine.split(" ");
            ArrayList<String> sentence = new ArrayList<>();
            for (String word : words) {
                sentence.add(word);
            }
            allSentences.add(sentence);
            currentWordLine = sentenceReader.readLine();
        }
        return allSentences;
    }


    /**
     * creates a list of lists of words based on file being inputted as parameter and return it
     * @param fileName                      file being read
     * @return                             list of lists based on parameter fileName
     * @throws IOException
     */
    public ArrayList<ArrayList<String>> readAndCreateListsWords(String fileName) throws IOException {
        ArrayList<ArrayList<String>> allSentences = new ArrayList<ArrayList<String>>();
        BufferedReader sentenceReader = new BufferedReader(new FileReader(fileName));
        String currentWordLine = sentenceReader.readLine();
        // reach line until line is empty
        // add each word in each line to their own list
        while (currentWordLine != null) {
            String[] words = currentWordLine.split(" ");
            ArrayList<String> sentence = new ArrayList<>();
            for (String word : words) {
                sentence.add(word.toLowerCase());
            }
            allSentences.add(sentence);
            currentWordLine = sentenceReader.readLine();
        }
        return allSentences;
    }




    public static void main(String[] args) throws Exception{
        Viterbi v = new Viterbi("PS5/brown-train-sentences.txt", "PS5/brown-train-tags.txt");
        v.fileTest("PS5/brown-test-sentences.txt", "PS5/brown-test-tags.txt");
        //Viterbi v = new Viterbi("PS5/simple-train-sentences.txt", "PS5/simple-train-tags.txt");
        //v.fileTest("PS5/simple-test-sentences.txt", "PS5/simple-test-tags.txt");
        //Viterbi v = new Viterbi("PS5/brown-train-sentences.txt", "PS5/brown-train-tags.txt");
        //v.fileTest("PS5/testSent.txt", "PS5/testtags.txt");
        //v.consoleTest();
    }
}

