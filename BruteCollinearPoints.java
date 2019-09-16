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

public class BruteCollinearPoints {

    // private ArrayList<LineSegment> lineSegments = new ArrayList<>();
    private final ArrayList<Point[]> twoPointsList = new ArrayList<>();
    private LineSegment[] lineSegments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        // Ensure valid input
        if (points == null) throw new IllegalArgumentException("Array supplied is null");
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException("Point is null");
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].equals(points[j]))
                    throw new IllegalArgumentException("Equal points are not allowed");
            }
        }
        lineSegments = new LineSegment[0];
        if (points.length < 4) return;

        Point[] copy = Arrays.copyOf(points, points.length);
        int lineIndex = 0;
        // Iterate over array four separate times and check for any four collinear points
        for (int i = 0; i < copy.length; i++) {
            for (int j = i + 1; j < copy.length; j++) {
                for (int k = j + 1; k < copy.length; k++) {
                    for (int m = k + 1; m < copy.length; m++) {
                        if (copy[i].slopeTo(copy[j]) == copy[i].slopeTo(copy[k])
                                && copy[i].slopeTo(copy[j]) == copy[i]
                                .slopeTo(copy[m])) {
                            // If collinear points found, send them to an array of points to make the line segment
                            Point[] temp = new Point[4];
                            temp[0] = copy[i];
                            temp[1] = copy[j];
                            temp[2] = copy[k];
                            temp[3] = copy[m];
                            Arrays.sort(temp);
                            Point[] twoPoints = new Point[2];
                            twoPoints[0] = temp[0];
                            twoPoints[1] = temp[3];
                            int test = 0;
                            // Test for duplicate line segments
                            for (Point[] pointArr : twoPointsList) {
                                if (Arrays.equals(pointArr, twoPoints)) {
                                    test = 1;
                                    break;
                                }
                            }
                            // If line is not a duplicate, create the line segment
                            if (test == 0) {
                                twoPointsList.add(twoPoints);
                                LineSegment line = new LineSegment(temp[0], temp[3]);
                                if (lineSegments.length == lineIndex) {
                                    LineSegment[] aux = new LineSegment[lineIndex + 1];
                                    for (int z = 0; z < lineSegments.length; z++)
                                        aux[z] = lineSegments[z];
                                    lineSegments = aux;
                                }
                                lineSegments[lineIndex] = line;
                                lineIndex++;
                            }
                        }
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] copy = Arrays.copyOf(lineSegments, lineSegments.length);
        return copy;
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}


