package pessanha.com.myfirstapp.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pessanha.com.myfirstapp.EuroResult;

/**
 * Created by pessanha on 14/03/2015.
 */
public class Ocr  implements Serializable {
    private static final String TESSBASE_PATH = "/storage/external_sd/Pictures/";
    private static final String DEFAULT_LANGUAGE = "por";
    private static final String EXPECTED_FILE = TESSBASE_PATH + "tessdata/" + DEFAULT_LANGUAGE;
    private int pictRotation; // get rotation needed to get pict saved
    private String imagePath;
    private String dataPath;
    private String lang;
    private int surfaceRotation;
    private String ocrText="";
    private ArrayList<EuroResult> euroresults;
    public Ocr(String IMAGE_PATH,String DATA_PATH,String LANG, int rotation){
        imagePath = IMAGE_PATH;
        dataPath = DATA_PATH;
        lang = LANG;
        this.surfaceRotation = rotation;
        euroresults= new ArrayList<EuroResult>();

    }
    public void PictToText(){
        // Instantiate a bitmap options variable
        BitmapFactory.Options options = new BitmapFactory.Options();
        // return 1/2 of the image in with and height
        options.inSampleSize = 2;
        // get bitmap with options rezied to 1/2
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        try {
            // Just learned that there is a bug in android a Exif cannot get orientation
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            // Trying to rotate by ourselves
            // Get rotation (surfaceRotation) from setCameraDisplayOrientation in surfaceview class
            pictRotation=surfaceRotation;

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
            //Log.e(TAG, "Rotate or coversion failed: " + e.toString());
        }

        // Do the magic with JNI Tesseract
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        //Log.v(TAG, "OCR Result: " + recognizedText);

        // Lets do some cleaning
        if (lang.equalsIgnoreCase("por")) {
            //recognizedText = recognizedText.replaceAll("[^0-9]", " ");
           // ([0-9]+\ +)+(\+)
            recognizedText = recognizedText.replaceAll("[^\r\n- +0-9]+", " ");
            ocrText = recognizedText;
            loadNumbers(ocrText);
        }
       /* if (recognizedText.length() != 0) {
            ((TextView) findViewById(R.id.field)).setText(recognizedText.trim());
        }*/
        //Toast.makeText(context,recognizedText.toString() , Toast.LENGTH_LONG).show();
        /*Intent intent=new Intent();
        intent.putExtra("MESSAGE",recognizedText.toString());
        setResult(1,intent);
        finish();//finishing activity*/
    }
    public String getTextFromImage(){
        PictToText();
        return ocrText;
    }
    public ArrayList<EuroResult> getEuroresultsFromImage(){
        PictToText();
        return euroresults;
    }
    public void loadNumbers(String text){


        String[] lines = text.split("\n");
        // Two by two lines (first one for numbers, second one for stars
        for(int i = 0; i<lines.length; i++){
           // Log.e("e", pCount + " " + split[i]);
            String[] nums = new String[4];
            String[] stars = new String[1];
            // first line has mora than 4 numbers? if so, then results are great, else discard line
            if(checkLineSize(lines[i])>=4){
                nums = getNumbersFromString(lines[i],0);
                if(i+1<lines.length)
                    stars = getNumbersFromString(lines[i+1],1);
                if(nums.length>4 && stars.length>1) {
                    euroresults.add(new EuroResult("", "", "", "", Integer.parseInt(nums[0]), Integer.parseInt(nums[1]),
                            Integer.parseInt(nums[2]), Integer.parseInt(nums[3]), Integer.parseInt(nums[4]),
                            Integer.parseInt(stars[0]), Integer.parseInt(stars[1])));
                }
            }
        }
    }
    // 0 for numbers, 1 for stars
    public String[] getNumbersFromString(String numbers,int type){
        int contador=0;
        String[] str = numbers.split(" ");
        String[] numbers_ =new String[str.length];
        int j=0;
        if(type==0){
            j=5;

        }else
        {
            j=2;

        }

        for(int i = 0; i<numbers_.length; i++){
            if(isInteger(str[i])){
                numbers_[contador]=str[i];
                contador ++;
            }else if (str[i].contains(" ") || (str[i].contains(""))){
               // numbers_[contador]=str[i];
            }
            else{
                numbers_[contador]="0";
                contador ++;
            }

        }
        if(type==0){
            if(contador<5 && numbers_.length>4){
                for(int x=contador-1;x<j;x++){
                  //  if(numbers_[x].trim()==""){
                        numbers_[x]="0";
                    //}
                }
            }
        }else
        {
            if(contador<2 && numbers_.length>1){
                for(int x=contador;x<j;x++){
                    //if(numbers_[x].trim()==""){
                        numbers_[x]="0";
                  //  }
                }
            }
        }
        return numbers_;
    }
    public boolean isInteger(String str){

        try{
            int num = Integer.parseInt(str);
            return true;
            // is an integer!
        } catch (NumberFormatException e) {
            // not an integer!
            return false;
        }

    }
    public int checkLineSize(String line){
        String[] numbers = line.split(" ");
        int countNumbers=0;
        for(int i = 0; i<numbers.length; i++){
            if(isInteger(numbers[i]))
            {
                countNumbers++;
            }
        }
        return countNumbers;
    }
}
