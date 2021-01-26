package com.remo.meteobucato;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.Locale;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    ProgressDialog pd;  // da capire meglio la classe ProgressDialog
    TextView txtMeteoDomani = null, txtMeteoDopodomani = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView txtDomaniView = (TextView) findViewById(R.id.txtDataDomani);
        final TextView txtDopodomaniView = (TextView) findViewById(R.id.txtDataDopodomani);

        // imposto la data di domani
        Locale.setDefault(Locale.ITALIAN);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,1); // aggiungo un giorno
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy E");
        String strDate = sdf.format(c.getTime());
        c.add(Calendar.DATE,1); // aggiungo due giorno
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy E");
        String strDate2 = sdf2.format(c.getTime());

        //String currentDateTimeString = java.text.DateFormat.getDateTimeInstance(DateFormat.SHORT).format(new Date());
        txtDomaniView.setText(strDate);
        txtDopodomaniView.setText(strDate2);

        //new JsonTask().execute("http://api.openweathermap.org/data/2.5/forecast?q=lecce&appid=35222ccfcb5285d12e8a0e3222d59d9c");
        new JsonTask().execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=lecce&cnt=3&appid=35222ccfcb5285d12e8a0e3222d59d9c&lang=it");
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
            txtMeteoDopodomani = (TextView) findViewById(R.id.txtMeteoDopodomani);

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
            final TextView txtMeteoDopodomani = (TextView) findViewById(R.id.txtMeteoDopodomani);
            String datejson = null, datejson2 = null, ico1 = null, ico2 = null;
            Integer id1 = 0, id2 = 0;

            final ImageView ok1 = (ImageView) findViewById(R.id.imageOK1);
            final ImageView no1 = (ImageView) findViewById(R.id.imageNO1);
            final ImageView ok2 = (ImageView) findViewById(R.id.imageOK2);
            final ImageView no2 = (ImageView) findViewById(R.id.imageNO2);
            // http://openweathermap.org/img/wn/10d@2x.png

            String urldisplay = null;
            Bitmap mIcon1 = null;
            Bitmap mIcon2 = null;
            final ImageView icom1 = (ImageView) findViewById(R.id.imageICO1);
            final ImageView icom2 = (ImageView) findViewById(R.id.imageICO2);

            icom1.setImageResource(R.drawable.s01d2x);

            try {
                JSONObject json = new JSONObject(result);
                //datejson = json.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
                datejson = json.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("description");
                datejson2 = json.getJSONArray("list").getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("description");
                id1 = json.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getInt("id");
                id2 = json.getJSONArray("list").getJSONObject(2).getJSONArray("weather").getJSONObject(0).getInt("id");
                ico1 = json.getJSONArray("list").getJSONObject(1).getJSONArray("weather").getJSONObject(0).getString("icon");
                ico2 = json.getJSONArray("list").getJSONObject(2).getJSONArray("weather").getJSONObject(0).getString("icon");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            txtMeteoDomani.setText(datejson);
            txtMeteoDopodomani.setText(datejson2);

            icom1.setImageResource(setIcon(ico1));
            icom2.setImageResource(setIcon(ico2));

            icom1.setVisibility(View.VISIBLE);
            icom2.setVisibility(View.VISIBLE);

            if (id1 < 600 && id1 != 771 && id1 != 762) {
                ok1.setVisibility(View.INVISIBLE);
                no1.setVisibility(View.VISIBLE);
            }else{
                ok1.setVisibility(View.VISIBLE);
                no1.setVisibility(View.INVISIBLE);
            }

            if (id2 < 600 && id2 != 771 && id2 != 762) {
                ok2.setVisibility(View.INVISIBLE);
                no2.setVisibility(View.VISIBLE);
            }else{
                ok2.setVisibility(View.VISIBLE);
                no2.setVisibility(View.INVISIBLE);
            }
        }
    }

    public int setIcon(String iconame) {

        int ricon = 0;

        if (iconame.contains("01d"))
            ricon = R.drawable.s01d2x;
        if (iconame.contains("02d"))
            ricon = R.drawable.s02d2x;
        if (iconame.contains("03d"))
            ricon = R.drawable.s03d2x;
        if (iconame.contains("04d"))
            ricon = R.drawable.s04d2x;
        if (iconame.contains("09d"))
            ricon = R.drawable.s09d2x;
        if (iconame.contains("11d"))
            ricon = R.drawable.s11d2x;
        if (iconame.contains("13d"))
            ricon = R.drawable.s13d2x;
        if (iconame.contains("50d"))
            ricon = R.drawable.s50d2x;
        if (iconame.contains("01n"))
            ricon = R.drawable.s01n2x;
        if (iconame.contains("02n"))
            ricon = R.drawable.s02n2x;
        if (iconame.contains("03n"))
            ricon = R.drawable.s03n2x;
        if (iconame.contains("04n"))
            ricon = R.drawable.s04n2x;
        if (iconame.contains("09n"))
            ricon = R.drawable.s09n2x;
        if (iconame.contains("11n"))
            ricon = R.drawable.s11n2x;
        if (iconame.contains("13n"))
            ricon = R.drawable.s13n2x;
        if (iconame.contains("50n"))
            ricon = R.drawable.s50n2x;

        return ricon;
    }

    /** Called when the user taps the Send button */
    public void sendMessage() {
        // Do something in response to button
    }

}