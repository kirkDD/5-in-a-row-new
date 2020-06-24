package com.example.a5_in_a_row_app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class Cube {

    float halfSize;
    float[][] coors;
    float[][] newCoors;
    float[] angle;
    float[] target_angle;
    float[][] R; // rotation matrix

    //6 faces
    Path[] faces;
    int[] alphas;
    boolean[] front;

    Runnable postInvalidate;

    void setRunnable(Runnable r) {
        postInvalidate = r;
    }

    public Cube(float halfSize) {
        this.halfSize = halfSize;
        coors = new float[8][];
        coors[0] = new float[]{halfSize, halfSize, halfSize};
        coors[1] = new float[]{halfSize, halfSize, -halfSize};
        coors[2] = new float[]{halfSize, -halfSize, halfSize};
        coors[3] = new float[]{halfSize, -halfSize, -halfSize};
        coors[4] = new float[]{-halfSize, halfSize, halfSize};
        coors[5] = new float[]{-halfSize, halfSize, -halfSize};
        coors[6] = new float[]{-halfSize, -halfSize, halfSize};
        coors[7] = new float[]{-halfSize, -halfSize, -halfSize};

        angle = new float[]{0f, 0f, 0f};
        target_angle = new float[]{0f, 0f, 0f};

        R = new float[3][3];
        newCoors = new float[8][3];
        
        faces = new Path[6];
        alphas = new int[6];
        front = new boolean[6];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = new Path();
            front[i] = true;
        }
    }

    void draw(Canvas c, Paint p, float cX, float cY) {
        // draw 6 faces
        c.translate(cX,cY);
//        int faceColor = p.getColor();
        int faceColor = Color.RED;
        // draw the behind ones
        for (int i = 0; i < faces.length; i++) {
            if (front[i]) continue;
            p.setStyle(Paint.Style.STROKE);
            p.setColor(faceColor / 2);
            p.setAlpha(255);
            c.drawPath(faces[i], p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(faceColor);
            p.setAlpha(alphas[i]);
            c.drawPath(faces[i], p);
        }

        for (int i = 0; i < faces.length; i++) {
            if (!front[i]) continue;
            p.setStyle(Paint.Style.STROKE);
            p.setColor(faceColor / 2);
            p.setAlpha(255);
            c.drawPath(faces[i], p);
            p.setStyle(Paint.Style.FILL);
            p.setColor(faceColor);
            p.setAlpha(alphas[i]);
            c.drawPath(faces[i], p);
        }
        c.translate(-cX, -cY);
        if (evolveCurrentAngle() && postInvalidate != null) {
            postInvalidate.run();
        }
    }

    // return T iff this face is facing toward u
    boolean isFacingOut(float[] a, float[] b, float[] c, float[] d) {
        // the vector to center of face! genius!
        return a[2] + b[2] + c[2] + d[2] > 0;
    }

    void setPathAndAlpha() {
        setOnePathNAlpha(newCoors[0], newCoors[1], newCoors[3], newCoors[2], 0);
        setOnePathNAlpha(newCoors[4], newCoors[5], newCoors[7], newCoors[6], 1);
        setOnePathNAlpha(newCoors[0], newCoors[1], newCoors[5], newCoors[4], 2);
        setOnePathNAlpha(newCoors[2], newCoors[3], newCoors[7], newCoors[6], 3);
        setOnePathNAlpha(newCoors[0], newCoors[2], newCoors[6], newCoors[4], 4);
        setOnePathNAlpha(newCoors[1], newCoors[3], newCoors[7], newCoors[5], 5);
    }

    // return a drawable Path
    void setOnePathNAlpha(float[] a, float[] b, float[] c, float[] d, int index) {
        // calculate and set the alpha of p
        alphas[index] = areaOf(a, b, c, d);
        Path path = faces[index];
        path.reset();
        front[index] = isFacingOut(a, b,c,d);
        if (!front[index]) {
            alphas[index] = Math.min(255, alphas[index] + 10);
        } else {
            alphas[index] = Math.max(0, alphas[index] - 10);
        }
        path.moveTo(a[0], a[1]);
        path.lineTo(b[0], b[1]);
        path.lineTo(c[0], c[1]);
        path.lineTo(d[0], d[1]);
        path.lineTo(a[0], a[1]);
        path.close();
    }

    // return a good alpha
    int areaOf(float[] a, float[] b, float[] c, float[] d) {
        double circumference = Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
        circumference += Math.sqrt(Math.pow(b[0] - c[0], 2) + Math.pow(b[1] - c[1], 2));
        circumference += Math.sqrt(Math.pow(c[0] - d[0], 2) + Math.pow(c[1] - d[1], 2));
        circumference += Math.sqrt(Math.pow(d[0] - a[0], 2) + Math.pow(d[1] - a[1], 2));
        circumference /= halfSize * 2;
        return 255 - (int) map((float) circumference, 3, 4, 50, 200);
    }

    // rotate the cube by angles xyz
    void rotate(float x, float y, float z) {
        target_angle[0] += x;
        target_angle[1] += y;
        target_angle[2] += z;
        postSetAngle();
    }

    void setTargetAngle(float x, float y, float z) {
        target_angle[0] = x;
        target_angle[1] = y;
        target_angle[2] = z;
        if (evolveCurrentAngle()) {
            postSetAngle();
            postInvalidate.run();
        }
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
//        System.out.println("returning true in evolve");
        return true;
    }


    void postSetAngle() {
        calculateRotationMatrix();
        applyRotationMatrix();
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

    // helper to calculate rotated coordinates
    void applyRotationMatrix() {
        for (int i = 0; i < coors.length; i++) {
            for (int j = 0; j < coors[i].length; j++) {
                newCoors[i][j] = coors[i][0] * R[j][0] + coors[i][1] * R[j][1] + coors[i][2] * R[j][2];
            }
            // add perspective
            float scale = map(Math.abs(newCoors[i][2]), 0, (float) (halfSize * Math.sqrt(3)), 1, 1.5f);
            if (newCoors[i][2] < 0) {
                scale = 2f - scale;
            }
            for (int j = 0; j < 2; j++) {
                newCoors[i][j] *= scale;
            }
        }
        // to R2
        // just dont use Z
        setPathAndAlpha();
    }


    float map(float v, float vStart, float vEnd, float min, float max) {
        v = Math.max(v, vStart);
        v = Math.min(v, vEnd);
        float delta = v - vStart;
        return min + (max - min) * delta / (vEnd - vStart);
    }

}
