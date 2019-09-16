/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 8/17/19
 *  Description: KdTree
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;

public class KdTree {

    private Node root;
    private int size = 0;
    private List<Point2D> points;
    private Point2D nearest = null;

    private static class Node {
        private final Point2D p;
        private Node lb;
        private Node rt;
        private final RectHV rect;

        public Node(Point2D p, Node lb, Node rt, RectHV rect) {
            this.p = p;
            this.lb = lb;
            this.rt = rt;
            this.rect = rect;
        }
    }

    // construct an empty set of points
    public KdTree() {
        root = null;
    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point is null or invalid");
        if (!contains(p)) size++;
        put(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Point is null or invalid");
        return get(p) != null;
    }

    private void draw(Node a, boolean verticalOrientation) {
        if (a == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        a.p.draw();
        StdDraw.text(a.p.x(), a.p.y(), a.p.toString());
        if (verticalOrientation) {
            StdDraw.setPenRadius();
            StdDraw.setPenColor(StdDraw.RED);
            a.rect.draw();
            draw(a.lb, false);
            draw(a.rt, false);
        }
        else {
            StdDraw.setPenRadius();
            StdDraw.setPenColor(StdDraw.BLUE);
            a.rect.draw();
            draw(a.lb, true);
            draw(a.rt, true);
        }
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, false);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Rectangle is null or invalid");
        points = new ArrayList<>();
        if (root == null) return points;
        searchRange(root, rect);
        return points;
    }

    private void searchRange(Node a, RectHV rect) {
        if (a == null) return;
        else if (rect.intersects(a.rect)) {
            if (rect.contains(a.p)) points.add(a.p);
            searchRange(a.lb, rect);
            searchRange(a.rt, rect);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (root == null) return null;
        else if (p == null) throw new IllegalArgumentException("Point is null or invalid");
        nearest = root.p;
        return searchNearest(root, p, false);
    }


    private Point2D searchNearest(Node a, Point2D point, boolean verticalOrientation) {
        if (a == null) return nearest;
        if (a.p.distanceSquaredTo(point) < nearest.distanceSquaredTo(point)) {
            nearest = a.p;
        }
        if (nearest.distanceSquaredTo(point) < a.rect.distanceSquaredTo(point)) {
            return nearest;
        }
        else if (!verticalOrientation) {
            if (point.x() < a.p.x()) {
                if (nearest.distanceSquaredTo(point) > a.rect.distanceSquaredTo(point)) {
                    searchNearest(a.lb, point, true);
                    searchNearest(a.rt, point, true);
                }
            }
            else {
                if (nearest.distanceSquaredTo(point) > a.rect.distanceSquaredTo(point)) {
                    searchNearest(a.rt, point, true);
                    searchNearest(a.lb, point, true);
                }
            }
        }
        else {
            if (point.y() < a.p.y()) {
                if (nearest.distanceSquaredTo(point) > a.rect.distanceSquaredTo(point)) {
                    searchNearest(a.lb, point, false);
                    searchNearest(a.rt, point, false);
                }
            }
            else {

                if (nearest.distanceSquaredTo(point) > a.rect.distanceSquaredTo(point)) {
                    searchNearest(a.rt, point, false);
                    searchNearest(a.lb, point, false);
                }
            }
        }
        return nearest;
    }

    private void put(Point2D p) {
        root = put(root, p, false, null);
    }

    private Node put(Node h, Point2D p, boolean verticalOrientation, Node previous) {
        if (h == null) {
            if (root == null) {
                return new Node(p, null, null, new RectHV(0, 0, 1, 1));
            }
            double xmin = previous.rect.xmin();
            double xmax = previous.rect.xmax();
            double ymin = previous.rect.ymin();
            double ymax = previous.rect.ymax();
            if (verticalOrientation) {
                if (p.x() < previous.p.x()) {
                    if (xmin < previous.p.x()) {
                        return new Node(p, null, null, new RectHV(xmin,
                                                                  ymin,
                                                                  previous.p.x(),
                                                                  ymax));
                    }
                    else {
                        return new Node(p, null, null, new RectHV(previous.p.x(),
                                                                  ymin,
                                                                  xmax,
                                                                  ymax));
                    }
                }
                else {
                    if (xmax > previous.p.x()) {
                        return new Node(p, null, null, new RectHV(previous.p.x(),
                                                                  ymin,
                                                                  xmax,
                                                                  ymax));
                    }
                    else {
                        return new Node(p, null, null, new RectHV(xmin,
                                                                  ymin,
                                                                  previous.p.x(),
                                                                  ymax));
                    }
                }
            }
            else {
                if (p.y() < previous.p.y()) {
                    if (ymin < previous.p.y()) {
                        return new Node(p, null, null, new RectHV(xmin,
                                                                  ymin,
                                                                  xmax,
                                                                  previous.p.y()));
                    }
                    else {
                        return new Node(p, null, null, new RectHV(xmin,
                                                                  previous.p.y(),
                                                                  xmax,
                                                                  ymax));
                    }
                }
                else {
                    if (ymax > previous.p.y()) {
                        return new Node(p, null, null, new RectHV(xmin,
                                                                  previous.p.y(),
                                                                  xmax,
                                                                  ymax));
                    }
                    else {
                        return new Node(p, null, null, new RectHV(xmin,
                                                                  ymin,
                                                                  xmax,
                                                                  previous.p.y()));
                    }
                }
            }
        }
        else if (!verticalOrientation) {
            if (p.compareTo(h.p) == 0) return h;
            else if (p.x() < h.p.x()) h.lb = put(h.lb, p, true, h);
            else h.rt = put(h.rt, p, true, h);

        }
        else {
            if (p.compareTo(h.p) == 0) return h;
            else if (p.y() < h.p.y()) h.lb = put(h.lb, p, false, h);
            else h.rt = put(h.rt, p, false, h);
        }
        return h;
    }

    private Node get(Point2D p) {
        return get(root, p, false);
    }

    private Node get(Node h, Point2D p, boolean verticalOrientation) {
        if (h == null) return null;
        if (!verticalOrientation) {
            if (p.compareTo(h.p) == 0) return h;
            if (p.x() < h.p.x()) return get(h.lb, p, true);
            else return get(h.rt, p, true);
        }
        else {
            if (p.compareTo(h.p) == 0) return h;
            if (p.y() < h.p.y()) return get(h.lb, p, false);
            else return get(h.rt, p, false);
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree tree = new KdTree();
        String filename = args[0];
        In in = new In(filename);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            tree.insert(p);
        }
        tree.draw();
        // tree.info();
        // RectHV rect = new RectHV(0.793893, 0.345492, 1.0, 0.654508);
        // rect.draw();
        // Point2D point = new Point2D(0.5, 0.5);
        // point.draw();
        // StdDraw.show();
        // StdDraw.pause(40);
        // StdDraw.text(0.5, 0.5, "ksdjlfjasdlfkjsadkflasdjfdaslkjfas");
        // StdDraw.show();
        // while (true) {
        //     System.out.println(StdDraw.mouseX());
        //     StdDraw.show();
        // }
        // // Point2D point = new Point2D(0.5, 0.5);
        // // Point2D point2 = new Point2D(0.25, 0.25);
        // // Point2D point3 = new Point2D(0.75, 0.75);
        // // RectHV rect = new RectHV(0.0, 0.0, 0.3, 0.3);
        // // RectHV rect2 = new RectHV(0.0, 0.0, 0.3, 0.3);
        // tree.insert(new Point2D(0.5, 0.5));
        // tree.insert(new Point2D(0.0, 0.0));
        // tree.insert(new Point2D(0.75, 1.0));
        // tree.insert(new Point2D(0.75, 0.0));
        // tree.insert(new Point2D(1.0, 1.0));
        // tree.insert(new Point2D(0.25, 1.0));
        // tree.insert(new Point2D(0.75, 0.75));
        // tree.insert(new Point2D(1.0, 0.5));
        // tree.insert(new Point2D(1.0, 0.0));
        // tree.insert(new Point2D(1.0, 0.75));
        // // tree.insert(new Point2D(0.375, 1.0));
        // // Point2D point = new Point2D(0.375, 1.0);
        // // System.out.println(tree.get(point));
        // // A  0.5 0.5
        // // B  0.0 0.0
        // // C  0.75 1.0
        // // D  0.75 0.0
        // // E  1.0 1.0
        // // F  0.25 1.0
        // // G  0.75 0.75
        // // H  1.0 0.5
        // // I  1.0 0.0
        // // J  1.0 0.75
        // // System.out.println(tree.isEmpty());
        // // tree.insert(point);
        // // tree.insert(point2);
        // // tree.insert(point3);
        // // tree.insert(point);
        // // tree.insert(point);
        // // tree.insert(point);
        // // System.out.println(tree.isEmpty());
        // System.out.println(tree.size());
        // // System.out.println(tree.get(new Point2D(0.375, 1.0)));
        // System.out.println(tree.contains(new Point2D(1.0, 0.0)));
        // // System.out.println(tree.contains(new Point2D(0.5, 0.5)));
        // // tree.draw();
        // // for (Point2D points : tree.range(rect)) System.out.println(points);
        // // System.out.println();
        // // for (Point2D points : tree.range(rect2)) System.out.println(points);
    }
}
