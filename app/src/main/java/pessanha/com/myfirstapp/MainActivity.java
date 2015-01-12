package pessanha.com.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//import android.support.v7.app.ActionBarActivity;


public class MainActivity extends Activity {
    private final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;
    Preview preview;
    ImageButton buttonClick;
    private Context context;
    private static final String TESSBASE_PATH = "/storage/external_sd/Pictures/";
    private static final String DEFAULT_LANGUAGE = "eng";
    private static final String EXPECTED_FILE = TESSBASE_PATH + "tessdata/" + DEFAULT_LANGUAGE
            + ".traineddata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        context=this;
        preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview,0);
        buttonClick = (ImageButton) findViewById(R.id.buttonClick);
        buttonClick.bringToFront();
        buttonClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                preview.camera.takePicture(shutterCallback, rawCallback,
                        jpegCallback);
            }
        });
        // do we have a camera?
      /*  if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
                camera.startPreview();
            }
        }*/
    }
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d("Camera", "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("Camera", "onPictureTaken - raw");
        }
    };
    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "tessdata");
    }
    /** Handles data for jpeg picture */
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            try {
                File pictureFileDir = getDir();

                if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

                    Log.d("Camera", "Can't create directory to save image.");
                    Toast.makeText(context, "Can't create directory to save image.",
                            Toast.LENGTH_LONG).show();
                    return;

                }
                String file2save = (String.format(pictureFileDir+"%d.jpg", System.currentTimeMillis()));
                outStream = new FileOutputStream(file2save);
                outStream.write(data);
                outStream.close();
                Toast.makeText(context, "File saved at : "+String.format(
                                "/sdcard/%d.jpg", System.currentTimeMillis()),
                        Toast.LENGTH_LONG).show();
                ocr(file2save,"/sdcard/","eng");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    public void onClick(View view) {

    }
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }
    /* A view é a view que foi clicada*/
    /*public void sendMessage(View view)
    {
        /*  The constructor used here takes two parameters:
            A Context as its first parameter (this is used because the Activity class is a subclass of Context)
            The Class of the app component to which the system should deliver the Intent (in this case, the activity that should be started)*/
    /*    Intent intent = new Intent(this,DisplayMessageActivity.class);
        EditText editText = (EditText)findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }*/
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //essencial remover a view para que possamos chamar novamente
        ((FrameLayout) findViewById(R.id.preview)).removeView(preview);
       /* if (camera != null) {
            camera.release();
            camera = null;
        }*/
    }
    protected void ocr(String IMAGE_PATH,String DATA_PATH,String LANG) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_PATH, options);

        try {
            ExifInterface exif = new ExifInterface(IMAGE_PATH);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Log.v("OCR", "Orient: " + exifOrientation);

            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v("OCR", "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                // tesseract req. ARGB_8888
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

        } catch (IOException e) {
            Log.e("OCR", "Rotate or coversion failed: " + e.toString());
        }

      /*  ImageView iv = (ImageView) findViewById(R.id.image);
        iv.setImageBitmap(bitmap);
        iv.setVisibility(View.VISIBLE);

        Log.v(LOG_TAG, "Before baseApi");
*/
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        Log.v("OCR", "OCR Result: " + recognizedText);

        // clean up and show
       /* if (LANG.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
        if (recognizedText.length() != 0) {
            ((TextView) findViewById(R.id.field)).setText(recognizedText.trim());
        }*/
        Toast.makeText(context,recognizedText.toString() , Toast.LENGTH_LONG).show();
       // Toast.makeText(context, "File saved at : "+String.format("/sdcard/%d.jpg", System.currentTimeMillis()),
        //        Toast.LENGTH_LONG).show();
    }
}
