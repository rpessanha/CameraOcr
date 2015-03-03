package pessanha.com.myfirstapp.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pessanha.com.myfirstapp.EuroResult;
import pessanha.com.myfirstapp.ResultsActivity;

/**
 * Created by pessanha on 21/02/2015.
 *
 * Consulta o webservice lottery e devolve lista pronta para listview e lista de euroresults.
 */
public class WsResults {
    private  String drawType;
    private  String lastNumberOfDraws;
    private final String NAMESPACE = "http://www.lottery.ie/resultsservice";
    private final String URL = "http://resultsservice.lottery.ie/ResultsService.asmx";
    private final String SOAP_ACTION = "http://www.lottery.ie/resultsservice/GetResults";
    private final String METHOD_NAME = "GetResults";
    private static SoapObject listaResultados;
    private ArrayList<EuroResult>  listaEuroResults;
    private final ArrayList<String> list = new ArrayList<String>();
    public WsResults(String drawType, String lastNumberOfDraws) {
        listaEuroResults = new ArrayList<EuroResult>();
        this.drawType = drawType;
        this.lastNumberOfDraws = lastNumberOfDraws;
    }

    /**
     * Get results as response from WS
     *
     */
    public void getResults(){
        SoapObject response = null; //Resposta
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
             response= (SoapObject) envelope.getResponse();

            //Assign it to fahren static variable
            //drawtype =(String)listaResultados.getProperty(0).toString();
            // xmlResponse=androidHttpTransport.responseDump;
            //  xmlRequest=androidHttpTransport.requestDump;
            //   xmlResponse = response.toString();
           /* this.runOnUiThread(new Runnable() {
                public void run() {
                    // Toast.makeText(ResultsActivity.this,xmlResponse.toString(), Toast.LENGTH_SHORT).show();
                    loadList(response);
                }
            });*/


        } catch (Exception e) {
            e.printStackTrace();
        }
       // return response;
        loadList(response);
    }

    private void loadList(SoapObject listaResultados_)
    {


        for (int i = 0; i < listaResultados_.getPropertyCount(); ++i) {

            listaResultados = (SoapObject) listaResultados_.getProperty(i);
            EuroResult euroresultado = new EuroResult(listaResultados);
            listaEuroResults.add(euroresultado);
            list.add(euroresultado.getDateOnly());
        }
        //return list;

    }
    public ArrayList<EuroResult> getList(){
        return listaEuroResults;
    }
    public ArrayList<EuroResult> getEuroResultsList(){
        return listaEuroResults;
    }

}
