package pessanha.com.myfirstapp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;



public class CameraPreview
        implements
        SurfaceHolder.Callback {
    public Activity activity;
    public Camera mCamera = null;
    public Camera.Parameters params;
    private SurfaceHolder sHolder;
    private int surfaceRotation; // save rotation from display
    public List<Camera.Size> supportedSizes;

private int rotation;


    public int isCamOpen = 0;
    public boolean isSizeSupported = false;
    private int previewWidth, previewHeight;

    private final static String TAG = "CameraPreview";

    public CameraPreview(int width, int height) {

        Log.i("campreview", "Width = " + String.valueOf(width));
        Log.i("campreview", "Height = " + String.valueOf(height));
        previewWidth = width;
        previewHeight = height;


    }

    private int openCamera() {
        if (isCamOpen == 1) {
            releaseCamera();
        }

        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        //setCameraDisplayOrientation(this.activity,0,mCamera);
        if (mCamera == null) {
            return -1;
        }
        params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size cs = sizes.get(0);
        params.setPreviewSize(cs.width, cs.height);
        params.setPictureSize(cs.width, cs.height);
        params.set("orientation", "portrait");
        params.setRotation(surfaceRotation);
       //
       // params.setPreviewSize(previewWidth, previewHeight);

        try {
            mCamera.setParameters(params);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
        try {
            mCamera.setPreviewDisplay(sHolder);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
            return -1;
        }
        isCamOpen = 1;
        return isCamOpen;
    }
    public int isCamOpen() {
        return isCamOpen;
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
        isCamOpen = 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        sHolder = holder;

        isCamOpen = openCamera();
        mCamera.setDisplayOrientation(getCamRotation());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();

    }

    /**
     * Called from PreviewSurfaceView to set touch focus.
     *
     * @param - Rect - new area for auto focus
     */
    public void doTouchFocus(final Rect tfocusRect) {
        Log.i(TAG, "TouchFocus");
        try {
            final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters para = mCamera.getParameters();
            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);

            mCamera.setParameters(para);

            mCamera.autoFocus(myAutoFocusCallback);
           // rotation = camPreview.getCamRotation();
           // takePictureAndOcr.onFocusClick(surfaceRotation);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Unable to autofocus");
        }

    }
    /**
     * Set display orientation for the surface
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    public  void setCameraDisplayOrientation(Activity activity,
                                             int cameraId, android.hardware.Camera camera) {
        int surfaceRotation;
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        surfaceRotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        //surfaceRotation = rotation;
        int degrees = 0;
        switch (surfaceRotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        //mCamera.setDisplayOrientation(result);
        //surfaceRotation = result;
        setCamRotation(result);
        // This global var is responsable for telling the OCR mechanism how to read the image

    }
    public int getCamRotation(){
            return surfaceRotation;
    }
    public void setCamRotation(int surfaceRotation_){

        this.surfaceRotation = surfaceRotation_;
    }
    /**
     * AutoFocus callback
     */
    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            if (arg0){
                mCamera.cancelAutoFocus();
            }
        }
    };

}
