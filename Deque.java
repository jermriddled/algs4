/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 7/23/19
 *  Description: Deque
 **************************************************************************** */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node first;
    private Node last;
    private int size;

    private class Node {
        Item item;
        Node next;
        Node previous;
    }

    // construct an empty deque
    public Deque() {
        first = new Node();
        first.next = null;
        first.item = null;
        last = first;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size < 1;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Please enter a valid item");
        if (isEmpty()) {
            first.item = item;
            last = first;
            size++;
        }
        else {
            Node oldFirst = first;
            first = new Node();
            first.item = item;
            first.next = oldFirst;
            oldFirst.previous = first;
            size++;
        }
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Please enter a valid item");
        if (isEmpty()) {
            last.item = item;
            first = last;
            size++;
        }
        else {
            Node oldLast = last;
            last = new Node();
            oldLast.next = last;
            last.previous = oldLast;
            last.item = item;
            size++;
        }
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        Item item = first.item;
        if (size() == 1) {
            first.item = null;
            first.next = null;
            first.previous = null;
            last.item = null;
            last.next = null;
            last.previous = null;
            size--;
            return item;
        }
        Node firstCopy = first;
        first = first.next;
        firstCopy.item = null;
        firstCopy.previous = null;
        first.previous = null;
        size--;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        Item item = last.item;
        if (size() == 1) {
            last.item = null;
            last.previous = null;
            last.next = null;
            first.item = null;
            first.previous = null;
            first.next = null;
            size--;
            return item;
        }
        Node lastCopy = last;
        last = last.previous;
        lastCopy.item = null;
        lastCopy.next = null;
        last.next = null;
        size--;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {

        Node current;
        int iteratorSize;
        int removed;

        public DequeIterator() {
            current = first;
        }

        @Override
        public boolean hasNext() {
            if (size() - removed == 0) return false;
            iteratorSize = size() - removed;
            if (iteratorSize == 1 && current == null) current = first;
            try {
                if (current == null) {
                    return false;
                }
            }
            catch (NullPointerException e) {
                return false;
            }
            return current.item != null;
        }

        @Override
        public Item next() {
            iteratorSize = size() - removed;
            if (iteratorSize == size()) current = first;
            if (iteratorSize <= 0) throw new NoSuchElementException("No more items in iterator");
            Item item = current.item;
            current = current.next;
            removed++;
            if (size() - removed == 0) current = first;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This operation is not allowed");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {

        // Deque<Integer> deque = new Deque<>();
        // deque.addFirst(1);
        // deque.addFirst(2);
        // deque.addFirst(3);
        // deque.addFirst(4);
        // deque.addFirst(5);
        // deque.addFirst(6);
        // deque.addFirst(7);
        // deque.addFirst(8);
        // deque.addFirst(9);
        // deque.addFirst(10);
        // Iterator<Integer> i = deque.iterator();
        // Iterator<Integer> i2 = deque.iterator();
        // deque.addLast(1);
        // deque.addFirst(2);
        // System.out.println("Size is: " + deque.size());
        // System.out.println("Size is: " + deque.size());
        // Iterator<Integer> i3 = deque.iterator();
        // System.out.println(i.hasNext());
        // System.out.println(i.next());
        // System.out.println(i.next());
        // deque.addFirst(1);
        // deque.addFirst(1);
        // deque.addFirst(1);
        // deque.addFirst(1);
        // deque.addFirst(1);
        // deque.addFirst(1);
        // deque.addFirst(1);
        // deque.addFirst(1);
        // Iterator<Integer> i2 = deque.iterator();
        // System.out.println(i2.hasNext());
        // System.out.println(i3.hasNext());
        // System.out.println(i2.next());
        // System.out.println(i3.next());
        // System.out.println(i2.next());
        // System.out.println(i3.next());
        // System.out.println(i3.hasNext());
        // System.out.println(i2.hasNext());
        // deque.addFirst(1);
        // deque.addLast(2);
        // System.out.println(i3.hasNext());
        // System.out.println(i2.hasNext());
        // System.out.println(i2.next());
        // System.out.println(i3.next());
        // System.out.println(i2.next());
        // System.out.println(i3.next());
        // System.out.println(i3.next());
        // System.out.println(i3.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // Iterator<Integer> i3 = deque.iterator();
        // System.out.println(deque.size());
        // deque.addFirst(1);
        // System.out.println(deque.size());
        // System.out.println(i2.hasNext());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // System.out.println(i2.next());
        // System.out.println(i.next());
        // Iterator<Integer> i = deque.iterator();
        // deque.addLast(1);
        // deque.addLast(2);
        // System.out.println(i.hasNext());
        // System.out.println(i.next());
        // System.out.println(i.hasNext());

        // Deque<Integer> deque = new Deque<>();
        // deque.addFirst(1);
        // deque.addFirst(2);
        // deque.addFirst(3);
        // Iterator<Integer> i = deque.iterator();
        // deque.addFirst(4);
        // deque.addFirst(5);
        // deque.addFirst(6);
        // while (i.hasNext()) System.out.println(i.next());
    }

}
