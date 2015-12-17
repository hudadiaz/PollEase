package com.zaidhuda.pollease;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String jsonResult, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_scan) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String re = scanResult.getContents();
            Log.d("code", re);
            url = re;
            accessWebService();
        }
    }

    public void accessWebService() {
        JsonReadTask task = new JsonReadTask();
        task.execute(url);
    }

    private class JsonReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream response = con.getInputStream();
                jsonResult = inputStreamToString(response).toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ProcessPoll();
        }
    }

    public void ProcessPoll() {
        Poll poll = null;
        try {
            JSONObject jPoll = new JSONObject(jsonResult);
            poll = new Poll(jPoll.getInt("id"));
            poll.setQuestion(jPoll.getString("question"));
            JSONArray choices = jPoll.getJSONArray("choices");
            for (int i=0; i<choices.length(); i++) {
                JSONObject choice = (JSONObject) choices.get(i);
                Choice c = new Choice(choice.getInt("id"));
                c.setAnswer(choice.getString("answer"));
                c.setCount(choice.getInt("count"));
                poll.addChoices(c);
            }
        } catch (JSONException e) {
            Log.d("JSONParse Error ", "Error" + e.toString());
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
