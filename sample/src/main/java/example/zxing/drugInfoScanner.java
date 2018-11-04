package example.zxing;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import utilities.NetworkUtils;

public class drugInfoScanner{

    Context c;
    public drugInfoScanner(Context context) {
        c = context;
    }
    List<String> drugs = new ArrayList<>();
    String json = "";

    // Add drug id to the current list
    public void add_data(String drug){
        this.drugs.add(drug);
    }
    public void clear_data(){
        this.drugs.clear();
    }
    // After done inputting, search for interactions and output string
    public String search_and_output() {
        if (this.drugs.size() < 2){
            return "Input more than 1 drugs!";
        }
        String[] drugID = new String[this.drugs.size()];
        for (int i = 0; i < this.drugs.size(); i++) {
            drugID[i] = this.drugs.get(i);
        }

        new FetchNetworkData().execute(drugID);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e){
            e.printStackTrace();
        }

        return this.json;
    }

    public void update(String input){
        this.json = input;
    }
    public class FetchNetworkData extends AsyncTask<String, Void, String> {

        public AsyncResponse delegate = null;

        @Override
        protected String doInBackground(String... params){
            if (params.length == 0) return null;
            String searchQuery = "";
            for (String Drug : params){
                searchQuery += Drug+"+";
            }
            searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            Log.d("searchquery", searchQuery);
            URL DRUGINFO_URL = NetworkUtils.buildUrl(searchQuery);
            Log.d("searchquery",DRUGINFO_URL.toString());

            String responseString = null;
            try{
                responseString = NetworkUtils.getResponseFromHttpUrl(DRUGINFO_URL);
            } catch(Exception e) {
                e.printStackTrace();
            }
            Log.d("uniquetag", responseString);
            update(responseString);
            return responseString;
        }   // end of method doInBackground

        // end of class FetchNetworkData
        @Override
        protected void onPostExecute(String responseData){
            //List<drugInfo> info = processDrugInfoJson(responseData);
            //updateDrugInfo(info);

            //delegate.processFinish("null");
        }
    }
}

