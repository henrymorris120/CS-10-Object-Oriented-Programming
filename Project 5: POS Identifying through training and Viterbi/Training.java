import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Training
 * PS5
 * @author Henry Morris and Izzy Axinn, Dartmouth CS 10, Spring 2022
 */

public class Training {
    private String fileWords, fileTags;                                     // filenames
    private ArrayList<ArrayList<String>> allWords, allTags;                 // list of lists of words and tags
    private Map<String, Map<String,Double>> observations, transitions;      // transitions and observations maps


    /**
     * Constructor that indicates which files are to be read
     * @param fileWords                 name of file with sentences
     * @param fileTags                  name of file with parts of speeches
     */
    public Training(String fileWords, String fileTags){
        this.fileWords = fileWords;
        this.fileTags = fileTags;
    }

    /**
     * reads the file with words and the file with the parts speeches
     * builds two lists of lists, one with the words and one with the corresponding parts of speeches
     * @throws Exception
     */
    public void read() throws Exception {
        // create new array lists of array lists
        allWords = new ArrayList<ArrayList<String>>();
        allTags = new ArrayList<ArrayList<String>>();
        // read each line
        BufferedReader sentenceReader = new BufferedReader(new FileReader(fileWords));
        BufferedReader tagReader = new BufferedReader(new FileReader(fileTags));
        String currentWordLine = sentenceReader.readLine();
        String currentTagLine = tagReader.readLine();
        while (currentWordLine != null) {
            // split each line and
            String[] words = currentWordLine.split(" ");
            ArrayList<String> sentence = new ArrayList<>();
            for (String word: words) {
                sentence.add(word.toLowerCase());
            }
            allWords.add(sentence);
            // remove any punctuation and add every POS in line to allTags
            String[] tags = currentTagLine.split(" ");
            ArrayList<String> sentenceTags = new ArrayList<>();
            for (String tag: tags) {
                sentenceTags.add(tag);
            }
            allTags.add(sentenceTags);
            // read next line
            currentWordLine = sentenceReader.readLine();
            currentTagLine = tagReader.readLine();
        }
        sentenceReader.close();
        tagReader.close();
    }

    /**
     * create the observations map
     */
    public void buildObservations() {
        observations = new HashMap<String, Map<String,Double>>();
        // create map to keep track how many times a word is used
        Map<String, Integer> totalTagAppearances = new HashMap<String, Integer>();
        // iterate through every tag in every sentence, keeping track of the index to find the word associated with it
        for (int i = 0; i < allWords.size(); i++) {
            for (int j = 0; j < Math.min(allTags.get(i).size(), allWords.get(i).size()); j++) {
                String tag = allTags.get(i).get(j);
                String word = allWords.get(i).get(j);
                // Add to totalTagAppearances map to keep track of each word's appearances
                if (totalTagAppearances.containsKey(tag)) {
                    totalTagAppearances.put(tag, totalTagAppearances.get(tag) + 1);
                }
                else {
                    totalTagAppearances.put(tag, 1);
                }
                // keep track through the maps in observations of a word's different POS by frequency
                if(observations.containsKey(tag)) {
                    if(observations.get(tag).containsKey(word)) {
                        observations.get(tag).put(word, observations.get(tag).get(word) + 1);
                    }
                    else {
                        observations.get(tag).put(word, 1.0);
                    }
                }
                else {
                    Map<String, Double> wordFreqMap = new HashMap<String, Double>();
                    wordFreqMap.put(word, 1.0);
                    observations.put(tag, wordFreqMap);
                }
            }

        }
        // change the values representing the number of appearances of each word at a POS to log of total probability
        for (String keyTag: observations.keySet()) {
            for (String keyWord: observations.get(keyTag).keySet()) {
                int freq = totalTagAppearances.get(keyTag);
                observations.get(keyTag).put(keyWord, Math.log(observations.get(keyTag).get(keyWord) / freq));
            }
        }
    }

    /**
     * create the transitions map
     */
    public void buildTransitions() {
        transitions = new HashMap<String, Map<String, Double>>();
        // create a map to keep track of how many times a specific part of speech is used
        // note, do not care about the POS in the last index of allTags
        Map<String, Integer> frequencyPOSMap = new HashMap<String, Integer>();
        // Iterate through every tag keeping of track of index
        for (int i = 0; i < allTags.size(); i++) {
            for (int j = 1; j < allTags.get(i).size(); j++) {
                String tag = allTags.get(i).get(j);
                String prevTag = allTags.get(i).get(j-1);
                // add to frequencyPOSMap to keep track of number of times each POS is used excluding the last POS of each line
                if (frequencyPOSMap.containsKey(prevTag)) {
                    frequencyPOSMap.put(prevTag, frequencyPOSMap.get(prevTag) + 1);
                }
                else {
                    frequencyPOSMap.put(prevTag, 1);
                }
                // keep track through the observations map of the POS of next word for every POS
                if (transitions.containsKey(prevTag)) {
                    if (transitions.get(prevTag).containsKey(tag)) {
                        transitions.get(prevTag).put(tag, transitions.get(prevTag).get(tag) + 1);
                    }
                    else {
                        transitions.get(prevTag).put(tag, 1.0);
                    }
                }
                else {
                    Map<String, Double> tagFreqMap = new HashMap<String, Double>();
                    tagFreqMap.put(tag, 1.0);
                    transitions.put(prevTag, tagFreqMap);
                }
            }
        }
        // change all the values representing the number of appearances of each POS after each POS
        // to the log of the probability of that next POS
        for (String keyTag: transitions.keySet()) {
            int freqKeyTag = frequencyPOSMap.get(keyTag);
            for(String keyPrevTag: transitions.get(keyTag).keySet()) {
                transitions.get(keyTag).put(keyPrevTag, Math.log(transitions.get(keyTag).get(keyPrevTag) / freqKeyTag));
            }
        }

    }

    /**
     * puts all methods together, reading the files and building the maps
     * @throws Exception
     */
    public void readFileAndMakeMaps() throws Exception {
        read();
        buildObservations();
        buildTransitions();
    }

    /**
     * returns observations map
     * @return         observations
     */
    public Map<String, Map<String, Double>> getObservations() {
        return observations;
    }

    /**
     * returns transitions map
     * @return         transitions
     */
    public Map<String, Map<String, Double>> getTransitions() {
        return transitions;
    }
}