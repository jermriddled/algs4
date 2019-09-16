/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 8/17/19
 *  Description: PointSET
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PointSET {

    private Set<Point2D> pointSET;

    // construct an empty set of points
    public PointSET() {
        pointSET = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return pointSET.isEmpty();
    }

    // number of points in the set
    public int size() {
        return pointSET.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point is null or invalid");
        pointSET.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point is null or invalid");
        return pointSET.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D point : pointSET) point.draw();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Rectangle is null or invalid");
        List<Point2D> rangeList = new ArrayList<>();
        for (Point2D point : pointSET) {
            if (rect.contains(point)) rangeList.add(point);
        }
        return rangeList;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (pointSET == null) throw new IllegalArgumentException("Set is empty");
        else if (p == null) throw new IllegalArgumentException("Point is null or invalid");
        Point2D nearest = null;
        for (Point2D point : pointSET) {
            if (nearest == null) nearest = point;
            else if (p.distanceSquaredTo(point) < p.distanceSquaredTo(nearest)) nearest = point;
        }
        return nearest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // empty
    }
}

