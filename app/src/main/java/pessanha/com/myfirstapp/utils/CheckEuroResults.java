package pessanha.com.myfirstapp.utils;

import java.util.ArrayList;

import pessanha.com.myfirstapp.EuroResult;

/**
 * Created by pessanha on 21/02/2015.
 */
public class CheckEuroResults {
    private ArrayList<EuroResult> listaResultsToCheck;
    private EuroResult resultToCheck;


    public CheckEuroResults(ArrayList<EuroResult> listaResultsToCheck, EuroResult euroresult) {
        this.listaResultsToCheck = listaResultsToCheck;
        resultToCheck =euroresult;


    }
    public ArrayList<EuroResult> CheckNumbers()
    {
        int stars=0;
        int numbers=0;
        // Para cada euroresult jogado
        for (int i =0;i<listaResultsToCheck.size();i++){
            // Para cada numero do euroresult jogado
            for(int j=0;j<5;j++){
                for (int k=0;k<5;k++){
                    if(listaResultsToCheck.get(i).getArrayNumbers()[j] == resultToCheck.getArrayNumbers()[k])
                    {
                        numbers++;
                    }

                }
            }
            for(int j=4;j<listaResultsToCheck.get(i).getArrayNumbers().length;j++){
                for (int k=4;k<resultToCheck.getArrayNumbers().length;k++){
                    if(listaResultsToCheck.get(i).getArrayNumbers()[j] == resultToCheck.getArrayNumbers()[k])
                    {
                        stars++;
                    }

                }
            }
            listaResultsToCheck.get(i).setStars(stars);
            listaResultsToCheck.get(i).setNumbers(stars);
        }
        return listaResultsToCheck;
    }
}
