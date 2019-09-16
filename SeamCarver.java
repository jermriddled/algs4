/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 9/13/19
 *  Description: SeamCarver
 **************************************************************************** */

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.Topological;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;

public class SeamCarver {

    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Null argument to constructor");
        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // calculate the horizontal gradient by finding the color of the pixel to the left and right of
    // the pixel passed as a parameter and plugging those into the equation
    // Δ2x(x,y)=Rx(x,y)2+Gx(x,y)2+Bx(x,y)2, then do the same for the vertical gradient
    private double xGradient(int x, int y) {
        Color colorLeft = picture.get(x - 1, y);
        Color colorRight = picture.get(x + 1, y);
        double Rx = colorRight.getRed() - colorLeft.getRed();
        double Gx = colorRight.getGreen() - colorLeft.getGreen();
        double Bx = colorRight.getBlue() - colorLeft.getBlue();
        double gradient = Math.pow(Rx, 2) + Math.pow(Gx, 2) + Math.pow(Bx, 2);
        return gradient;
    }

    private double yGradient(int x, int y) {
        Color colorTop = picture.get(x, y - 1);
        Color colorBottom = picture.get(x, y + 1);
        double Ry = colorBottom.getRed() - colorTop.getRed();
        double Gy = colorBottom.getGreen() - colorTop.getGreen();
        double By = colorBottom.getBlue() - colorTop.getBlue();
        double gradient = Math.pow(Ry, 2) + Math.pow(Gy, 2) + Math.pow(By, 2);
        return gradient;
    }

