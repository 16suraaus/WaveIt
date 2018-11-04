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
    List<drugInfo> output = new ArrayList<>();
    List<String> drugs = new ArrayList<>();
    String json = "";

    // Drug interaction info object to return
    public class drugInfo{
        public String[] drugs;
        public String interaction;
        public String severity;

        public drugInfo(){
            this.drugs = null;
            this.interaction = "";
            this.severity = "";
        }
    }
    // Add drug id to the current list
    public void add_data(String drug){
        this.drugs.add(drug);
    }
    public void clear_data(){
        this.drugs.clear();
        this.output.clear();
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
        String output = "";
        for (int i = 0; i < this.output.size(); i++){
            drugInfo curr = this.output.get(i);
            String[] names = curr.drugs;
            output += "Drugs are:";
            for (String name : names){
                output += " " + name;
            }
            output += "\n";
            output += "Severity: " + curr.severity + "\n";
            output += "Description: " + curr.interaction + "\n\n";
        }
        // Remove last two newline characters
        //output = output.substring(0, output.length() - 2);

        return this.json;
    }

    public void updateDrugInfo(List<drugInfo> info){
        this.output = info;
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
            List<drugInfo> info = processDrugInfoJson(responseData);
            updateDrugInfo(info);
            String message = "Analyzing interactions!";
            Toast.makeText(c, message, Toast.LENGTH_LONG).show();

            //delegate.processFinish("null");
        }
    }
}

