package com.remo.meteobucato;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ProgressDialog pd;  // da capire meglio la classe ProgressDialog
    TextView txtMeteoDomani = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView txtDomaniView = (TextView) findViewById(R.id.txtDataDomani);

        // imposto la data di domani
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,1); // aggiungo un giorno
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());

        //String currentDateTimeString = java.text.DateFormat.getDateTimeInstance(DateFormat.SHORT).format(new Date());
        txtDomaniView.setText(strDate);

        new JsonTask().execute("http://api.openweathermap.org/data/2.5/forecast?q=lecce&appid=35222ccfcb5285d12e8a0e3222d59d9c");
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            txtMeteoDomani = (TextView) findViewById(R.id.txtMeteoDomani);

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    /*
                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    */

                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                    reader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                        Log.d("Response: ", ">>>>> " + line);   //here u ll get whole response...... :-)
                    }

                    return stringBuilder.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            if(result != null)
            {
                //try {
                    Log.e("App", "Success: " + result );
                //} catch (JSONException ex) {
                //    Log.e("App", "Failure", ex);
                //}
            }

            final TextView txtMeteoDomani = findViewById(R.id.txtMeteoDomani);
            String datejson = null;
            try {
                JSONObject json = new JSONObject(result);
                datejson = json.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            txtMeteoDomani.setText(datejson);
        }
    }

    /** Called when the user taps the Send button */
    public void sendMessage() {
        // Do something in response to button
    }

}