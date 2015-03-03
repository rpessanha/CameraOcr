package pessanha.com.myfirstapp;



import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import pessanha.com.myfirstapp.utils.MobileInternetConnectionDetector;
import pessanha.com.myfirstapp.utils.WIFIInternetConnectionDetector;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *

 */
public class Start extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;
    MobileInternetConnectionDetector mobileInternet;
    WIFIInternetConnectionDetector wifiInternet;
    ImageButton btnOcr, btnResults,btnCheckResults;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
         mobileInternet = new MobileInternetConnectionDetector(getApplicationContext());
         wifiInternet= new WIFIInternetConnectionDetector(getApplicationContext());
      //  btnOcr = (ImageButton)findViewById(R.id.imageButton3);
        btnResults = (ImageButton)findViewById(R.id.imageButton);
        btnCheckResults = (ImageButton)findViewById(R.id.imageButton2);
        if (checkConnectivity()){
            /*btnOcr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(buttonClick);

                    Intent intent = new Intent
                            (Start.this, MainActivity.class);
                    Start.this.startActivity(intent);
                }
            });*/
            btnResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Toast.makeText(Start.this,
                            "Click!", Toast.LENGTH_SHORT).show();*/
                    Intent intent = new Intent
                            (Start.this, ResultsActivity.class);
                    Start.this.startActivity(intent);
                }
            });
            btnCheckResults.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Toast.makeText(Start.this,
                            "Click!", Toast.LENGTH_SHORT).show();*/
                    Intent intent = new Intent
                            (Start.this, CheckResultsActivity.class);
                    Start.this.startActivity(intent);
                }
            });
        }
        else{
            showAlertDialog(Start.this, "No Internet Connection",
                    "Your device doesn't have mobile internet", false);
        }




    }
    public boolean checkConnectivity(){
        if(mobileInternet.checkMobileInternetConn() || wifiInternet.checkMobileInternetConn()){
            // Internet Connection exists
           return true;
        } else {
            return false;

        }
    }
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }
    @Override
    protected void onDestroy() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */



}
