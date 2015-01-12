package pessanha.com.myfirstapp;

/**
 * Created by pessanha on 03/01/2015.
 */
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview  extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Preview";

    SurfaceHolder mHolder;
    public Camera camera;

    Preview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);

            camera.setPreviewCallback(new PreviewCallback() {

                public void onPreviewFrame(byte[] data, Camera arg1) {
//
                    Preview.this.invalidate();
                }
            });
        } catch (IOException e) {
            camera.release();
            camera= null;
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        //camera.release();
        this.getHolder().removeCallback(this);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
        /* devemos verificar se o size é supported*/
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size cs = sizes.get(0);
        parameters.setPreviewSize(cs.width, cs.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(parameters);
        camera.startPreview();
    }

}
