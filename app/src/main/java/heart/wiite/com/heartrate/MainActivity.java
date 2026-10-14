package heart.wiite.com.heartrate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Sensor stepCounterSensor;
    
    private TextView tvHeartRate;
    private TextView tvBloodOxygen;
    private TextView tvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHeartRate = findViewById(R.id.tvHeartRate);
        tvBloodOxygen = findViewById(R.id.tvBloodOxygen);
        tvSteps = findViewById(R.id.tvSteps);

        // 申请权限
        if (ActivityCompat.checkSelfPermission(this, "android.permission.BODY_SENSORS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.BODY_SENSORS",
                    "android.permission.ACTIVITY_RECOGNITION"
            }, 100);
        }

        // 初始化传感器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        if (heartRateSensor == null) {
            tvHeartRate.setText("心率: 不支持");
        }
        if (stepCounterSensor == null) {
            tvSteps.setText("步数: 不支持");
        }
        // 注意：标准安卓没有统一血氧传感器，需厂商SDK
        tvBloodOxygen.setText("血氧: 需厂商SDK");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;

        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            int bpm = (int) event.values[0];
            tvHeartRate.setText("心率: " + bpm + " bpm");
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int steps = (int) event.values[0];
            tvSteps.setText("步数: " + steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
