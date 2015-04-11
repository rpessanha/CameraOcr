package pessanha.com.myfirstapp;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pessanha on 10/02/2015.
 */
public class EuroResult implements Serializable {
    private String drawDate;
    private String drawNumber;
    private String message;
    private String topPrize;
    private String dateOnly;
    private int firstNumber;
    private int secondNumber;
    private int thirdNumber;
    private int fourthNumber;
    private int fifthNumber;
    private int firstLuckyNumber;
    private int secondLuckyNumber;
    private int stars;
    private int numbers;

    public EuroResult(String drawDate_, String drawNumber_, String message_,
                 String topPrize_, int firstNumber_, int secondNumber_, int thirdNumber_, int fourthNumber_, int fifthNumber_,
                 int firstLuckyNumber_, int secondLuckyNumber_){
        /*if (drawDate_ == null) {
            drawDate = "";
            drawNumber = drawNumber_;
            message = message_;
            topPrize = topPrize_;
        }*/
        drawDate=drawDate_;
        drawNumber=drawNumber_;
        message=message_;
        topPrize=topPrize_;
        dateOnly="";
        firstNumber=firstNumber_;
        secondNumber=secondNumber_;
        thirdNumber=thirdNumber_;
        fourthNumber=fourthNumber_;
        fifthNumber=fifthNumber_;
        firstLuckyNumber=firstLuckyNumber_;
        secondLuckyNumber=secondLuckyNumber_;
        stars=0;
        numbers=0;
    }
    public EuroResult(SoapObject soapObject){
        String dtStart = soapObject.getProperty(1).toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SoapObject subSoapObject = (SoapObject)soapObject.getProperty(9);

        drawDate= soapObject.getProperty(1).toString();
        drawNumber= soapObject.getProperty(2).toString();
        message= soapObject.getProperty(3).toString();
        topPrize=soapObject.getProperty(6).toString();
        dateOnly=date.toLocaleString().substring(0,date.toLocaleString().indexOf(" "));
        firstNumber = Integer.parseInt(((SoapObject) subSoapObject.getProperty(0)).getProperty(0).toString());
        secondNumber = Integer.parseInt(((SoapObject) subSoapObject.getProperty(1)).getProperty(0).toString());
        thirdNumber = Integer.parseInt(((SoapObject) subSoapObject.getProperty(2)).getProperty(0).toString());
        fourthNumber = Integer.parseInt(((SoapObject) subSoapObject.getProperty(3)).getProperty(0).toString());
        fifthNumber = Integer.parseInt(((SoapObject) subSoapObject.getProperty(4)).getProperty(0).toString());
        firstLuckyNumber = Integer.parseInt(((SoapObject) subSoapObject.getProperty(5)).getProperty(0).toString());
        secondLuckyNumber = Integer.parseInt(((SoapObject) subSoapObject.getProperty(6)).getProperty(0).toString());
        stars=0;
        numbers=0;

    }

    public String getDrawdate(){
        return drawDate;
    }
    public String getDrawNumber(){
        return drawNumber;
    }
    public String getMessage(){
        return message;
    }
    public String getTopPrize(){
        return topPrize;
    }
    public String getDateOnly(){
        return dateOnly;
    }
    public int getFirstNumber(){
        return firstNumber;
    }
    public int getSecondNumber(){
        return secondNumber;
    }
    public int getThirdNumber(){
        return thirdNumber;
    }
    public int getFourthNumber(){
        return fourthNumber;
    }
    public int getFifthNumber(){
        return fifthNumber;
    }
    public int getFirstLuckyNumber(){
        return firstLuckyNumber;
    }
    public int getSecondLuckyNumber(){
        return secondLuckyNumber;
    }
    public int getStars(){
        return stars;
    }
    public int getNumbers()
    {
        return numbers;
    }
    public void setFirstNumber(int firstNumber_){
         firstNumber=firstNumber_;
    }
    public void setSecondNumber(int secondNumber_){
         secondNumber=secondNumber_;
    }
    public void setThirdNumber(int thirdNumber_){
         thirdNumber=thirdNumber_;
    }
    public void setFourthNumber(int fourthNumber_){
         fourthNumber=fourthNumber_;
    }
    public void setFifthNumber(int fifthNumber_){
         fifthNumber=fifthNumber_;
    }
    public void setFirstLuckyNumber(int firstLuckyNumber_){
         firstLuckyNumber=firstLuckyNumber_;
    }
    public void setSecondLuckyNumber(int secondLuckyNumber_){
         secondLuckyNumber=secondLuckyNumber_;
    }
    public void setStars(int stars_){
        stars=stars_;
    }
    public void setNumbers(int numbers_){
        numbers=numbers_;
    }
    public String getEuroKey()
    {
        String finalResult="";
        String firstNumber_str=Integer.toString(firstNumber);
        String secondNumber_str= Integer.toString(secondNumber);
        String thirdNumber_str=Integer.toString(thirdNumber);
        String fourthNumber_str=Integer.toString(fourthNumber);
        String fifthNumber_str=Integer.toString(fifthNumber);
        String firstLuckyNumber_str=Integer.toString(firstLuckyNumber);
        String secondLuckyNumber_str=Integer.toString(secondLuckyNumber);
        if(firstNumber_str.trim().equals("0") || firstNumber_str.length()>2 )
            firstNumber_str="?";
        if(secondNumber_str.trim().equals("0") || secondNumber_str.length()>2 )
            secondNumber_str="?";
        if(thirdNumber_str.trim().equals("0") || thirdNumber_str.length()>2 )
            thirdNumber_str="?";
        if(fourthNumber_str.trim().equals("0") || fourthNumber_str.length()>2 )
            fourthNumber_str="?";
        if(fifthNumber_str.trim().equals("0") || fifthNumber_str.length()>2 )
            fifthNumber_str="?";
        if(firstLuckyNumber_str.trim().equals("0") || firstLuckyNumber_str.length()>2 )
            firstLuckyNumber_str="?";
        if(secondLuckyNumber_str.trim().equals("0") || secondLuckyNumber_str.length() >2 )
            secondLuckyNumber_str="?";
        finalResult = firstNumber_str+" - "+
                secondNumber_str+" - "+
                thirdNumber_str+" - "+
                fourthNumber_str+" - "+
                fifthNumber_str+" - ("+
                firstLuckyNumber_str+" - "+
                secondLuckyNumber_str+")";
        return finalResult;

    }
    public int[] getArrayNumbers()
    {
        int[] numbers = new int[7];
        //for(int i =0;i<numbers.length;i++){
        numbers[0] = this.getFirstNumber();
        numbers[1] = this.getSecondNumber();
        numbers[2] = this.getThirdNumber();
        numbers[3] = this.getFourthNumber();
        numbers[4] = this.getFifthNumber();
        numbers[5] = this.getFirstLuckyNumber();
        numbers[6] = this.getSecondLuckyNumber();
        return numbers;
        //}
    }
}
