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
                // normalize and turn into angles
                float sum = 0;
                for (float f : accelerations) {
                    sum += f * f;
                }
                sum = (float) Math.sqrt(sum);
                for (int i = 0; i < accelerations.length; i++) {
                    accelerations[i] /= sum;
                }
                // turn into angles
                ///////////// issue: discontinuity from -PI to PI
                float alpha = (float) Math.atan2(accelerations[0], accelerations[1]);
                System.out.println(alpha);
                float beta = (float) Math.atan2(accelerations[2], accelerations[1]);
                if (c != null) {
                    // solve the jump -PI to PI ???
                    targetAngles[0] = alpha;
                    targetAngles[2] = beta;
                }
                for (int i = 0; i < accelerations.length; i++) {
                    // map to color, after normalized
                    colors[i] = map(accelerations[i], -1, 1, 0, 255);
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
                    Thread.sleep(20);
                    for (int i = 0; i < currAngles.length; i++) {
                        // solve the jump issue:
                        // how to get to tar[i]? knowing that -PI and PI are connected
                        float delta = (targetAngles[i] - currAngles[i]);
                        if (Math.abs(delta) > Math.PI) {
                            // go the other way
                            if (targetAngles[i] > 0) {
                                delta = (float) - (Math.PI - targetAngles[i] + Math.PI + currAngles[i]);
                            } else {
                                delta = (float) (Math.PI + targetAngles[i] + Math.PI - currAngles[i]);
                            }
                        }
                        // cap delta
//                        delta = (float) Math.min(delta, 0.01);
                        currAngles[i] += delta * 0.1f;
                        if (currAngles[i] < - Math.PI) {
                            currAngles[i] += 2f * Math.PI;
                        }
                        if (currAngles[i] > Math.PI) {
                            currAngles[i] -= 2f * Math.PI;
                        }
                    }
                    if (c != null) {
                        c.setAngle(currAngles[0], currAngles[1], currAngles[2]);
                    }
                    this.post(this::invalidate); // cant use runOnUiThread somehow
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // draw background
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeWidth(25);
        brush.setColor(Color.rgb((int) colors[0], (int) colors[1], (int) colors[2]));
        canvas.drawRect(0, 0, getWidth(), getHeight(), brush);
        // draw cube
        if (c == null) {
            c = new Cube(Math.min(getWidth(), getHeight()) / 3f);
            cX = getWidth() / 2f;
            cY = getHeight() / 2f;
        }
        brush.setStrokeWidth(10);
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

