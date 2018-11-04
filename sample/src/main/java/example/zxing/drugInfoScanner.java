package example.zxing;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import utilities.NetworkUtils;

public class drugInfoScanner{

    Context c;
    public drugInfoScanner(Context context) {
        c = context;
    }
    void makeSearch(String drugID){
        new FetchNetworkData().execute(drugID);
    }

    public class FetchNetworkData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params){
            if (params.length == 0) return null;
            //String searchQuery = "";
            //for (String Drug : params){
            //    searchQuery += Drug+"+";
            //}
            //searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            String searchQuery = "207106";
            URL DRUGINFO_URL = NetworkUtils.buildUrl(searchQuery);

            String responseString = null;
            try{
                responseString = NetworkUtils.getResponseFromHttpUrl(DRUGINFO_URL);
            } catch(Exception e) {
                e.printStackTrace();
            }
            if (responseString == null){
                Log.d("this is shit", "what is this");
                return "there is no response";
            }else {
                Log.d("response Json", responseString);
                return "there is response";
            }
        }   // end of method doInBackground

        // parse weather data json
        public String[] processDrugInfoJson(String responseJsonData){
            String[] weatherInfo = new String[25];
            try{
                JSONObject weatherData = new JSONObject(responseJsonData);
                JSONArray weatherArray = weatherData.getJSONArray("consolidated_weather");
                weatherInfo = new String[weatherArray.length() + 1];
                weatherInfo[0] = weatherData.getString("title");                    // Save name of the location as first element in the array
                for (int i = 1; i < weatherArray.length() + 1; i++){
                    JSONObject childJson = weatherArray.getJSONObject(i - 1);
                    String weather = childJson.getString("weather_state_name");
                    String date = childJson.getString("applicable_date");
                    String maxTemp = childJson.getString("max_temp");
                    String minTemp = childJson.getString("min_temp");

                    // save four data fields into a single string
                    weatherInfo[i] = date + "\t\t" + weather + "\t\t\n" + "Max temp is " + maxTemp.substring(0, 4)
                            + "F\t\t" + "Min temp is " + minTemp.substring(0, 4) + "F";
                }   // end of for
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return weatherInfo;


        }   // end of class FetchNetworkData
        @Override
        protected void onPostExecute(String responseData){

            //String [] weathers = processDrugInfoJson(responseData);
            String message = "You'll die";
            Toast.makeText(c, message+responseData, Toast.LENGTH_LONG).show();

        }
    }
}

