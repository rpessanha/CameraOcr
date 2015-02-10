package pessanha.com.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pessanha.com.myfirstapp.utils.MobileInternetConnectionDetector;
import pessanha.com.myfirstapp.utils.WIFIInternetConnectionDetector;


public class ResultsActivity extends Activity {
    private final String NAMESPACE = "http://www.lottery.ie/resultsservice";
    private final String URL = "http://resultsservice.lottery.ie/ResultsService.asmx";
    private final String SOAP_ACTION = "http://www.lottery.ie/resultsservice/GetResults";
    private final String METHOD_NAME = "GetResults";
    private String TAG = "PGGURU";
    private static String xmlResponse;
    private static String xmlRequest;
    private static String drawtype;
    private static String drawType="EuroMillions";
    private static String lastNumberOfDraws="2";
    private static SoapObject listaResultados;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        listview = (ListView) findViewById(R.id.listView);
        MobileInternetConnectionDetector mobileInternet = new MobileInternetConnectionDetector(getApplicationContext());
        WIFIInternetConnectionDetector wifiInternet= new WIFIInternetConnectionDetector(getApplicationContext());
        if(mobileInternet.checkMobileInternetConn() || wifiInternet.checkMobileInternetConn()){
            // Internet Connection exists
            showAlertDialog(ResultsActivity.this, "Internet Connection",
                    "Your device has mobile internet", true);
        } else {
            // Internet connection doesn't exist
            showAlertDialog(ResultsActivity.this, "No Internet Connection",
                    "Your device doesn't have mobile internet", false);
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
            }
        });

        // Showing Alert Message
        alertDialog.show();
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
    public void getResults(String drawType,String lastNumberOfDraws) {
        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //Property which holds input parameters
        PropertyInfo drawTypePI = new PropertyInfo();
        PropertyInfo lastNumberOfDrawsPI = new PropertyInfo();
        //Set Name
        drawTypePI.setName("drawType");
        //Set Value
        drawTypePI.setValue(drawType);
        //Set dataType
        drawTypePI.setType(String.class);
        request.addProperty(drawTypePI);
        //Set Name
        lastNumberOfDrawsPI.setName("lastNumberOfDraws");
        //Set Value
        lastNumberOfDrawsPI.setValue(lastNumberOfDraws);
        //Set dataType
        lastNumberOfDrawsPI.setType(String.class);

        request.addProperty(lastNumberOfDrawsPI);

        //Add the property to request object
       // request.addProperty("drawType",drawType);
        //request.addProperty("lastNumberOfDraws",lastNumberOfDraws);

        //Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.implicitTypes = false;
        envelope.dotNet = true;
       // envelope.encodingStyle = SoapSerializationEnvelope.XSD;
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug=true;
        try {
            //Invole web service
            //androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            androidHttpTransport.call(SOAP_ACTION, envelope);
            //Get the response
            //soap = GetSoapObject(method_name);
           final SoapObject response = (SoapObject) envelope.getResponse();

            //Assign it to fahren static variable
            //drawtype =(String)listaResultados.getProperty(0).toString();
           // xmlResponse=androidHttpTransport.responseDump;
          //  xmlRequest=androidHttpTransport.requestDump;
         //   xmlResponse = response.toString();
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Toast.makeText(ResultsActivity.this,xmlResponse.toString(), Toast.LENGTH_SHORT).show();
                    loadList(response);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadList(SoapObject listaResultados_)
    {

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < listaResultados_.getPropertyCount(); ++i) {
            listaResultados = (SoapObject) listaResultados_.getProperty(i);
            list.add(listaResultados.getProperty(1).toString());
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);


                               // list.remove(item);
                               // adapter.notifyDataSetChanged();
                                //view.setAlpha(1);


            }

        });
    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
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
    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            getResults(drawType, lastNumberOfDraws);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            //tv.setText(fahren + "° F");
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //tv.setText("Calculating...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

}
