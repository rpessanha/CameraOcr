package pessanha.com.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pessanha.com.myfirstapp.utils.WsResults;


public class CheckResultsActivity extends Activity {
    final Context context = CheckResultsActivity.this;
    private ImageButton btnresults, btnverify;
    private ArrayList<EuroResult> listaResultsToCheck;
    private EditText txt1, txt2, txt3, txt4, txt5, txt6, txt7;
    private ListView lista;
    private WsResults wsResults;
    private static String drawType="EuroMillions";
    private static String lastNumberOfDraws="5";
    private String TAG = "PGGURU";
    private ArrayList<EuroResult>  listaEuroResults;
    private EuroResult euroresult;
    AlertDialog.Builder alertDialogBuilder;
    //private View otherview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.check_results_activity);
        btnresults = (ImageButton) findViewById(R.id.btnPrompt);
        btnverify = (ImageButton) findViewById(R.id.imageButton4);
        //otherview = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.results_prompt, null, false);
        listaResultsToCheck = new ArrayList<EuroResult>();
        lista=(ListView)findViewById(R.id.listView2);
        registerForContextMenu(lista);
        // get prompts.xml view
        btnresults.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.results_prompt, null);
                promptsView.setLayoutParams(new WindowManager.LayoutParams(500, 300));
                txt1 = (EditText)promptsView.findViewById(R.id.txt1);
                txt2 = (EditText)promptsView.findViewById(R.id.txt2);
                txt3 = (EditText)promptsView.findViewById(R.id.txt3);
                txt4 = (EditText)promptsView.findViewById(R.id.txt4);
                txt5 = (EditText)promptsView.findViewById(R.id.txt5);
                txt6 = (EditText)promptsView.findViewById(R.id.txt6);
                txt7 = (EditText)promptsView.findViewById(R.id.txt7);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Test return result
                                        if (txt1.getText().toString().matches("") ||
                                                txt2.getText().toString().matches("") ||
                                                txt3.getText().toString().matches("") ||
                                                txt4.getText().toString().matches("") ||
                                                txt5.getText().toString().matches("") ||
                                                txt6.getText().toString().matches("") ||
                                                txt7.getText().toString().matches("")


                                                ) {
                                            Toast.makeText(CheckResultsActivity.this, "Por favor, preencha todos os números!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        final ArrayList<String> list = new ArrayList<String>();
                                        listaResultsToCheck.add(new EuroResult("", "", "", "", Integer.parseInt(txt1.getText().toString()),
                                                Integer.parseInt(txt2.getText().toString()),Integer.parseInt(txt3.getText().toString()),Integer.parseInt(txt4.getText().toString())
                                        ,Integer.parseInt(txt5.getText().toString()),Integer.parseInt(txt6.getText().toString()),Integer.parseInt(txt7.getText().toString())));
                                        for (int i = 0; i < listaResultsToCheck.size(); ++i) {
                                            list.add(listaResultsToCheck.get(i).getEuroKey());
                                        }

                                       /* final StableArrayAdapter adapter = new StableArrayAdapter(CheckResultsActivity.this,
                                                android.R.layout.simple_list_item_1, list);
                                        lista.setAdapter(adapter);*/
                                        lista.setAdapter(addItemToAdapter(listaResultsToCheck));
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setLayout(500,300);
                // show it
                alertDialog.show();
            }
        });
        btnverify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                wsResults = new WsResults(drawType, lastNumberOfDraws);
                //AsyncCall para carregar resultados

                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
        });
    /*    lista.setLongClickable(true);
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked", "pos: " + pos);

                return true;
            }
        });*/
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Editar");
        menu.add(0, v.getId(), 0, "Apagar");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getTitle() == "Editar") // "Edit" chosen
        {
           loadDialogWithEuroresult(info.position);
        }
        else if(item.getTitle() == "Apagar")  // "Delete" chosen
        {
            deleteEuroResult(info.position);
        }
        else
        {
            return false;
        }

        return true;
    }
    private void deleteEuroResult(final int idlista){
        listaResultsToCheck.remove(idlista);
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < listaResultsToCheck.size(); ++i) {
            list.add(listaResultsToCheck.get(i).getEuroKey());
        }
        lista.setAdapter(addItemToAdapter(listaResultsToCheck));
       /* final StableArrayAdapter adapter = new StableArrayAdapter(CheckResultsActivity.this,
                android.R.layout.simple_list_item_1, list);*/
        //SimpleAdapter  adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);

    }
    private void loadDialogWithEuroresult(final int idlista){

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.results_prompt, null);
        promptsView.setLayoutParams(new WindowManager.LayoutParams(500, 300));
        txt1 = (EditText)promptsView.findViewById(R.id.txt1);
        txt2 = (EditText)promptsView.findViewById(R.id.txt2);
        txt3 = (EditText)promptsView.findViewById(R.id.txt3);
        txt4 = (EditText)promptsView.findViewById(R.id.txt4);
        txt5 = (EditText)promptsView.findViewById(R.id.txt5);
        txt6 = (EditText)promptsView.findViewById(R.id.txt6);
        txt7 = (EditText)promptsView.findViewById(R.id.txt7);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        txt1.setText(Integer.toString(listaResultsToCheck.get(idlista).getFirstNumber()) );
        txt2.setText(Integer.toString(listaResultsToCheck.get(idlista).getSecondNumber()));
        txt3.setText(Integer.toString(listaResultsToCheck.get(idlista).getThirdNumber()));
        txt4.setText(Integer.toString(listaResultsToCheck.get(idlista).getFourthNumber()));
        txt5.setText(Integer.toString(listaResultsToCheck.get(idlista).getFifthNumber()));
        txt6.setText(Integer.toString(listaResultsToCheck.get(idlista).getFirstLuckyNumber()));
        txt7.setText(Integer.toString(listaResultsToCheck.get(idlista).getSecondLuckyNumber()));
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Test return result
                               /* if (txt1.getText().toString().matches("") ||
                                        txt2.getText().toString().matches("") ||
                                        txt3.getText().toString().matches("") ||
                                        txt4.getText().toString().matches("") ||
                                        txt5.getText().toString().matches("") ||
                                        txt6.getText().toString().matches("") ||
                                        txt7.getText().toString().matches("")


                                        ) {
                                    Toast.makeText(CheckResultsActivity.this, "Por favor, preencha todos os números!", Toast.LENGTH_SHORT).show();
                                    return;
                                }*/
                                final ArrayList<String> list = new ArrayList<String>();
                                listaResultsToCheck.get(idlista).setFirstNumber(Integer.parseInt(txt1.getText().toString()));
                                listaResultsToCheck.get(idlista).setSecondNumber(Integer.parseInt(txt2.getText().toString()));
                                listaResultsToCheck.get(idlista).setThirdNumber(Integer.parseInt(txt3.getText().toString()));
                                listaResultsToCheck.get(idlista).setFourthNumber(Integer.parseInt(txt4.getText().toString()));
                                listaResultsToCheck.get(idlista).setFifthNumber(Integer.parseInt(txt5.getText().toString()));
                                listaResultsToCheck.get(idlista).setFirstLuckyNumber(Integer.parseInt(txt6.getText().toString()));
                                listaResultsToCheck.get(idlista).setSecondLuckyNumber(Integer.parseInt(txt7.getText().toString()));
                               /* listaResultsToCheck.add(new EuroResult("", "", "", "", Integer.parseInt(txt1.getText().toString()),
                                        Integer.parseInt(txt2.getText().toString()), Integer.parseInt(txt3.getText().toString()), Integer.parseInt(txt4.getText().toString())
                                        , Integer.parseInt(txt5.getText().toString()), Integer.parseInt(txt6.getText().toString()), Integer.parseInt(txt7.getText().toString())));*/
                                for (int i = 0; i < listaResultsToCheck.size(); ++i) {
                                    list.add(listaResultsToCheck.get(i).getEuroKey());
                                }

                               /* final StableArrayAdapter adapter = new StableArrayAdapter(CheckResultsActivity.this,
                                        android.R.layout.simple_list_item_1, list);
                                lista.setAdapter(adapter);*/
                                lista.setAdapter(addItemToAdapter(listaResultsToCheck));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setLayout(500,300);
        // show it
        alertDialog.show();
    }
    private SimpleAdapter addItemToAdapter(ArrayList<EuroResult> list)
    {
        // Each row in the list stores country name, currency and flag
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for(int i=0;i<list.size();i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt","Sorteio de "+((EuroResult)list.get(i)).getDateOnly());
            hm.put("cur",((EuroResult)list.get(i)).getEuroKey());
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
        // Getting a reference to listview of main.xml layout file
        //ListView listView = ( ListView ) findViewById(R.id.listview);

        // Setting the adapter to the listView
        //listView.setAdapter(adapter);
    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
                //mIdMap.put("Teste", i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menucheckresults, menu);
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
    public void listResults(){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.list_layout, null);
        promptsView.setLayoutParams(new WindowManager.LayoutParams(500, 300));
         alertDialogBuilder = new AlertDialog.Builder(
                context);
       // alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("List");
        ListView lv = (ListView) promptsView.findViewById(R.id.lv);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        lv.setAdapter(addItemToAdapter(wsResults.getList()));
        //alertDialogBuilder.show();
        final AlertDialog dlg = alertDialogBuilder.create();
        dlg.show();
       // listview.setAdapter(addItemToAdapter(wsResults.getList()));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {


                euroresult = new EuroResult(
                        listaEuroResults.get(position).getDateOnly(),"","","",
                        listaEuroResults.get(position).getFirstNumber(),
                        listaEuroResults.get(position).getSecondNumber(),
                        listaEuroResults.get(position).getThirdNumber(),
                        listaEuroResults.get(position).getFourthNumber(),
                        listaEuroResults.get(position).getFifthNumber(),
                        listaEuroResults.get(position).getFirstLuckyNumber(),
                        listaEuroResults.get(position).getSecondLuckyNumber()
                );
                dlg.cancel();
                //mTimeText.setText("Time: " + dateFormat.format(date));
               /* showAlertDialog(ResultsActivity.this, "Sorteio do dia "+listaEuroResults.get(position).getDateOnly(),
                        listaEuroResults.get(position).getFirstNumber()+" - "+
                                listaEuroResults.get(position).getSecondNumber()+" - "+
                                listaEuroResults.get(position).getThirdNumber()+" - "+
                                listaEuroResults.get(position).getFourthNumber()+" - "+
                                listaEuroResults.get(position).getFifthNumber()+"   ("+
                                listaEuroResults.get(position).getFirstLuckyNumber()+" - "+
                                listaEuroResults.get(position).getSecondLuckyNumber()+")", true);*/
                // list.remove(item);
                // adapter.notifyDataSetChanged();
                //view.setAlpha(1);


            }

        });

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
           // handleClickListview();
            pdia.dismiss();

        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            pdia = new ProgressDialog(CheckResultsActivity.this);
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
