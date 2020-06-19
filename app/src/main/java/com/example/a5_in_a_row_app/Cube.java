package com.example.a5_in_a_row_app;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Calendar;

public class Cube {

    float[][] coors;
    float[][] newCoors;
    float[] angle;
    float[][] R; // rotation matrix
    public Cube(float halfSize) {
        coors = new float[8][];
        coors[0] = new float[] {halfSize, halfSize, halfSize};
        coors[1] = new float[] {halfSize, halfSize, - halfSize};
        coors[2] = new float[] {halfSize, - halfSize, halfSize};
        coors[3] = new float[] {halfSize, - halfSize, - halfSize};
        coors[4] = new float[] {- halfSize, halfSize, halfSize};
        coors[5] = new float[] {- halfSize, halfSize, - halfSize};
        coors[6] = new float[] {- halfSize, - halfSize, halfSize};
        coors[7] = new float[] {- halfSize, - halfSize, - halfSize};

        angle = new float[]{0f, 0f, 0f};

        R = new float[3][3];
        newCoors = new float[8][3];
    }

    void draw(Canvas c, Paint p, float cX, float cY) {
        // draw 6 faces
        c.translate(cX,cY);
        c.drawPath(getQuadPath(newCoors[0], newCoors[1], newCoors[3], newCoors[2]), p);
        c.drawPath(getQuadPath(newCoors[4], newCoors[5], newCoors[7], newCoors[6]), p);

        c.drawPath(getQuadPath(newCoors[0], newCoors[1], newCoors[5], newCoors[4]), p);
        c.drawPath(getQuadPath(newCoors[2], newCoors[3], newCoors[7], newCoors[6]), p);

        c.drawPath(getQuadPath(newCoors[0], newCoors[2], newCoors[6], newCoors[4]), p);
        c.drawPath(getQuadPath(newCoors[1], newCoors[3], newCoors[7], newCoors[5]), p);
        c.translate(-cX,-cY);
    }

    Path getQuadPath(float[] a, float[] b, float[] c, float[] d) {
        Path path = new Path();
        path.moveTo(a[0], a[1]);
        path.lineTo(b[0], b[1]);
        path.lineTo(c[0], c[1]);
        path.lineTo(d[0], d[1]);
        path.lineTo(a[0], a[1]);
        path.close();
        return path;
    }

    void rotate(float x, float y, float z) {
        angle[0] += x;
        angle[1] += y;
        angle[2] += z;
        calculateRotationMatrix();
        applyRotationMatrix();
    }

    void applyRotationMatrix() {
        for (int i = 0; i < coors.length; i++) {
            for (int j = 0; j < coors[i].length; j++) {
                newCoors[i][j] = coors[i][0] * R[j][0] + coors[i][1] * R[j][1] + coors[i][2] * R[j][2];
            }
        }
        // to R2
        // just dont use Z
    }


    void setAngle(float x, float y, float z) {
        angle[0] = x;
        angle[1] = y;
        angle[2] = z;
        calculateRotationMatrix();
        applyRotationMatrix();
    }

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
