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
    float[] accelerations;
    float[] colors;

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
                    colors[i] = map(accelerations[i], -10, 10, 0,255);
                }
                invalidate();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        brush = new Paint();
        brush.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw the thing
        brush.setColor(Color.rgb((int) colors[0], (int) colors[1], (int) colors[2]));
        canvas.drawRect(0,0,getWidth(),getHeight(),brush);
    }

    float map(float v, float vStart, float vEnd, float min, float max) {
        v = Math.max(v, vStart);
        v = Math.min(v, vEnd);
        float delta = v - vStart;
        return min + (max - min) * delta / (vEnd - vStart);
    }
}

