package com.example.camer.swipetunes.model;

public class Gesture {
    public Point[] Points = null;
    public String Name = "";
    private int SAMPLING_RESOLUTION = 32;

    /// Constructs a gesture from an array of points
    public Gesture(Point[] points, String gestureName)
    {
        this.Name = gestureName;
        this.Points = Scale(points);
        this.Points = TranslateTo(Points, Centroid(Points));
        this.Points = Resample(Points, SAMPLING_RESOLUTION);
    }

    /// Performs scale normalization with shape preservation into [0..1]x[0..1]
    private Point[] Scale(Point[] points)
    {
        float minx = Float.MAX_VALUE, miny = Float.MAX_VALUE, maxx = Float.MIN_VALUE, maxy = Float.MIN_VALUE;
        for (int i = 0; i < points.length; i++)
        {
            if (minx > points[i].X) minx = points[i].X;
            if (miny > points[i].Y) miny = points[i].Y;
            if (maxx < points[i].X) maxx = points[i].X;
            if (maxy < points[i].Y) maxy = points[i].Y;
        }

        Point[] newPoints = new Point[points.length];
        float scale = Math.max(maxx - minx, maxy - miny);
        for (int i = 0; i < points.length; i++)
            newPoints[i] = new Point((points[i].X - minx) / scale, (points[i].Y - miny) / scale, points[i].StrokeID);
        return newPoints;
    }

    /// Translates the array of points by p
    private Point[] TranslateTo(Point[] points, Point p)
    {
        Point[] newPoints = new Point[points.length];
        for (int i = 0; i < points.length; i++)
            newPoints[i] = new Point(points[i].X - p.X, points[i].Y - p.Y, points[i].StrokeID);
        return newPoints;
    }

    /// Computes the centroid for an array of points
    private Point Centroid(Point[] points)
    {
        float cx = 0, cy = 0;
        for (int i = 0; i < points.length; i++)
        {
            cx += points[i].X;
            cy += points[i].Y;
        }
        return new Point(cx / points.length, cy / points.length, 0);
    }

    /// Resamples the array of points into n equally-distanced points
    public Point[] Resample(Point[] points, int n)
    {
        Point[] newPoints = new Point[n];
        newPoints[0] = new Point(points[0].X, points[0].Y, points[0].StrokeID);
        int numPoints = 1;

        float I = PathLength(points) / (n - 1); // computes interval length
        float D = 0;
        for (int i = 1; i < points.length; i++)
        {
            if (points[i].StrokeID == points[i - 1].StrokeID)
            {
                float d = Geometry.EuclideanDistance(points[i - 1], points[i]);
                if (D + d >= I)
                {
                    Point firstPoint = points[i - 1];
                    while (D + d >= I)
                    {
                        // add interpolated point
                        float t = Math.min(Math.max((I - D) / d, 0.0f), 1.0f);
                        if (Float.isNaN(t)) t = 0.5f;
                        newPoints[numPoints++] = new Point(
                                (1.0f - t) * firstPoint.X + t * points[i].X,
                                (1.0f - t) * firstPoint.Y + t * points[i].Y,
                                points[i].StrokeID
                        );

                        // update partial length
                        d = D + d - I;
                        D = 0;
                        firstPoint = newPoints[numPoints - 1];
                    }
                    D = d;
                }
                else D += d;
            }
        }

        if (numPoints == n - 1) // sometimes we fall a rounding-error short of adding the last point, so add it if so
            newPoints[numPoints++] = new Point(points[points.length - 1].X, points[points.length - 1].Y, points[points.length - 1].StrokeID);
        return newPoints;
    }

    /// Computes the path length for an array of points
    private float PathLength(Point[] points)
    {
        float length = 0;
        for (int i = 1; i < points.length; i++)
            if (points[i].StrokeID == points[i - 1].StrokeID)
                length += Geometry.EuclideanDistance(points[i - 1], points[i]);
        return length;
    }
}