    // energy of pixel at column x and row y
    // dual gradient energy function = the square root of the sum of the horizontal and vertical
    // gradients
    public double energy(int x, int y) {
        if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1) throw new
                IllegalArgumentException("Invalid pixel");
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) return 1000;

        double xGradient = xGradient(x, y);
        double yGradient = yGradient(x, y);
        return Math.sqrt(xGradient + yGradient);
    }

    // Robert Sedgwick and Kevin Wayne's AcyclicSP -
    // https://algs4.cs.princeton.edu/44sp/AcyclicSP.java.html

    private class CustomAcyclicSP {
        private DirectedEdge[] edgeTo;
        private double[] distTo;
        private double totalEnergy = 0;

        public CustomAcyclicSP(EdgeWeightedDigraph G, int s) {
            edgeTo = new DirectedEdge[G.V()];
            distTo = new double[G.V()];

            for (int v = 0; v < G.V(); v++)
                distTo[v] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;
            Topological topological = new Topological(G);
            for (int v : topological.order()) {
                for (DirectedEdge e : G.adj(v))
                    relax(e);
            }
        }

        private void relax(DirectedEdge e) {
            int v = e.from(), w = e.to();
            if (distTo[w] > distTo[v] + e.weight()) {
                distTo[w] = distTo[v] + e.weight();
                totalEnergy = totalEnergy + e.weight() + distTo[v];
                edgeTo[w] = e;
            }
        }

        public boolean hasPathTo(int v) {
            validateVertex(v);
            return distTo[v] < Double.POSITIVE_INFINITY;
        }

        private void validateVertex(int v) {
            int V = distTo.length;
            if (v < 0 || v >= V)
                throw new IllegalArgumentException(
                        "vertex " + v + " is not between 0 and " + (V - 1));
        }

        public Iterable<DirectedEdge> pathTo(int v) {
            validateVertex(v);
            if (!hasPathTo(v)) return null;
            Stack<DirectedEdge> path = new Stack<DirectedEdge>();
            for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
                path.push(e);
            }
            return path;
        }

        public DirectedEdge[] getEdgeTo() {
            return edgeTo;
        }
    }


    // sequence of indices for horizontal seam
    // transpose the image, run findVerticalSeam, and then transpose the image back to original
    // dimensions
    public int[] findHorizontalSeam() {
        if (width() <= 1) {
            int[] invalid = { 0 };
            return invalid;
        }
        int width = width();
        int height = height();
        Picture temp = picture;
        picture = new Picture(height, width);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                picture.set(j, i, temp.get(i, j));
            }
        }

        int[] path = findVerticalSeam();
        picture = temp;

        return path;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (height() <= 1) {
            int[] invalid = { 0 };
            return invalid;
        }
        int width = width();
        int height = height();
        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(width * height);
        double[][] energy = new double[height()][width()];

        // corner case for an image of width of one pixel
        if (width == 1) {
            for (int i = 0; i < height; i++) {
                if (i == height - 1) continue;
                energy[i][0] = energy(0, i);
                DirectedEdge edge = new DirectedEdge(i, i + 1, energy[i][0]);
                digraph.addEdge(edge);
            }
        }
        else {
            for (int i = 0; i < height(); i++) {
                for (int j = 0; j < width(); j++) {
                    energy[i][j] = energy(j, i);
                }
            }

            // construct the digraph
            for (int i = 0; i < height - 1; i++) {
                for (int j = 0; j < width; j++) {
                    if (j == 0) {
                        DirectedEdge edge1 = new DirectedEdge(j + (i * width),
                                                              j + ((i + 1) * width),
                                                              energy[i + 1][j]);
                        DirectedEdge edge2 = new DirectedEdge(j + (i * width),
                                                              j + ((i + 1) * width) + 1,
                                                              energy[i + 1][j + 1]);
                        digraph.addEdge(edge1);
                        digraph.addEdge(edge2);
                    }
                    else if (j == width() - 1) {
                        DirectedEdge edge1 = new DirectedEdge(j + (i * width),
                                                              j + ((i + 1) * width),
                                                              energy[i + 1][j]);
                        DirectedEdge edge2 = new DirectedEdge(j + (i * width),
                                                              j + ((i + 1) * width) - 1,
                                                              energy[i + 1][j - 1]);
                        digraph.addEdge(edge1);
                        digraph.addEdge(edge2);
                    }
                    else {
                        DirectedEdge edge1 = new DirectedEdge(j + (i * width),
                                                              j + ((i + 1) * width),
                                                              energy[i + 1][j]);
                        DirectedEdge edge2 = new DirectedEdge(j + (i * width),
                                                              j + ((i + 1) * width) + 1,
                                                              energy[i + 1][j + 1]);
                        DirectedEdge edge3 = new DirectedEdge(j + (i * width),
                                                              j + ((i + 1) * width) - 1,
                                                              energy[i + 1][j - 1]);
                        digraph.addEdge(edge1);
                        digraph.addEdge(edge2);
                        digraph.addEdge(edge3);
                    }
                }
            }
        }

        // find the shortest path by iterating through every available path and comparing
        // the total energy of each one
        double leastEnergy = Integer.MAX_VALUE;
        CustomAcyclicSP shortestSP = null;
        for (int i = 0; i < width(); i++) {
            CustomAcyclicSP SP = new CustomAcyclicSP(digraph, i);
            int first = 0;
            for (int z = 0; z < width; z++) {
                int last = width() * height() - width() + z;
                double currentEnergy = 0;
                if (SP.edgeTo[last] != null) {
                    for (DirectedEdge e : SP.pathTo(SP.edgeTo[last].to())) {
                        currentEnergy += e.weight();
                    }
                    currentEnergy += SP.edgeTo[last].weight();
                    if (currentEnergy < leastEnergy) {
                        leastEnergy = currentEnergy;
                        shortestSP = SP;
                    }
                }
            }
        }

        Iterable<DirectedEdge> edgePath2 = null;
        double shortestPath = Integer.MAX_VALUE;
        for (int z = 0; z < width; z++) {
            double currentPath = 0;
            int last = width() * height() - width() + z;
            if (shortestSP.edgeTo[last] != null) {
                Iterable<DirectedEdge> edgePath = shortestSP.pathTo(shortestSP.edgeTo[last].to());
                for (DirectedEdge e : edgePath) {
                    currentPath += e.weight();
                }
                if (currentPath < shortestPath) {
                    shortestPath = currentPath;
                    edgePath2 = edgePath;
                }
            }
        }
        int[] path = new int[1];
        int pathIndex = 0;
        for (DirectedEdge e : edgePath2) {
            if (path.length == pathIndex) {
                path = Arrays.copyOf(path, path.length + 1);
            }
            path[pathIndex] = e.from() - (pathIndex * width());
            pathIndex++;
        }
        path = Arrays.copyOf(path, path.length + 1);
        path[path.length - 1] = path[path.length - 2];
        return path;
    }

    private void validateVertical(int[] seam) {
        if (width() <= 1) throw new IllegalArgumentException("Picture cannot be carved");
        if (seam == null)
            throw new IllegalArgumentException("Null argument passed to removeVerticalSeam");
        if (seam.length != height())
            throw new IllegalArgumentException("Invalid argument passed to removeVerticalSeam");
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] >= width() || seam[i] < 0)
                throw new IllegalArgumentException("Invalid argument passed to removeVerticalSeam");
            else if (i != seam.length - 1) {
                if (seam[i + 1] > seam[i] + 1 || seam[i + 1] < seam[i] - 1) throw new
                        IllegalArgumentException("Invalid argument passed to removeVerticalSeam");
            }
        }
    }

    // method to help ensure proper usage of removeVerticalSeam and removeHorizontalSeam
    private void validateHorizontal(int[] seam) {
        if (height() <= 1) throw new IllegalArgumentException("Picture cannot be carved");
        if (seam == null)
            throw new IllegalArgumentException("Null argument passed to removeVerticalSeam");
        if (seam.length != width())
            throw new IllegalArgumentException("Invalid argument passed to removeVerticalSeam");
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] >= height())
                throw new IllegalArgumentException("Invalid argument passed to removeVerticalSeam");
            else if (i != seam.length - 1) {
                if (seam[i + 1] > seam[i] + 1 || seam[i + 1] < seam[i] - 1) throw new
                        IllegalArgumentException("Invalid argument passed to removeVerticalSeam");
            }
        }
    }

    // remove horizontal seam from current picture
    // transpose the image, run removeVerticalSeam, and transpose the image back
    // to original dimensions
    public void removeHorizontalSeam(int[] seam) {
        validateHorizontal(seam);
        int width = width();
        int height = height();
        Picture temp = picture;
        picture = new Picture(height, width);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                picture.set(j, i, temp.get(i, j));
            }
        }

        removeVerticalSeam(seam);
        temp = picture;
        picture = new Picture(width, height - 1);
        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width; j++) {
                picture.set(j, i, temp.get(i, j));
            }
        }
    }

    // remove vertical seam from current picture
    // at the specified seam, begin shifting every pixel one pixel to the left
    public void removeVerticalSeam(int[] seam) {
        validateVertical(seam);
        int width = width();
        int height = height();
        Picture temp = picture;
        picture = new Picture(width - 1, height);
        for (int i = 0; i < height; i++) {
            int flag = 0;
            for (int j = 0; j < width - 1; j++) {
                if (j == seam[i]) {
                    flag = 1;
                }
                if (flag == 1) {
                    picture.setRGB(j, i, temp.getRGB(j + 1, i));
                }
                else {
                    picture.setRGB(j, i, temp.getRGB(j, i));
                }
            }
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        File file = new File(args[0]);
        Picture picture = new Picture(file);
        SeamCarver sc = new SeamCarver(picture);
        sc.findVerticalSeam();
    }
}
