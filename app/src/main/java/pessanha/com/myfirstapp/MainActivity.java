package pessanha.com.myfirstapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import android.support.v7.app.ActionBarActivity;


public class MainActivity extends Activity {
    private final static String TAG = "OCR Camera";
    protected static final int MEDIA_TYPE_IMAGE = 0;
    private Camera camera;
    private int cameraId = 0;
    Preview preview;
    ImageButton buttonClick;
    SeekBar seekbar;
    private int surfaceRotation; // save rotation from display
    private int pictRotation; // get rotation needed to get pict saved
    private Context context;
    private static final String TESSBASE_PATH = "/storage/external_sd/Pictures/";
    private static final String DEFAULT_LANGUAGE = "por";
    private static final String EXPECTED_FILE = TESSBASE_PATH + "tessdata/" + DEFAULT_LANGUAGE
            + ".traineddata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title from windows
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set the content the activity_main.xml
        setContentView(R.layout.activity_main);
        // Define the screen orientation, prevents rotating
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Save context with environment data from activity
        context=this;
        // Instance the preview surfaceview
        preview = new Preview(this);
        // Add preview (surfaceview) to framelayout preview com index 0
        ((FrameLayout) findViewById(R.id.preview)).addView(preview, 0);
        // Instance Button for camera click
        buttonClick = (ImageButton) findViewById(R.id.buttonClick);
        // Still have to find out what to do with this seekbar (maybe zoom or focus)
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               /// camera.
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // Bring button to front
        buttonClick.bringToFront();
        // Clicking button then picture is taken
        buttonClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                preview.camera.takePicture(shutterCallback, null,
                        jpegCallback);
            }
        });

    }
    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Its essecial to remove the layout so we create it again
        ((FrameLayout) findViewById(R.id.preview)).removeView(preview);

    }
    // Nothing planed for this one, maybe a dancing button :)
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for jpeg picture */
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            try {
                // Call next method for creating a file type image
                File picFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (picFile == null) {
                    Log.e(TAG, "Couldn't create media file; check storage permissions?");
                    return;
                }
                FileOutputStream fos = new FileOutputStream(picFile);
                fos.write(data);
                fos.flush();
                fos.close();
                // Lets tell android to scan for newly taken images so that thei apear in the galery
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.parse("file://" + picFile));
                sendBroadcast(mediaScanIntent);
                // Call ocr JNI methods to recognized chars in image
                ocr(picFile.getAbsolutePath(),"/sdcard/","eng");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Method to create a file in pictures/OCR for use in PictureCallBack
     *
     * @param type
     * @return File
     */
    private File getOutputMediaFile(int type) {
        // Create new File object
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "OCR");
        // Check if it exists, if not create it
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }
        // Create timestamp string to attach to filename
        String timeStamp =
                new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
        // Return File from absolute path to image
        if (type == MEDIA_TYPE_IMAGE) {
            return new File(dir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
    }



    /**
     * Get the rotation of the last image added.
     * @param context Environment variable in context = this (activity)
     * @param selectedImage
     * @return
     */
    private static int getRotation(Context context,Uri selectedImage) {
        int rotation =0;
        ContentResolver content = context.getContentResolver();


        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },null, null,"date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() !=0 ) {
            while(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }

    /**
     * The one and only method for OCR of picture taken
     * @param IMAGE_PATH Path of image in string
     * @param DATA_PATH
     * @param LANG
     */
    protected void ocr(String IMAGE_PATH,String DATA_PATH,String LANG) {
        // Instantiate a bitmap options variable
        BitmapFactory.Options options = new BitmapFactory.Options();
        // return 1/2 of the image in with and height
        options.inSampleSize = 2;
        // get bitmap with options rezied to 1/2
        Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_PATH, options);
        try {
            // Just learned that there is a bug in android a Exif cannot get orientation
            ExifInterface exif = new ExifInterface(IMAGE_PATH);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.v(TAG, "Orient: " + exifOrientation);
            // Trying to rotate by ourselves
            // Get rotation (surfaceRotation) from setCameraDisplayOrientation in surfaceview class
            pictRotation=surfaceRotation;
            Toast.makeText(context,Integer.toString(surfaceRotation),
                    Toast.LENGTH_LONG).show();

            /* Dont need to change now because made it surface*/
           /*switch (surfaceRotation) {
                case 90:
                    pictRotation = 90;
                    break;
                case 180:
                    pictRotation = 180;
                    break;
                case 270:
                    pictRotation = 270;
                    break;
            }*/

            Log.v(TAG, "Rotation: " + pictRotation);
            // If rotaion different from 0, then matrix rotation the bitmap
            if (pictRotation != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(0);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                // tesseract requires ARGB_8888
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

        } catch (IOException e) {
            Log.e(TAG, "Rotate or coversion failed: " + e.toString());
        }

        // Do the magic with JNI Tesseract
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        Log.v(TAG, "OCR Result: " + recognizedText);

        // Lets do some cleaning
        if (LANG.equalsIgnoreCase("por")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
       /* if (recognizedText.length() != 0) {
            ((TextView) findViewById(R.id.field)).setText(recognizedText.trim());
        }*/
        Toast.makeText(context,recognizedText.toString() , Toast.LENGTH_LONG).show();

    }

    /**
     * Class Surfaceview with interface surfaceholder
     *
     * Surfaceholder interface is tipically available through surfaceview
     */
    public class Preview  extends SurfaceView implements SurfaceHolder.Callback { // Callbacks react to something that happens
        private static final String TAG = "Preview";

        SurfaceHolder mHolder;
        public Camera camera;

        Preview(Context context) {
            super(context);
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder(); // Allow access to suraceview
            mHolder.addCallback(this); // Add callbacks to this surfaceview
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public  void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            camera = Camera.open();
            setCameraDisplayOrientation(MainActivity.this,0,camera);
            try {
                camera.setPreviewDisplay(holder);
                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    public void onPreviewFrame(byte[] data, Camera arg1) {
                        // refresh the view
                        Preview.this.invalidate();
                    }
                });
            } catch (IOException e) {
                camera.release();
                camera= null;
                e.printStackTrace();
            }
        }

        /**
         * Surface will be destroyed when we return, so stop the preview.
         * Because the CameraDevice object is not a shared resource, it's very
         * important to release it when the activity is paused.
         *
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            this.getHolder().removeCallback(this);
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        /**
         * Now that the size is known, set up the camera parameters and begin
         * the preview. Runs imediatly after the create
         *
         * @param holder
         * @param format
         * @param w
         * @param h
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Define camera parameters
            Camera.Parameters parameters = camera.getParameters();
            /* Check supported size*/
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            parameters.setPictureFormat(PixelFormat.JPEG);
            Camera.Size cs = sizes.get(0);
            parameters.setPreviewSize(cs.width, cs.height);
            parameters.setPictureSize(cs.width, cs.height);
            //parameters.setRotation(Parameters.);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setFocusMode("auto");
            parameters.set("orientation", "portrait");
            // If i rotate here, i dont need to rotate in ocr.
            parameters.setRotation(surfaceRotation);
            camera.setParameters(parameters);
            camera.startPreview();
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
            android.hardware.Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            surfaceRotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
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
            camera.setDisplayOrientation(result);
            // This global var is responsable for telling the OCR mechanism how to read the image
            surfaceRotation = result;
        }

    }
}
