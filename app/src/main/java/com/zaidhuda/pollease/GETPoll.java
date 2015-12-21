package com.zaidhuda.pollease;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Zaid on 20/12/2015.
 */
public class GETPoll extends AsyncTask<String, Void, String> {
    private Activity activity;
    private String POLLS_URL;
    private String requestUrl;
    private String jsonResult;
    private Poll poll;
    private ProgressDialog progressDialog;
    private OnGETPollListener mListener;


    public GETPoll(String polls_url, String requestUrl, Activity activity) {
        POLLS_URL = polls_url;
        this.activity = activity;
        this.requestUrl = requestUrl;
        this.execute(requestUrl);
        mListener = (OnGETPollListener) activity;
    }

    private void setPoll(Poll poll) {
        this.poll = poll;
        mListener.setPoll(poll);
    }

    public void detachListener() {
        mListener = null;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream response = con.getInputStream();
            jsonResult = inputStreamToString(response).toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error in retrieving poll", Toast.LENGTH_LONG).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error in retrieving poll", Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error in retrieving poll", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error in retrieving poll", Toast.LENGTH_LONG).show();
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
            Toast.makeText(activity, "Error in retrieving poll", Toast.LENGTH_LONG).show();
        }
        return answer;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity, "", "Retrieving data, please wait", false);
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (requestUrl.startsWith(POLLS_URL)) {
            ProcessPollJSON();
        }
    }

    public void ProcessPollJSON() {
        try {
            JSONObject jPoll = new JSONObject(jsonResult);
            setPoll(new Gson().fromJson(jPoll.getJSONObject("poll").toString(), Poll.class));
            Toast.makeText(activity, "Poll retrieved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("JSONParse Error ", "Error" + e.toString());
            Toast.makeText(activity, "Error in retrieving poll", Toast.LENGTH_LONG).show();
        }
    }

    public interface OnGETPollListener {
        void setPoll(Poll poll);
    }
}
