package com.example.a5_in_a_row_app;

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
    float[] colors;

    // test
    PointCloud c;
    Cube cube;
    float cX;
    float cY;

    // animate a percentage
    public StrangeView(Context context) {
        super(context);
        colors = new float[3];

        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        final Sensor acceleration = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] accelerations = sensorEvent.values;
                float[] targetAngles = new float[3];
                int[] colors = new int[3];
                for (int i = 0; i < accelerations.length; i++) {
                    colors[i] = (int) map(accelerations[i], -10, 10, 0, 255);
                    if (c != null) {
                        targetAngles[i] = map(accelerations[i], -10, 10, (float) (-Math.PI), (float) (Math.PI));
                    }

                }
                brush.setColor(Color.rgb(colors[0], (int) colors[1], (int) colors[2]));
                if (cube != null) {
                    cube.setTargetAngle(targetAngles[0], targetAngles[1], targetAngles[2]);
                }
                if (c != null) {
                    c.setTargetAngle(targetAngles[0], targetAngles[1], targetAngles[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, acceleration, SensorManager.SENSOR_DELAY_NORMAL);

        brush = new Paint();
        brush.setStyle(Paint.Style.FILL);
        brush.setStrokeWidth(5);
        brush.setStrokeCap(Paint.Cap.ROUND);
        brush.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // draw cubes
        if (cX == 0 || c == null || cube == null) initDims();
        int temp_color = brush.getColor();
        cube.draw(canvas, brush, cX, cY);
        brush.setColor(temp_color / 2);
        brush.setAlpha(255);
        c.draw(canvas, brush, cX, cY);
        brush.setColor(temp_color);
    }

    void initDims() {
        cX = getWidth() / 2f;
        cY = getHeight() / 2f;
        if (c == null) {
            c = new PointCloud(20, Math.min(getWidth(), getHeight()) / 2f, 5);
            c.setRunnable(this::postInvalidate);
        }
        if (cube == null) {
            cube = new Cube(Math.min(getWidth(), getHeight()) / 2f / (float) Math.sqrt(3) - brush.getStrokeWidth() / 2);
            cube.setRunnable(this::postInvalidate);
        }
    }

    float map(float v, float vStart, float vEnd, float min, float max) {
        v = Math.max(v, vStart);
        v = Math.min(v, vEnd);
        float delta = v - vStart;
        return min + (max - min) * delta / (vEnd - vStart);
    }

}

