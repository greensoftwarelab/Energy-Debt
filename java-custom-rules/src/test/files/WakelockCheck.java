import android.os.PowerManager;
import android.app.Activity;

class WakelockCheck extends Activity{
    private WakeLock wakeLock;

    @Override()
    public void onPause() { // Noncompliant@-1 {{Wakelock}}
        super.onPause();
        return;
    }

    @Override()
    public void onPause() {
        super.onPause();
        if(wakeLock.isHeld()) wakeLock.release();
    }

    public void onPause() {
        return;
    }

}

class NotWakelockCheck{
    public void foo();
}