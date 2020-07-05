package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // Global variables
    EditText inputText;
    Button button;
    TextView outputText;


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls){
            String result = "";

            // This is like our browser where we input the url to get to a website
            HttpURLConnection urlConnection = null;

            // We put this in a try catch in case we enter sth else beside a url (example forgot http://)
            try {
                // Get our string input (urls) and assign it to our url Variable
                URL url = new URL(urls[0]);
                // Assign our url to our httpURLConnection (browser)
                urlConnection = (HttpURLConnection) url.openConnection();

                // Our reader to read the file get from the website
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                // data is every char that we read one by one, if it reach the end it will be -1
                int data = reader.read();

                StringBuilder str = new StringBuilder("");

                // It will keep on reading new char, adding that char to result, over and over until it reach the end which is value -1
                while(data != -1){
                    // Convert data from int to char
                    char current = (char) data;

                    // Add the char characters by character to result
                    //result += current;
                    str.append(current);

                    // Read the next char
                    data = reader.read();
                }
                reader.close();

                // return the result when finish
                //return result;
                return str.toString();
            } catch (Exception e){
                e.printStackTrace();;
            }
            // return fail if sth when wrong and it did not finish reading from the website
            return "Failed";

            //Note: If it failed (error permission denied), go to AndroidManifest.xml file and add the line below
            // <uses-permission android:name="android.permission.INTERNET"/>
            // We do this to ask android permission for our app to access the internet.
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s=="Failed")
                outputText.setText("City Not Found.");

            Log.i("JSON", s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String outputStr = "";
                JSONObject jsonPart;

                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                for (int i = 0; i < arr.length(); i++){
                    jsonPart = arr.getJSONObject(i);

                    outputStr = jsonPart.getString("main") + ": ";
                    outputStr += jsonPart.getString("description");
                }

                outputStr += "\n";

                jsonPart = jsonObject.getJSONObject("main");
                outputStr += "Temperature: " + jsonPart.getString("temp");
                outputStr += " Feels like: " + jsonPart.getString("feels_like");

                outputStr += "\n";

                jsonPart = jsonObject.getJSONObject("wind");
                outputStr += "Wind Speed: " + jsonPart.getString("speed");

                outputStr += "\n";

                jsonPart = jsonObject.getJSONObject("sys");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                Date date = new Date();
                date.setTime(Long.parseLong(jsonPart.getString("sunrise"))*1000);

                outputStr += "Sunrise: " + simpleDateFormat.format(date);
                date = new Date(Long.parseLong(jsonPart.getString("sunset"))*1000);
                outputStr += " Sunset: " + simpleDateFormat.format(date);


                //Write to outputText
                //format the string
                outputText.setText(outputStr);
                Log.i("OutputStr", outputStr);

            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    // onClick button function
    public void getFun(View view){
        DownloadTask task = new DownloadTask();
        String cityName = inputText.getText().toString();
        //String cityName = "Phnom+Penh";
        //format cityName
        cityName.replace(" ", "+");

        String url = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&units=metric&appid="; //appid is your api key

        try {
            task.execute(url); // This .get() will get the string that we return in the doINBackground function.
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize
        inputText = (EditText) findViewById(R.id.inputText);
        button = (Button) findViewById(R.id.button);
        outputText = (TextView) findViewById(R.id.outputText);
    }
}