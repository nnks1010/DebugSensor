package com.example.ntomoy.debugsensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sm;
    TextView direction;
    TextView tvgx, tvgy, tvgz;
    TextView tvax, tvay, tvaz;

    private float[] fAccell = null;
    private float[] fMagnetic = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        direction = (TextView) findViewById(R.id.textView7);
        tvax = (TextView) findViewById(R.id.textView);
        tvay = (TextView) findViewById(R.id.textView2);
        tvaz = (TextView) findViewById(R.id.textView3);
        tvgx = (TextView) findViewById(R.id.textView4);
        tvgy = (TextView) findViewById(R.id.textView5);
        tvgz = (TextView) findViewById(R.id.textView6);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER :
                tvax.setText(String.format("%f", event.values[0]));
                tvay.setText(String.format("%f", event.values[1]));
                tvaz.setText(String.format("%f", event.values[2]));
                fAccell = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD :
                tvgx.setText(String.format("%f", event.values[0]));
                tvgy.setText(String.format("%f", event.values[1]));
                tvgz.setText(String.format("%f", event.values[2]));
                fMagnetic = event.values.clone();
                break;
        }
        if (fAccell != null && fMagnetic != null) {
            float[] inR = new float[9];
             float[] outR = new float[9];
             float[] fAttitude = new float[3];
            // 回転行列を得る
            SensorManager.getRotationMatrix(inR, null, fAccell, fMagnetic);
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            SensorManager.getOrientation(outR, fAttitude);
            float angrad = fAttitude[0];
            int orientDegrees = (int) Math.floor(angrad >= 0 ? Math.toDegrees(angrad) : 360 + Math.toDegrees(angrad));
            direction.setText(String.format("方位 :%d", orientDegrees));
        }
    }
}
