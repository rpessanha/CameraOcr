package pessanha.com.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pessanha.com.myfirstapp.utils.WsResults;


public class ResultsActivity extends Activity {
    private String TAG = "Euromilhoes";
    private static String drawType="EuroMillions";
    private static String lastNumberOfDraws="5";
    private ListView listview;
    private WsResults wsResults;
    private ArrayList<EuroResult>  listaEuroResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lastresults);
        // Referencia a istview
        listview = (ListView) findViewById(R.id.listView);
        // Carrega arraylist com Euroresults objects
        listaEuroResults = new ArrayList<EuroResult>();
        // Classe carrega resultado WebService
        wsResults=new WsResults(drawType,lastNumberOfDraws);
        //AsyncCall para carregar resultados
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }


    /**
     *  Lists results by adding them to the adapter to fill the listvew
     *  The adapter is filled by the values returned from the weResults method getList of type ArrayList<EuroResult>
     */
    public void listResults(){
        listview.setAdapter(addItemToAdapter(wsResults.getList()));
    }

    /**
     *
     * @param euroResults Arraylist of euroresult
     * @return adapter
     */
    private SimpleAdapter addItemToAdapter(ArrayList<EuroResult> euroResults)
    {
        // Each row in the list stores Sorteio, key and icon
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for(int i=0;i<euroResults.size();i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt","Sorteio de "+((EuroResult)euroResults.get(i)).getDateOnly());
            hm.put("cur",((EuroResult)euroResults.get(i)).getEuroKey());
            hm.put("flag", Integer.toString(R.drawable.starshapeflaticon) );
            //hm.put("flag", Integer.toString(flags[i]) );
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {"flag","txt","cur" };

        // Ids of views in listview_layout
        int[] to = {R.id.flag, R.id.txt,R.id.cur};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);
        return adapter;
    }

    /**
     *  Called by the AsyncCallws to show the numbers in AlertDialog
     */
    private void handleClickListview(){
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {



                //mTimeText.setText("Time: " + dateFormat.format(date));
                showAlertDialog(ResultsActivity.this, "Sorteio do dia "+listaEuroResults.get(position).getDateOnly(),
                        listaEuroResults.get(position).getFirstNumber()+" - "+
                                listaEuroResults.get(position).getSecondNumber()+" - "+
                                listaEuroResults.get(position).getThirdNumber()+" - "+
                                listaEuroResults.get(position).getFourthNumber()+" - "+
                                listaEuroResults.get(position).getFifthNumber()+"   ("+
                                listaEuroResults.get(position).getFirstLuckyNumber()+" - "+
                                listaEuroResults.get(position).getSecondLuckyNumber()+")", true);
                // list.remove(item);
                // adapter.notifyDataSetChanged();
                //view.setAlpha(1);


            }

        });

    }
    /**
     *
     * @param context   The base class
     * @param title Title box in alert dialog
     * @param message
     * @param status
     */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        // AlertDialog Builder
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(R.drawable.starshapeflaticon);

        // Setting OK Button and clickListener
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Let it empty. Just to close this one
            }
        });

        // Showing Alert Message
        alertDialog.show();

        // Get message box from alertdialog
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

        // Change font
        textView.setTextSize(22);
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        private ProgressDialog pdia;
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
           // getResults(drawType, lastNumberOfDraws);
            wsResults.getResults();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            //tv.setText(fahren + "° F");
            listaEuroResults = wsResults.getEuroResultsList();
            listResults();
            handleClickListview();
            pdia.dismiss();

        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            pdia = new ProgressDialog(ResultsActivity.this);
            pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdia.setMessage("A carregar...");
            pdia.setIndeterminate(true);
            pdia.setCanceledOnTouchOutside(false);
            pdia.show();
            //tv.setText("Calculating...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

}
