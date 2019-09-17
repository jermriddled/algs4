/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 9/5/19
 *  Description: Outcast
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordnet;

    // constructor takes a WordNetDegraded object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNetDegraded nouns, return an outcast
    // outcast is defined as a noun in a set whose combined distance to all other
    // nouns in the set is greater than any other noun's combined distance to
    // all other nouns
    public String outcast(String[] nouns) {
        int longestDistance = 0;
        String outcast = nouns[0];
        for (int i = 0; i < nouns.length; i++) {
            // for each noun, combine all its distances to other nouns
            int sumOfDistances = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (j == i) continue;
                int distance = wordnet.distance(nouns[i], nouns[j]);
                if (distance != -1) sumOfDistances += distance;
            }
            if (sumOfDistances > longestDistance) {
                longestDistance = sumOfDistances;
                outcast = nouns[i];
            }
        }
        return outcast;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
