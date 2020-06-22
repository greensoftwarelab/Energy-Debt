import android.hardware.Camera;
import android.app.Activity;
import android.media.MediaRecorder;

class CameraResourceLeakCheck extends Activity {
    private Camera camera;
    private MediaRecorder mediaRecorder;

    @Override
    public void onPause() { // Noncompliant@-1 {{Camera Resource Leak}}
        super.onPause();
        return;
    }

    @Override()
    public void onPause() { // Noncompliant@-1 {{Camera Resource Leak}}
        super.onPause();
        if (camera != null){
            camera.release();
            camera = null;
        }
    }

    @Override()
    public void onPause() {
        super.onPause();
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
        if (camera != null){
            camera.release();
            camera = null;
        }
    }

    public void onPause() {
        return;
    }

}

class NotCameraResourceLeakCheck extends Activity {
    private Camera camera;

    @Override
    public void onPause() { // Noncompliant@-1 {{Camera Resource Leak}}
        super.onPause();
        return;
    }

    @Override()
    public void onPause() {
        super.onPause();
        if (camera != null){
            camera.release();
            camera = null;
        }
    }

    public void onPause() {
        return;
    }

}

class AlsoNotCameraResourceLeakCheck {

    public void onPause() {
        return;
    }

}