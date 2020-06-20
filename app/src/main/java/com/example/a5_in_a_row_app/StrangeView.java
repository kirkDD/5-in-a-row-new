package com.example.a5_in_a_row_app;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class StrangeView extends View {

    Paint brush;
    float[] accelerations;
    float[] colors;

    // test
    Cube c;
    float size;
    float cX;
    float cY;

    // for cube
    float[] currAngles;
    float[] targetAngles;

    // animate a percentage
    public StrangeView(Context context) {
        super(context);
        colors = new float[3];
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        final Sensor acceleration = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                accelerations = sensorEvent.values;
                for (int i = 0; i < accelerations.length; i++) {
                    colors[i] = map(accelerations[i], -10, 10, 0, 255);
                    if (c != null) {
                        targetAngles[i] = map(accelerations[i], -10, 10, (float) (-Math.PI), (float) (Math.PI));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        brush = new Paint();
        brush.setStyle(Paint.Style.FILL);
        brush.setStrokeWidth(5);
        currAngles = new float[3];
        targetAngles = new float[3];
        new Thread(() -> {
            while (true) {
                try {
                    // FPS
                    Thread.sleep(30);
                    for (int i = 0; i < currAngles.length; i++) {
                        currAngles[i] += (targetAngles[i] - currAngles[i]) * 0.1;
                    }
                    c.setAngle(currAngles[0], currAngles[1], currAngles[2]);
                    invalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        brush.setColor(Color.rgb((int) colors[0], (int) colors[1], (int) colors[2]));
        canvas.drawRect(0, 0, getWidth(), getHeight(), brush);
        // draw cube
        if (c == null) {
            c = new Cube(Math.min(getWidth(), getHeight()) / 3f);
            c.rotate(1, 1, 1);
            cX = getWidth() / 2f;
            cY = getHeight() / 2f;
        }
        brush.setColor(Color.argb(100, 0, 0, 0));
        c.draw(canvas, brush, cX, cY);

    }

    float map(float v, float vStart, float vEnd, float min, float max) {
        v = Math.max(v, vStart);
        v = Math.min(v, vEnd);
        float delta = v - vStart;
        return min + (max - min) * delta / (vEnd - vStart);
    }
}

