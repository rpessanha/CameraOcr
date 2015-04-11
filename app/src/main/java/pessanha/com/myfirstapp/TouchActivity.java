package pessanha.com.myfirstapp;

/**
 * Created by pessanha on 10/03/2015.
 */
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pessanha.com.myfirstapp.utils.DrawingView;
import pessanha.com.myfirstapp.utils.Ocr;


public class TouchActivity extends Activity implements PreviewSurfaceView.TakePictureAndOcr {

    private PreviewSurfaceView camView;
    private CameraPreview cameraPreview;
    private DrawingView drawingView;
    private ImageButton buttonClick;
    private int previewWidth = 720;
    private int previewHeight = 1280;
    private int rotation,surfaceRotation;
    private Camera camera;
    private String TAG = "TouchActivity";
    protected static final int MEDIA_TYPE_IMAGE = 0;
    public static ArrayList<EuroResult> euroresults_static;
    private Ocr ocr;
    ProgressDialog pdia;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title from windows
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);*/
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_touch);
        // Define the screen orientation, prevents rotating
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        buttonClick = (ImageButton) findViewById(R.id.buttonClick);
        camView = (PreviewSurfaceView) findViewById(R.id.preview_surface);


        SurfaceHolder camHolder = camView.getHolder();

        cameraPreview = new CameraPreview(previewWidth, previewHeight);
      //  cameraPreview.SetOnClickListener(this);
        cameraPreview.setCameraDisplayOrientation(TouchActivity.this, 0, cameraPreview.mCamera);
        camHolder.addCallback(cameraPreview);
        camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        camView.setListener(cameraPreview);
        camView.SetOnClickListener(this);
        //cameraPreview.changeExposureComp(-currentAlphaAngle);
        drawingView = (DrawingView) findViewById(R.id.drawing_surface);
        camView.setDrawingView(drawingView);

        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   /* Toast.makeText(StartActivity.this,
                            "Click!", Toast.LENGTH_SHORT).show();*/

                pdia = new ProgressDialog(TouchActivity.this);
                pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pdia.setMessage("A reconhecer números...");
                pdia.setIndeterminate(true);
                pdia.setCanceledOnTouchOutside(false);
                pdia.show();
                cameraPreview.mCamera.takePicture(shutterCallback, null,
                        jpegCallback);
            }
        });


    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.touch, menu);
        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusClick(int rotation) {

        cameraPreview.mCamera.takePicture(shutterCallback, null,
                jpegCallback);
    }
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //Log.d(TAG, "onShutter'd");
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
// new
                Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                picture = Bitmap.createBitmap(picture, 50, 410, 600, 540);
                picture.compress(Bitmap.CompressFormat.JPEG, 100, fos);
// new
                fos.write(data);
                fos.flush();
                fos.close();
                // Lets tell android to scan for newly taken images so that thei apear in the galery
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.parse("file://" + picFile));
                sendBroadcast(mediaScanIntent);
                // Call ocr JNI methods to recognized chars in image

                ocr = new Ocr(picFile.getAbsolutePath(),"/sdcard/","por",cameraPreview.getCamRotation());
                //ocr(picFile.getAbsolutePath(),"/sdcard/","eng");
                //Toast.makeText(TouchActivity.this, ocr.getTextFromImage(), Toast.LENGTH_LONG).show();
                //fechar este intent
                euroresults_static = ocr.getEuroresultsFromImage();
                pdia.dismiss();
                if(euroresults_static.size()>0)
                {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("result", euroresults_static);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtras(mBundle);
                    setResult(1, returnIntent);
                    cameraPreview.releaseCamera();
                    TouchActivity.this.finish();
                }
                else
                {
                    new AlertDialog.Builder(TouchActivity.this)
                            .setTitle("Imagem não focada")
                            .setMessage("Não consegui encontrar os números. Tentar novamente?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cameraPreview.mCamera.startPreview();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    cameraPreview.releaseCamera();
                                    TouchActivity.this.finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
               // Bundle b = new Bundle();


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
}