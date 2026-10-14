package heart.wiite.com.heartrate;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Sensor bloodOxygenSensor; // 新增：血氧传感器
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
            // 厂家说血氧是 TYPE_HEART_BEAT
            bloodOxygenSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_BEAT); 
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        if (heartRateSensor == null) {
            tvHeartRate.setText("心率: 不支持");
        }
        if (bloodOxygenSensor == null) {
            tvBloodOxygen.setText("血氧: 不支持");
        }
        if (stepCounterSensor == null) {
            tvSteps.setText("步数: 不支持");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        // 注册血氧传感器
        if (bloodOxygenSensor != null) {
            sensorManager.registerListener(this, bloodOxygenSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

        int type = event.sensor.getType();
        
        // 心率
        if (type == Sensor.TYPE_HEART_RATE) {
            int bpm = (int) event.values[0];
            tvHeartRate.setText("心率: " + bpm + " bpm");
        } 
        // 血氧（厂家说 TYPE_HEART_BEAT 就是血氧）
        else if (type == Sensor.TYPE_HEART_BEAT) {
            int spo2 = (int) event.values[0];
            tvBloodOxygen.setText("血氧: " + spo2 + " %");
        }
        // 计步
        else if (type == Sensor.TYPE_STEP_COUNTER) {
            int steps = (int) event.values[0];
            tvSteps.setText("步数: " + steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
