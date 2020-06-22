import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}

public class NotSensorActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;

    @Override
    protected void onPause() { // Noncompliant@-1 {{Sensor Resource Leak}}
        super.onPause();
    }
}
