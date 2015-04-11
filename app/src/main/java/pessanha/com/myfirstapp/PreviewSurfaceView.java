package pessanha.com.myfirstapp;

/**
 * Created by pessanha on 10/03/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import pessanha.com.myfirstapp.utils.DrawingView;


/**
 * SurfaceView to show LenxCameraPreview2 feed
 */
public class PreviewSurfaceView extends SurfaceView {

    private CameraPreview camPreview;
    private boolean listenerSet = false;
    public Paint paint;
    private DrawingView drawingView;
    private boolean drawingViewSet = false;
    private TakePictureAndOcr takePictureAndOcr;
    private int rotation;
  //  private ImageButton takePicture;
    public PreviewSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
       /* takePicture = (ImageButton)findViewById(R.id.buttonClick);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePictureAndOcr.onFocusClick(camPreview.getCamRotation());
            }
        });*/
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!listenerSet) {
            return false;
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float x = event.getX();
            float y = event.getY();

            Rect touchRect = new Rect(
                    (int)(x - 100),
                    (int)(y - 100),
                    (int)(x + 100),
                    (int)(y + 100));

            final Rect targetFocusRect = new Rect(
                    touchRect.left * 2000/this.getWidth() - 1000,
                    touchRect.top * 2000/this.getHeight() - 1000,
                    touchRect.right * 2000/this.getWidth() - 1000,
                    touchRect.bottom * 2000/this.getHeight() - 1000);

            camPreview.doTouchFocus(targetFocusRect);
            if (drawingViewSet) {
                drawingView.setHaveTouch(true, touchRect);
                drawingView.invalidate();

                // Remove the square after some time
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        drawingView.setHaveTouch(false, new Rect(0, 0, 0, 0));
                        drawingView.invalidate();
                        //takePictureAndOcr.onFocusClick(camPreview.getCamRotation());
                    }
                }, 1000);

                /*1Start take pict process here */

            }

        }
        return false;
    }
    /**
     * set CameraPreview instance for touch focus.
     * @param camPreview - CameraPreview
     */
    public void setListener(CameraPreview camPreview) {
        this.camPreview = camPreview;

        listenerSet = true;
    }

    /**
     * set DrawingView instance for touch focus indication.
     * @param  - DrawingView
     */
    public void setDrawingView(DrawingView dView) {
        drawingView = dView;
        drawingViewSet = true;
    }
    public void SetOnClickListener(TakePictureAndOcr takePictureAndOcr){
        this.takePictureAndOcr = takePictureAndOcr;

    }
    public interface TakePictureAndOcr {
        public void onFocusClick(int rotation);
    }
}