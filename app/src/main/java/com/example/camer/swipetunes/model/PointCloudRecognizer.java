package com.example.camer.swipetunes.model;

import android.util.Log;

import java.util.ArrayList;

public class PointCloudRecognizer {
    public static String Classify(Gesture inputGesture, ArrayList<Gesture> gestures) {
        float minDistance = Float.MAX_VALUE;
        String recognizedGesture = "none";
        for (int i = 0; i < gestures.size(); i++)
        {
            Gesture currentGesture = gestures.get(i);
            Log.d("", "Current gesture: " + currentGesture.Name);
            float distance = GreedyCloudMatch(inputGesture.Points, currentGesture.Points);
            if (distance < minDistance)
            {
                minDistance = distance;
                recognizedGesture = currentGesture.Name;
                Log.d("", "Recognized gesture was updated: " + recognizedGesture);
            }
        }
        return recognizedGesture;
    }
    private static float GreedyCloudMatch(Point[] points1, Point[] points2) {
        int n = points1.length; // the two clouds should have the same number of points by now
        float eps = 0.5f;       // controls the number of greedy search trials (eps is in [0..1])
        int step = (int) Math.floor((Math.pow(n, 1.0f - eps)));
        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < n; i += step)
        {
            float dist1 = CloudDistance(points1, points2, i);   // match points1 --> points2 starting with index point i
            float dist2 = CloudDistance(points2, points1, i);   // match points2 --> points1 starting with index point i
            minDistance = Math.min(minDistance, Math.min(dist1, dist2));
        }
        return minDistance;
    }

    /// Computes the distance between two point clouds by performing a minimum-distance greedy matching
    /// starting with point startIndex
    private static float CloudDistance(Point[] points1, Point[] points2, int startIndex)
    {
        int n = points1.length;       // the two clouds should have the same number of points by now
        boolean[] matched = new boolean[n]; // matched[i] signals whether point i from the 2nd cloud has been already matched
        for (boolean matchedElement : matched)
        {
            matchedElement = false;
        }

        float sum = 0;  // computes the sum of distances between matched points (i.e., the distance between the two clouds)
        int i = startIndex;
        do
        {
            int index = -1;
            float minDistance = Float.MAX_VALUE;
            for(int j = 0; j < n; j++)
                if (!matched[j])
                {
                    float dist = Geometry.SqrEuclideanDistance(points1[i], points2[j]);  // use squared Euclidean distance to save some processing time
                    if (dist < minDistance)
                    {
                        minDistance = dist;
                        index = j;
                    }
                }
            matched[index] = true; // point index from the 2nd cloud is matched to point i from the 1st cloud
            float weight = 1.0f - ((i - startIndex + n) % n) / (1.0f * n);
            sum += weight * minDistance; // weight each distance with a confidence coefficient that decreases from 1 to 0
            i = (i + 1) % n;
        } while (i != startIndex);
        return sum;
    }
}
