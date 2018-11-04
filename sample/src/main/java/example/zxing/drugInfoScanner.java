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

import utilities.NetworkUtils;



public class drugInfoScanner{

    Context c;
    public drugInfoScanner(Context context) {
        c = context;
    }
    List<drugInfo> output = new ArrayList<>();
    void makeSearch(String drugID){
        new FetchNetworkData().execute(drugID);
    }

    public class drugInfo{
        public String[] drugs;
        public String interaction;
        public String severity;

        public drugInfo(){
            this.drugs = null;
            this.interaction = "";
            this.severity = "";
        }

        public drugInfo(String[] drugs, String interaction, String severity){
            this.drugs = drugs;
            this.interaction = interaction;
            this.severity = severity;
        }
    }

    public void updateDrugInfo(List<drugInfo> info){
        this.output = info;
    }

    public class FetchNetworkData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params){
            if (params.length == 0) return null;
            String searchQuery = "";
            for (String Drug : params){
                searchQuery += Drug+"+";
            }
            searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            URL DRUGINFO_URL = NetworkUtils.buildUrl(searchQuery);

            String responseString = null;
            try{
                responseString = NetworkUtils.getResponseFromHttpUrl(DRUGINFO_URL);
            } catch(Exception e) {
                e.printStackTrace();
            }

            return responseString;
        }   // end of method doInBackground

        // parse weather data json
        public List<drugInfo> processDrugInfoJson(String responseJsonData){
            List<drugInfo> output = new ArrayList<>();
            try{
                JSONObject drugData = new JSONObject(responseJsonData);
                JSONArray drugArray = drugData.getJSONArray("fullInteractionType");
                for (int i = 0; i < drugArray.length(); i++){
                    drugInfo current = new drugInfo();
                    JSONObject tmp = drugArray.getJSONObject(i);
                    JSONArray names = tmp.getJSONArray("minConcept");
                    String[] name = new String[2];
                    for (int j = 0; j < names.length(); j++){
                        JSONObject name_object = names.getJSONObject(i);
                        name[j] = name_object.getString("name");
                    }
                    JSONObject indiv = tmp.getJSONObject("interactionPair");
                    String severity = indiv.getString("severity");
                    String description = indiv.getString("description");
                    current.drugs = name;
                    current.severity = severity;
                    current.interaction = description;
                    output.add(current);
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return output;

        }   // end of class FetchNetworkData
        @Override
        protected void onPostExecute(String responseData){

            updateDrugInfo(processDrugInfoJson(responseData));
            String message = "Ouput sent!";
            Toast.makeText(c, message, Toast.LENGTH_LONG).show();

        }
    }
}

