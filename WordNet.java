/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 9/5/19
 *  Description: WordNet
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class WordNet {

    private TreeMap<String, ArrayList<Integer>> synsetsKeyByNoun;
    private HashMap<String, String> synsetsKeyByID;
    private Digraph hypernyms;
    private final int synsetsLength;
    private ArrayList<Integer> distanceArrA;
    private ArrayList<Integer> distanceArrB;
    private int synsetsCount;
    private int hypernymsCount;
    private final SAP sap;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("Input files cannot be null");
        In in = new In(synsets);
        buildSynsets(in);
        synsetsLength = synsetsKeyByID.size();
        in = new In(hypernyms);
        buildHypernms(in);
        sap = new SAP(this.hypernyms);
    }

    private void buildSynsets(In synsets) {
        synsetsKeyByID = new HashMap<>();
        synsetsKeyByNoun = new TreeMap<>();
        while (!synsets.isEmpty()) {
            synsetsCount++;
            String[] arr = synsets.readLine().split(",");

            // split different terms that correspond to the same id
            String[] words = arr[1].split(" ");
            // build two different maps, one of which has the ids as keys, the other which
            // has the terms as keys. For repeated terms that have different ids, use a map
            // that has arraylists as values, and add each different id to duplicate keys
            for (int i = 0; i < words.length; i++) {
                if (synsetsKeyByNoun.containsKey(words[i])) {
                    synsetsKeyByNoun.get(words[i]).add(Integer.parseInt(arr[0]));
                }
                else {
                    synsetsKeyByNoun.put(words[i], new ArrayList<>());
                    synsetsKeyByNoun.get(words[i]).add(Integer.parseInt(arr[0]));
                }
            }
            synsetsKeyByID.put(arr[0], arr[1]);
        }
    }

    private void buildHypernms(In hypernyms) {

        // create and build a digraph from the input
        this.hypernyms = new Digraph(synsetsLength);
        ArrayList<Integer> possibleRoots = new ArrayList<>();
        while (!hypernyms.isEmpty()) {
            hypernymsCount++;
            String[] line = hypernyms.readLine().split(",");

            // capture all possible roots
            if (line.length == 1) {
                possibleRoots.add(Integer.parseInt(line[0]));
                hypernymsCount--;
            }

            // build the digraph
            for (int i = 1; i < line.length; i++) {
                this.hypernyms.addEdge(Integer.parseInt(line[0]), Integer.parseInt(line[i]));
            }
        }
        if (hypernymsCount != (synsetsCount - 1))
            throw new IllegalArgumentException("Not a rooted DAG");
        boolean flag = false;
        for (Integer i : possibleRoots) {
            if (this.hypernyms.outdegree(i) == 0 && flag) {
                throw new IllegalArgumentException("Hypernyms is not a rooted DAG");
            }
            else if (this.hypernyms.outdegree(i) == 0) {
                flag = true;
            }
        }

        // ensure the digraph has topological order
        Topological checkOrder = new Topological(this.hypernyms);
        if (!checkOrder.hasOrder()) throw new IllegalArgumentException("Not a DAG");
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        ArrayList<String> nouns = new ArrayList<>();
        for (String key : synsetsKeyByNoun.keySet()) nouns.add(key);
        return nouns;
    }

    // is the word a WordNetDegraded noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("Argument to isNoun cannot be null");
        return synsetsKeyByNoun.get(word) != null;
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("Arguments to distance cannot be null");
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Not a WordNetDegraded noun");

        // find the id corresponding to each noun, and add it to an arraylist to be used in
        // SAP methods
        distanceArrA = new ArrayList<>();
        distanceArrB = new ArrayList<>();
        for (String word : synsetsKeyByNoun.tailMap(nounA).keySet()) {
            if (word.equals(nounA)) {
                distanceArrA = synsetsKeyByNoun.get(word);
            }
            else break;
        }
        for (String word : synsetsKeyByNoun.tailMap(nounB).keySet()) {
            if (word.equals(nounB)) {
                distanceArrB = synsetsKeyByNoun.get(word);
            }
            else break;
        }

        return sap.length(distanceArrA, distanceArrB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("Arguments to distance cannot be null");
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Not a WordNetDegraded noun");

        // same as distance except return ancestor instead of length
        distanceArrA = new ArrayList<>();
        distanceArrB = new ArrayList<>();
        for (String word : synsetsKeyByNoun.tailMap(nounA).keySet()) {
            if (word.equals(nounA)) {
                distanceArrA = synsetsKeyByNoun.get(word);
            }
            else break;
        }
        for (String word : synsetsKeyByNoun.tailMap(nounB).keySet()) {
            if (word.equals(nounB)) {
                distanceArrB = synsetsKeyByNoun.get(word);
            }
            else break;
        }
        return synsetsKeyByID.get(Integer.toString(sap.ancestor(distanceArrA, distanceArrB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        // for (String word : wn.nouns()) System.out.println(word);
        // wn.getLengths();
        // wn.getEdges();
        // System.out.println(wn.distance("magneton", "magnetron"));
        // System.out.println(wn.sap("magneton", "magnetron"));
        // System.out.println(wn.isNoun("horse"));
        // System.out.println(wn.distance("time_period period_of_time period", "1750s"));
        // int totaldistance = wn.distance("bed", "water")
        //         + wn.distance("bed", "soda")
        //         + wn.distance("bed", "orange_juice")
        //         + wn.distance("bed", "milk")
        //         + wn.distance("bed", "apple_juice")
        //         + wn.distance("bed", "tea")
        //         + wn.distance("bed", "coffee");
        // System.out.println("bed: " + totaldistance);
        // System.out.println(wn.distance("water", "soda"));
        // System.out.println(wn.distance("water", "bed"));
        // System.out.println(wn.distance("water", "orange_juice"));
        // System.out.println(wn.distance("water", "milk"));
        // System.out.println(wn.distance("water", "apple_juice"));
        // System.out.println(wn.distance("water", "tea"));
        // System.out.println(wn.distance("water", "coffee"));
        // System.out.println(wn.distance("water", "soda"));
        // System.out.println(wn.distance("water", "bed"));
        // System.out.println(wn.distance("water", "orange_juice"));
        // System.out.println(wn.distance("water", "milk"));
        // System.out.println(wn.distance("water", "apple_juice"));
        // int totaldistance = wn.distance("table", "horse") + wn.distance("table", "zebra")
        //         + wn.distance("table", "cat") + wn.distance("table", "bear");
        // System.out.println(totaldistance);
        // System.out.println(wn.distance("Helipterum", "movableness"));
        // System.out.println(wn.hypernymsCount);
        // System.out.println(wn.synsetsCount);
        // for (Integer i : wn.possibleRoots) System.out.println(i);
        // System.out.println(wn.sap("Helipterum", "movableness"));
        System.out.println(wn.distance("in-fighting", "Jezebel"));
        // System.out.println(wn.sap("in-fighting", "Jezebel"));
        System.out.println(wn.sap("in-fighting", "Jezebel"));

    }
}

