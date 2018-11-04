package example.zxing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ContinuousCaptureActivity extends Activity {
    private static final String TAG = ContinuousCaptureActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private Spinner spinner;
    private scanner sc;
    private drugInfoScanner dis;
    String lastSpinnerState;
    private Button resetButton;
    private TextView bookListText;
    private Button helpbutton;
    private Button drugSubmitButton;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(!spinner.getSelectedItem().toString().equals(lastSpinnerState)){
                bookListText.setText("'o' is correct, 'x' is incorrect");
                lastText = "";
                sc.clearLists();
            }
            lastSpinnerState = spinner.getSelectedItem().toString();
            if(spinner.getSelectedItem().toString().equals("Drug Info")){
                drugSubmitButton.setVisibility(View.VISIBLE);
                bookListText.setVisibility(View.INVISIBLE);
            }else{
                drugSubmitButton.setVisibility(View.INVISIBLE);
                bookListText.setVisibility(View.VISIBLE);
            }

            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            lastText = result.getText();
            barcodeView.setStatusText(result.getText());

            //beepManager.playBeepSoundAndVibrate(sc.inputOrdered(lastText));
            if(spinner.getSelectedItem().toString().equals("Odd One Out")) {
                try {
                    beepManager.playBeepSoundAndVibrate(sc.inputOdd(lastText));
                }
                catch(Exception e) {
                    String message = "Error, QR code must be Library of Congress classification.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                bookListText.setText(sc.returnBookListString());
            }else if(spinner.getSelectedItem().toString().equals("Ordered Books")){
                try {
                    beepManager.playBeepSoundAndVibrate(sc.inputOrdered(lastText));
                }
                catch(Exception e) {
                    String message = "Error, QR code must be Library of Congress classification.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                bookListText.setText(sc.returnBookListString());
            }else if(spinner.getSelectedItem().toString().equals("Drug Info")){
                dis.add_data(lastText);
                beepManager.playBeepSoundAndVibrate(false);
            }else{
                Log.d("spinner", spinner.getSelectedItem().toString());
            }

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.continuous_scan);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        sc = new scanner();
        dis = new drugInfoScanner(this);
        spinner = findViewById(R.id.modeSpinner);
        lastSpinnerState = "";
        bookListText = findViewById(R.id.bookListText);
        drugSubmitButton = findViewById(R.id.drugSubmitButton);

        resetClicked();
        drugSubmitClicked();
        helpbuttonMethod();
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void resetClicked(){
        resetButton = findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastText = "";
                bookListText.setText("'o' is correct, 'x' is incorrect");
                sc.clearLists();
                dis.clear_data();
                drugSubmitButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void drugSubmitClicked(){

        drugSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchResults = dis.search_and_output();
                Log.d("json data",searchResults);
                String result = "";
                try{
                    JSONObject drugData = new JSONObject(searchResults);
                    JSONArray drugArray = drugData.getJSONArray("fullInteractionTypeGroup");
                    //Log.d("xxresult", drugArray.toString());
                    for (int i = 0; i < drugArray.length(); i++){
                        JSONObject temp = drugArray.getJSONObject(i);
                        Log.d("xxresult_0", temp.toString());
                        JSONObject tmp = temp.getJSONArray("fullInteractionType").getJSONObject(0);
                        JSONArray names = tmp.getJSONArray("minConcept");
                        Log.d("xxresult_1", names.toString());
                        result += "Drugs are:";
                        // Add names
                        for (int j = 0; j < names.length(); j++){
                            JSONObject name_object = names.getJSONObject(j);
                            Log.d("xxresult_2", name_object.toString());
                            result += "\n" + Integer.toString(j+1) + ": " + name_object.getString("name");
                        }
                        JSONObject indiv = tmp.getJSONArray("interactionPair").getJSONObject(0);
                        Log.d("xxresult_3", indiv.toString());
                        String severity = indiv.getString("severity");
                        String description = indiv.getString("description");
                        result += "\n";
                        Log.d("xxresult_4", result);
                        if (severity.equals("N/A")){
                            severity = "Low";
                        }
                        result += "Severity: " + severity + "\n";
                        Log.d("xxresult_5", result);
                        result += "Description: " + description + "\n\n";
                        Log.d("xxresult_6", result);
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
                dis.clear_data();
                if (result.equals("")){
                    result = "No known interactions!";
                }

                AlertDialog alertDialog = new AlertDialog.Builder(ContinuousCaptureActivity.this).create();
                alertDialog.setTitle("Interaction");
                SpannableStringBuilder str = new SpannableStringBuilder(result);

                alertDialog.setMessage(str);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                drugSubmitButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void helpbuttonMethod(){
        helpbutton = findViewById(R.id.helpbutton);

        helpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ContinuousCaptureActivity.this).create();
                alertDialog.setTitle("Usage");
                SpannableStringBuilder str = new SpannableStringBuilder("Note: Compatible with Library of Congress identification schematic.\n\nOdd One Out: Gets category from first book scanned. Any further books" +
                        " are checked against the book.\n\nOrdered Books: Checks the sequential order of books scanned.\n\n" +
                        "" +
                        "Reset Items: Clears the cache of books.\n\n" +
                        "Drug Info: Scan all of your medications and press Submit when finished to view dangerous medication combinations.");
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 69, 80, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 169, 183, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 232, 243, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 272, 282, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



                alertDialog.setMessage(str);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
