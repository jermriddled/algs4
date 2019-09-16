/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 7/23/19
 *  Description: Permutation
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;

public class Permutation {
    public static void main(String[] args) {

        int num = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            queue.enqueue(StdIn.readString());
        }
        for (int i = 0; i < num; i++) {
            System.out.println(queue.dequeue());
        }

    }
}
