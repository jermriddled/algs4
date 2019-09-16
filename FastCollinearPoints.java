/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private LineSegment[] lineSegments = new LineSegment[0];
    private final ArrayList<Point[]> pointsList = new ArrayList<>();


    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        // Ensure valid input
        if (points == null) throw new IllegalArgumentException("Array supplied is null");
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException("Point is null");
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].equals(points[j]))
                    throw new IllegalArgumentException("Equal points are not allowed");
            }
        }

        if (points.length < 4) return;
        int lineIndex = 0;

        // REVISION OF BEST ONE WITH MERGE SORT ADDED - THIS IS FASTER IN TIMING IN MOST TIMING TESTS BUT ADDS SUBSEGMENTS BECAUSE EACH ITERATION OF I SORTS BY SLOPE ORDER, THEN TEMP IS NOT IN ORDER
        // BECAUSE TEMP[0] IS COPY[0] WHICH IS IN THE SAME LINE BUT IS NOT THE ENDPOINT...I WAS SOLVING THIS PREVIOUSLY BY USING ARRAYS.SORT ON THE TEMP ARRAY ONCE I WAS MAKING A LINE SEGMENT

        Point[] sortCopy = Arrays.copyOf(points, points.length);
        sort(sortCopy);
        for (int i = 0; i < points.length; i++) {
            Point[] copy = Arrays.copyOf(sortCopy, points.length);
            // For each point in the array, sort all points by slope order of ith point
            Arrays.sort(copy, copy[i].slopeOrder());
            ArrayList<Double> slopes = new ArrayList<>();
            Point[] temp = new Point[0];
            for (int j = 0; j < copy.length; j++) {
                double slope = copy[0].slopeTo(copy[j]);
                // If this slope has been encountered before, make a new array of points for a line segment, starting with the points involved in the first two matching slopes
                if (slopes.contains(slope)) {
                    // THIS PART IS CREATING SUBSEGMENTS - HAS THE WHOLE LINE IN IT, BUT ENDPOINTS ARE NOT AT [0] AND [LENGTH-1]
                    if (temp.length == 0) {
                        temp = new Point[3];
                        temp[0] = copy[0];
                        temp[1] = copy[j - 1];
                        temp[2] = copy[j];
                    }
                    // Add all subsequent points that match the already found matching slopes
                    else {
                        Point[] aux = new Point[temp.length + 1];
                        for (int z = 0; z < temp.length; z++) {
                            aux[z] = temp[z];
                        }
                        temp = aux;
                        temp[temp.length - 1] = copy[j];
                        // If end of array is reached, create the line segment
                        if (j == copy.length - 1) {
                            int compare = 0;
                            sort(temp);
                            Point[] arr = new Point[2];
                            arr[0] = temp[0];
                            arr[1] = temp[temp.length - 1];
                            // Check for duplicate line segments
                            for (int z = 0; z < pointsList.size(); z++) {
                                if (Arrays.equals(pointsList.get(z), arr)) compare = 1;
                            }
                            // Create the line segment if it is not a duplicate
                            if (compare == 0) {
                                pointsList.add(arr);
                                LineSegment line = new LineSegment(temp[0], temp[temp.length - 1]);
                                LineSegment[] aux2 = new LineSegment[lineIndex + 1];
                                if (lineSegments.length != 0) {
                                    for (int z = 0; z < lineSegments.length; z++)
                                        aux2[z] = lineSegments[z];
                                }
                                lineSegments = aux2;
                                lineSegments[lineIndex] = line;
                                lineIndex++;
                            }
                            // Reset line segment array so that new line segments can be found
                            temp = new Point[0];
                        }
                    }
                }
                // Create line segment if there are 4 or more points in the segment
                else if (temp.length >= 4) {
                    int compare = 0;
                    sort(temp);
                    Point[] arr = new Point[2];
                    arr[0] = temp[0];
                    arr[1] = temp[temp.length - 1];
                    // Check for duplicates
                    for (int z = 0; z < pointsList.size(); z++) {
                        if (Arrays.equals(pointsList.get(z), arr)) compare = 1;
                    }
                    // Create the line segment if it is not a duplicate
                    if (compare == 0) {
                        pointsList.add(arr);
                        LineSegment line = new LineSegment(temp[0], temp[temp.length - 1]);
                        LineSegment[] aux = new LineSegment[lineIndex + 1];
                        if (lineSegments.length != 0) {
                            for (int z = 0; z < lineSegments.length; z++)
                                aux[z] = lineSegments[z];
                        }
                        lineSegments = aux;
                        lineSegments[lineIndex] = line;
                        lineIndex++;
                    }
                    temp = new Point[0];
                }
                // Reset line segment array so that new line segments can be found
                else temp = new Point[0];
                slopes.add(slope);
            }
        }
    }

    // TOO MANY COMPARES

    // for (int i = 0; i < points.length; i++) {
    //     Point[] copy = Arrays.copyOf(points, points.length);
    //     Arrays.sort(copy, copy[i].slopeOrder());
    //     ArrayList<Double> slopes = new ArrayList<>();
    //     Point[] temp = new Point[0];
    //     int length = 0;
    //     for (int j = 0; j < copy.length; j++) {
    //         // Double slope = 0.0;
    //         // if (j != i) slope = copy[i].slopeTo(copy[j]);
    //         double slope = copy[0].slopeTo(copy[j]);
    //         if (slopes.contains(slope)) {
    //             if (temp.length == 0) {
    //                 temp = new Point[2];
    //                 if (copy[0].compareTo(copy[j - 1]) < 0) {
    //                     temp[0] = copy[0];
    //                     temp[1] = copy[j - 1];
    //                 }
    //                 else {
    //                     temp[0] = copy[j - 1];
    //                     temp[1] = copy[0];
    //                 }
    //                 length = 2;
    //                 if (copy[j].compareTo(temp[0]) < 0) {
    //                     temp[0] = copy[j];
    //                     length++;
    //                 }
    //                 else if (copy[j].compareTo(temp[1]) > 0) {
    //                     temp[1] = copy[j];
    //                     length++;
    //                 }
    //                 else length++;
    //             }
    //             else if (copy[j].compareTo(temp[0]) < 0) {
    //                 temp[0] = copy[j];
    //                 length++;
    //             }
    //             else if (copy[j].compareTo(temp[1]) > 0) {
    //                 temp[1] = copy[j];
    //                 length++;
    //             }
    //             else length++;
    //
    //             if (j == copy.length - 1 && length >= 4) {
    //                 int compare = 0;
    //                 for (int z = 0; z < pointsList.size(); z++) {
    //                     if (Arrays.equals(pointsList.get(z), temp)) compare = 1;
    //                 }
    //                 if (compare == 0) {
    //                     pointsList.add(temp);
    //                     LineSegment line = new LineSegment(temp[0], temp[1]);
    //                     LineSegment[] aux = new LineSegment[lineIndex + 1];
    //                     if (lineSegments.length != 0) {
    //                         for (int z = 0; z < lineSegments.length; z++)
    //                             aux[z] = lineSegments[z];
    //                     }
    //                     lineSegments = aux;
    //                     lineSegments[lineIndex] = line;
    //                     lineIndex++;
    //                 }
    //             }
    //             // if (temp.length == 0) {
    //             //     temp = new Point[3];
    //             //     temp[0] = copy[0];
    //             //     temp[1] = copy[j - 1];
    //             //     temp[2] = copy[j];
    //         }
    //
    //         else if (length >= 4) {
    //             int compare = 0;
    //             for (int z = 0; z < pointsList.size(); z++) {
    //                 if (Arrays.equals(pointsList.get(z), temp)) compare = 1;
    //             }
    //             if (compare == 0) {
    //                 pointsList.add(temp);
    //                 LineSegment line = new LineSegment(temp[0], temp[1]);
    //                 LineSegment[] aux = new LineSegment[lineIndex + 1];
    //                 if (lineSegments.length != 0) {
    //                     for (int z = 0; z < lineSegments.length; z++)
    //                         aux[z] = lineSegments[z];
    //                 }
    //                 lineSegments = aux;
    //                 lineSegments[lineIndex] = line;
    //                 lineIndex++;
    //             }
    //             temp = new Point[0];
    //             length = 0;
    //         }
    //         else {
    //             temp = new Point[0];
    //             length = 0;
    //         }
    //         slopes.add(slope);

    // FIRST GOOD VERSION, GOT 94/100

    // for (int i = 0; i < points.length; i++) {
    //     Point[] copy = Arrays.copyOf(points, points.length);
    //     Arrays.sort(copy, copy[i].slopeOrder());
    //     ArrayList<Double> slopes = new ArrayList<>();
    //     Point[] temp = new Point[0];
    //     for (int j = 0; j < copy.length; j++) {
    //         // Double slope = 0.0;
    //         // if (j != i) slope = copy[i].slopeTo(copy[j]);
    //         double slope = copy[0].slopeTo(copy[j]);
    //         if (slopes.contains(slope)) {
    //             if (temp.length == 0) {
    //                 temp = new Point[3];
    //                 temp[0] = copy[0];
    //                 temp[1] = copy[j - 1];
    //                 temp[2] = copy[j];
    //             }
    //             else {
    //                 Point[] aux = new Point[temp.length + 1];
    //                 for (int z = 0; z < temp.length; z++) {
    //                     aux[z] = temp[z];
    //                 }
    //                 temp = aux;
    //                 temp[temp.length - 1] = copy[j];
    //                 if (j == copy.length - 1) {
    //                     Arrays.sort(temp);
    //                     int compare = 0;
    //                     Point[] arr = new Point[2];
    //                     arr[0] = temp[0];
    //                     arr[1] = temp[temp.length - 1];
    //                     for (int z = 0; z < pointsList.size(); z++) {
    //                         if (Arrays.equals(pointsList.get(z), arr)) compare = 1;
    //                     }
    //                     if (compare == 0) {
    //                         pointsList.add(arr);
    //                         LineSegment line = new LineSegment(temp[0], temp[temp.length - 1]);
    //                         LineSegment[] aux2 = new LineSegment[lineIndex + 1];
    //                         if (lineSegments.length != 0) {
    //                             for (int z = 0; z < lineSegments.length; z++)
    //                                 aux2[z] = lineSegments[z];
    //                         }
    //                         lineSegments = aux2;
    //                         lineSegments[lineIndex] = line;
    //                         lineIndex++;
    //                     }
    //                     temp = new Point[0];
    //                 }
    //             }
    //         }
    //         else if (temp.length >= 4) {
    //             Arrays.sort(temp);
    //             int compare = 0;
    //             Point[] arr = new Point[2];
    //             arr[0] = temp[0];
    //             arr[1] = temp[temp.length - 1];
    //             for (int z = 0; z < pointsList.size(); z++) {
    //                 if (Arrays.equals(pointsList.get(z), arr)) compare = 1;
    //             }
    //             if (compare == 0) {
    //                 pointsList.add(arr);
    //                 LineSegment line = new LineSegment(temp[0], temp[temp.length - 1]);
    //                 LineSegment[] aux = new LineSegment[lineIndex + 1];
    //                 if (lineSegments.length != 0) {
    //                     for (int z = 0; z < lineSegments.length; z++)
    //                         aux[z] = lineSegments[z];
    //                 }
    //                 lineSegments = aux;
    //                 lineSegments[lineIndex] = line;
    //                 lineIndex++;
    //             }
    //             temp = new Point[0];
    //         }
    //         else temp = new Point[0];
    //         slopes.add(slope);


    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] copy = Arrays.copyOf(lineSegments, lineSegments.length);
        return copy;

    }

    private static boolean isSorted(Point[] a, int first, int second) {
        for (int i = first; i <= second; i++) {
            if (a[i].compareTo(a[i + 1]) > 0) return false;
        }
        return true;
    }

    private static boolean less(Point a, Point b) {
        return a.compareTo(b) < 0;
    }


    private static void merge(Point[] a, Point[] aux, int lo, int mid, int hi) {
        assert isSorted(a, lo, mid);
        assert isSorted(a, mid + 1, hi);
        for (int k = lo; k <= hi; k++) aux[k] = a[k];
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) a[k] = aux[j++];
            else if (j > hi) a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else a[k] = aux[i++];
        }
        assert isSorted(a, lo, hi);
    }

    private static void sort(Point[] a, Point[] aux, int lo, int hi) {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid);
        sort(a, aux, mid + 1, hi);
        merge(a, aux, lo, mid, hi);
    }

    private static void sort(Point[] a) {
        Point[] aux = new Point[a.length];
        sort(a, aux, 0, a.length - 1);
    }

    public static void main(String[] args) {

        // read the n points from a file

        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
