package com.zaidhuda.pollease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private User user;
    private String POLLS_URL;
    private String SESSION_URL;
    private String jsonResult, request_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PollEditActivity.class);
                startActivity(intent);
            }
        });

        user = new User(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        POLLS_URL = getResources().getString(R.string.polls_url);
        SESSION_URL = getResources().getString(R.string.session_url);
        registerSession();

//        request_url = POLLS_URL+"/1";
//        startPollActivity(request_url);
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
        if (scanResult.getContents() != null) {
            String re = scanResult.getContents();
            if (re.startsWith(POLLS_URL)) {
                request_url = re;
                startPollActivity(re);
            }
        }
    }

    private void startPollActivity(String re) {
        Intent intent = new Intent(this, PollActivity.class);
        intent.putExtra("poll_url", re);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void registerSession() {
        POSTSession task = new POSTSession();
        task.execute(request_url);
    }

    public void ProcessSessionJSON() {
        try {
            JSONObject jUser = new JSONObject(jsonResult);
            User tempUser = new Gson().fromJson(jUser.toString(), User.class);
            user.setID(tempUser.getID());
            user.setToken(tempUser.getToken());
            //todo save user info to database
        } catch (Exception e) {
            Log.d("JSONParse Error ", "Error" + e.toString());
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class POSTSession extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(SESSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject param = new JSONObject();
                param.put("user", new JSONObject()
                        .put("identifier", user.getIdentifier()));

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(param.toString());
                wr.flush();

                conn.connect();
                InputStream response = conn.getInputStream();
                jsonResult = inputStreamToString(response).toString();

//                Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                Toast.makeText(getApplicationContext(), "Error logging in", Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging in, please wait", false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            ProcessSessionJSON();
            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();
        }
    }
}
