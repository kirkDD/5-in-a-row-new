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
    float[][] R; // rotation matrix

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

        R = new float[3][3];
        newCoors = new float[8][3];
    }

    void draw(Canvas c, Paint p, float cX, float cY) {
        // draw 6 faces
        c.translate(cX,cY);


        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.GREEN);
        c.drawPath(getQuadPath(newCoors[0], newCoors[1], newCoors[3], newCoors[2]), p);
        c.drawPath(getQuadPath(newCoors[4], newCoors[5], newCoors[7], newCoors[6]), p);

        c.drawPath(getQuadPath(newCoors[0], newCoors[1], newCoors[5], newCoors[4]), p);
        c.drawPath(getQuadPath(newCoors[2], newCoors[3], newCoors[7], newCoors[6]), p);

        c.drawPath(getQuadPath(newCoors[0], newCoors[2], newCoors[6], newCoors[4]), p);
        c.drawPath(getQuadPath(newCoors[1], newCoors[3], newCoors[7], newCoors[5]), p);

        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);
        c.drawPath(getQuadPath(newCoors[0], newCoors[1], newCoors[3], newCoors[2]), p);
        c.drawPath(getQuadPath(newCoors[4], newCoors[5], newCoors[7], newCoors[6]), p);

        c.drawPath(getQuadPath(newCoors[0], newCoors[1], newCoors[5], newCoors[4]), p);
        c.drawPath(getQuadPath(newCoors[2], newCoors[3], newCoors[7], newCoors[6]), p);

        c.drawPath(getQuadPath(newCoors[0], newCoors[2], newCoors[6], newCoors[4]), p);
        c.drawPath(getQuadPath(newCoors[1], newCoors[3], newCoors[7], newCoors[5]), p);

        c.translate(-cX, -cY);

    }

    // return T iff this face is facing toward u
    boolean isFacingOut(float[] a, float[] b, float[] c, float[] d) {
        // take the cross product?
//        float bound = halfSize * (float) Math.sqrt(2);
//        if (a[2] < - bound || b[2] < -bound || c[2] < -bound || d[2] < -bound) {
//            return false;
//        }
        // the vector to center! genius!
        return a[2] + b[2] + c[2] + d[2] > 0;
    }

    // return a drawable Path
    Path getQuadPath(float[] a, float[] b, float[] c, float[] d) {
        Path path = new Path();
        if (isFacingOut(a,b,c,d)) {
            path.moveTo(a[0], a[1]);
            path.lineTo(b[0], b[1]);
            path.lineTo(c[0], c[1]);
            path.lineTo(d[0], d[1]);
            path.lineTo(a[0], a[1]);
            path.close();
        }
        return path;
    }

    // rotate the cube by angles xyz
    void rotate(float x, float y, float z) {
        angle[0] += x;
        angle[1] += y;
        angle[2] += z;
        calculateRotationMatrix();
        applyRotationMatrix();
    }

    // helper to calculate rotated coordinates
    void applyRotationMatrix() {
        for (int i = 0; i < coors.length; i++) {
            for (int j = 0; j < coors[i].length; j++) {
                newCoors[i][j] = coors[i][0] * R[j][0] + coors[i][1] * R[j][1] + coors[i][2] * R[j][2];
            }
        }
        // to R2
        // just dont use Z
    }

    // set the angle
    void setAngle(float x, float y, float z) {
        angle[0] = x;
        angle[1] = y;
        angle[2] = z;
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



}
