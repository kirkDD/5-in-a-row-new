package com.example.a5_in_a_row_app;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointCloud {

    float[] target_angle;
    float[] angle; // current angle
    float[][] R; // rotation matrix

    List<float[]> points;
    List<float[]> newPoints;

    float pointRadius;
    float maxDist;

    Runnable postInvalidate;

    void setRunnable(Runnable r) {
        postInvalidate = r;
    }

    /**
     * manage a list of points in 3D
     *
     * @param point_list a list of float arrays of length 3
     */
    public PointCloud(List<float[]> point_list, float point_size) {
        pointRadius = point_size;
        R = new float[3][3];
        angle = new float[3];
        target_angle = new float[3];
        points = point_list;
        newPoints = new ArrayList<>();
        maxDist = 0;
        for (float[] p : point_list) {
            float dist = (float) Math.sqrt(p[0] * p[0] + p[1] * p[1] + p[2] * p[2]);
            if (maxDist < dist) maxDist = dist;
            newPoints.add(new float[3]);
        }
    }

    public PointCloud(int numPoint, float maxSize, float point_size) {
        this(getNewPoints(numPoint, maxSize), point_size);
    }

    static List<float[]> getNewPoints(int numPoint, float maxDist) {
        Random r = new Random();
        List<float[]> randPoints = new ArrayList<>();
        for (; numPoint > 0; numPoint--) {
            float[] p = new float[]{
                    (r.nextFloat() - 0.5f) * maxDist,
                    (r.nextFloat() - 0.5f) * maxDist,
                    (r.nextFloat() - 0.5f) * maxDist};
            randPoints.add(p);
        }
        return randPoints;
    }

    void draw(Canvas c, Paint p, float cX, float cY) {
        // draw 6 faces
        p.setStyle(Paint.Style.FILL);
        c.translate(cX,cY);
        for (float[] pt : newPoints) {
            c.drawCircle(pt[0], pt[1], pointRadius +
                    pointRadius * maxDist / Math.abs(maxDist) *
                            map(Math.abs(pt[2]), 0, maxDist, 1, 2), p);
        }
        c.translate(-cX, -cY);
        if (evolveCurrentAngle() && postInvalidate != null) {
            postInvalidate.run();
        }
    }

    // rotate the cube by angles xyz
    void rotate(float x, float y, float z) {
        target_angle[0] += x;
        target_angle[1] += y;
        target_angle[2] += z;
        postSetAngle();
    }

    // set the angle
    void setTargetAngle(float x, float y, float z) {
        target_angle[0] = x;
        target_angle[1] = y;
        target_angle[2] = z;
        if (evolveCurrentAngle()) postSetAngle();
    }
    boolean evolveCurrentAngle() {
        float checkSum = 0;
        for (int i = 0; i < target_angle.length; i++) {
            checkSum += Math.abs(target_angle[i] - angle[i]);
            angle[i] += (target_angle[i] - angle[i]) * 0.1f;
        }
        if (checkSum < 0.0001) {
            for (int i = 0; i < target_angle.length; i++) {
                angle[i] = target_angle[i];
            }
            return false;
        }
        return true;
    }



    void postSetAngle() {
        calculateRotationMatrix();
        applyRotationMatrix();
    }

    // helper to calculate rotated coordinates
    void applyRotationMatrix() {
        for (int i = 0; i < points.size(); i++) {
            float[] old = points.get(i);
            float[] newP = newPoints.get(i);
            for (int j = 0; j < old.length; j++) {
                newP[j] = old[0] * R[j][0] + old[1] * R[j][1] + old[2] * R[j][2];
            }
        }
        // to R2
        // just dont use Z
    }

    // calculate current R matrix from current angle
    void calculateRotationMatrix() {
        R[0][0] = (float) (Math.cos(angle[0]) * Math.cos(angle[1]));
        R[1][0] = (float) (Math.sin(angle[0]) * Math.cos(angle[1]));
        R[0][1] = (float) (Math.cos(angle[0]) * Math.sin(angle[1]) * Math.sin(angle[2]) - Math.sin(angle[0]) * Math.cos(angle[2]));
        R[1][1] = (float) (Math.sin(angle[0]) * Math.sin(angle[1]) * Math.sin(angle[2]) + Math.cos(angle[0]) * Math.cos(angle[2]));
        R[0][2] = (float) (Math.cos(angle[0]) * Math.sin(angle[1]) * Math.cos(angle[2]) + Math.sin(angle[0]) * Math.sin(angle[2]));
        R[1][2] = (float) (Math.sin(angle[0]) * Math.sin(angle[1]) * Math.cos(angle[2]) - Math.cos(angle[0]) * Math.sin(angle[2]));
        R[2][0] = (float) - Math.sin(angle[1]);
        R[2][1] = (float) (Math.cos(angle[1]) * Math.sin(angle[2]));
        R[2][2] = (float) (Math.cos(angle[1]) * Math.cos(angle[2]));
    }

    float map(float v, float vStart, float vEnd, float min, float max) {
        v = Math.max(v, vStart);
        v = Math.min(v, vEnd);
        float delta = v - vStart;
        return min + (max - min) * delta / (vEnd - vStart);
    }

}
