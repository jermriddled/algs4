/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */


import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] arr;
    private final int head;
    private int tail;
    private int size = 0;

    // construct an empty randomized queue
    public RandomizedQueue() {
        tail = 0;
        head = 0;
        arr = (Item[]) new Object[tail];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        try {
            return arr[0] == null;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Please enter a valid item to enqueue");
        if (size == 0) {
            arr = (Item[]) new Object[1];
            arr[head] = item;
            size++;
            tail = 1;
        }
        else if (size == tail) {
            tail = tail * 2;
            Item[] temp = (Item[]) new Object[tail];
            for (int i = 0; i < size; i++) {
                temp[i] = arr[i];
            }
            temp[size] = item;
            arr = temp;
            size++;
        }
        else {
            arr[size] = item;
            size++;
        }
    }

    // remove and return a random item
    public Item dequeue() {
        if (size == 0) throw new NoSuchElementException("Queue is empty");
        int randomNumber = StdRandom.uniform(size);
        if (size == 1) {
            Item item = arr[0];
            arr[0] = null;
            size = 0;
            tail = 0;
            return item;
        }
        if (size == 2) {
            Item item = arr[randomNumber];
            Item[] temp = (Item[]) new Object[1];
            if (randomNumber == 1) temp[0] = arr[0];
            else temp[0] = arr[1];
            arr = temp;
            size = 1;
            tail = 1;
            return item;
        }
        if (size == tail / 4) {
            tail = tail / 2;
            Item[] temp = (Item[]) new Object[tail];
            for (int i = 0; i < size; i++) {
                temp[i] = arr[i];
            }
            arr = temp;
        }
        if (randomNumber != size - 1) {
            Item tempItem = arr[size - 1];
            arr[size - 1] = arr[randomNumber];
            arr[randomNumber] = tempItem;
        }
        Item item = arr[size - 1];
        arr[size - 1] = null;
        size--;
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (size() == 0) throw new NoSuchElementException("Queue is empty");
        int randomNumber = StdRandom.uniform(size);
        return arr[randomNumber];
    }

    // return an independent iterator over items in random order

    public Iterator<Item> iterator() {
        return new RandomIterator();
    }

    private class RandomIterator implements Iterator<Item> {

        int cursor = 0;
        Item[] temp = (Item[]) new Object[size];

        public RandomIterator() {
            for (int i = 0; i < size; i++) {
                temp[i] = arr[i];
            }
            StdRandom.shuffle(temp);
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public Item next() {
            if (cursor >= size) throw new NoSuchElementException("Queue is empty");
            Item item = temp[cursor];
            temp[cursor] = null;
            cursor++;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This operation is not allowed");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {

        // RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        // System.out.println(queue.isEmpty());
        // for (int i = 0; i < 20; i++) {
        //     int randomNumber = StdRandom.uniform(2);
        //     // while (queue.size() == 0) {
        //     //     randomNumber = StdRandom.uniform(2);
        //     //     if (randomNumber == 0) queue.enqueue(i);
        //     // }
        //     // if (randomNumber == 1) System.out.println(queue.dequeue());
        //     if (randomNumber == 0) queue.enqueue(i);
        //     else queue.dequeue();

        // }

        // queue.enqueue(1);
        // Iterator<Integer> iterator = queue.iterator();
        // queue.enqueue(1);
        // // queue.enqueue(2);
        // // queue.dequeue();
        // // queue.dequeue();
        // iterator.next();
        // iterator.next();

        // for (int i = 0; i < 10; i++) {
        //     queue.enqueue(i);
        // }
        // for (int i = 0; i < 9; i++) {
        //     queue.dequeue();
        // }
        // System.out.println(queue.size());
        // Iterator<Integer> iterator = queue.iterator();
        // Iterator<Integer> iterator2 = queue.iterator();
        // for (int i = 0; i < 10; i++) {
        //     System.out.println(iterator.next());
        // }
        // for (int i = 0; i < 10; i++) {
        //     System.out.println(iterator2.next());
        // }

        // Object[] arr = queue.getArray();
        // for (int p = 0; p < 4; p++) {
        //     System.out.println(arr[p]);

        // for (int j = 0; j < 4; j++) {
        //     System.out.println(queue.dequeue());
        // }
        // System.out.println(queue.isEmpty());
        // System.out.println(queue.size());
        // queue.enqueue(1);
        // System.out.println(queue.isEmpty());
        // System.out.println(queue.size());
        // System.out.println(queue.dequeue());
        // System.out.println(queue.isEmpty());
        // System.out.println(queue.size());
        // queue.enqueue(1);
        // queue.enqueue(2);
        // System.out.println(queue.isEmpty());
        // System.out.println(queue.size());
        // System.out.println(queue.dequeue());
        // System.out.println(queue.dequeue());
        // System.out.println(queue.isEmpty());
        // System.out.println(queue.size());
        // queue.enqueue(1);
        // System.out.println(queue.sample());
        // System.out.println(queue.isEmpty());
        // System.out.println(queue.size());
        // queue.enqueue(2); // this one not being enqueued but array grows
        // queue.enqueue(3);
        // Iterator<Integer> i = queue.iterator();
        // System.out.println(i.hasNext());
        // System.out.println(i.next());
        // System.out.println(i.next());
        // System.out.println(queue.dequeue());
        // System.out.println(queue.dequeue());
        // System.out.println(queue.dequeue());
        // System.out.println(StdRandom.uniform(0));


    }

}

