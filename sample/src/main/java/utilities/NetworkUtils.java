package utilities;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    static final String DRUGINFO_URL = "https://rxnav.nlm.nih.gov/REST/interaction/list.json?rxcuis=";

    public static URL buildUrl(String userQuery){

        Uri builtUri = null;
        // Different uri building depending on if it's a weather uri or location uri
        //builtUri = Uri.parse(DRUGINFO_URL).buildUpon()
        //            .appendQueryParameter("rxcuis",userQuery)
        //            .build();
        URL url = null;
        try{
            url = new URL(DRUGINFO_URL);
            Log.d("build url", DRUGINFO_URL+userQuery);
            //Log.d("informational", "URL: " + builtUri.toString());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }   // end of method buildUrl

    public static String getResponseFromHttpUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //try {
        //    InputStream in = urlConnection.getInputStream();
        //    Scanner scanner = new Scanner(in);
        //    scanner.useDelimiter("\\A");

        //    boolean hasInput = scanner.hasNext();
        //    if (hasInput) return scanner.next();
        //    else return null;
        //} finally {
        //    urlConnection.disconnect();
        //}
        BufferedReader br;
        if (200 <= urlConnection.getResponseCode() && urlConnection.getResponseCode() <= 299) {
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();
    }   // end of method
} // end of class
