package com.zaidhuda.pollease.AsyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.helpers.PollDataSource;
import com.zaidhuda.pollease.objects.Poll;

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

/**
 * Created by Zaid on 20/12/2015.
 */
public class POSTPoll extends AsyncTask<String, Void, String> {
    private Activity activity;
    private Fragment fragment;
    private Poll poll;
    private String createUrl;
    private String question;
    private String password;
    private ProgressDialog progressDialog;
    private OnPOSTPollListener mListener;
    private int responseCode;
    private PollDataSource pollDataSource;

    public POSTPoll(String question, String password, Fragment fragment) {
        this.activity = fragment.getActivity();
        this.question = question;
        this.password = password;
        activity = fragment.getActivity();
        createUrl = activity.getResources().getString(R.string.polls_url);
        pollDataSource = new PollDataSource(activity);
        this.execute(createUrl);
        mListener = (OnPOSTPollListener) fragment;
    }

    private void onPollCreated() {
        poll.setUrl(createUrl + poll.getId());
        pollDataSource.open();
        try {
            pollDataSource.createPoll(poll);
        } catch (SQLException e) {
            System.out.println(e);
        }
        pollDataSource.close();

        if (mListener != null)
            mListener.onPollCreated(poll);
    }

    public void detachListener() {
        mListener = null;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(createUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            JSONObject pollP = new JSONObject()
                    .put("question", question)
                    .put("password", password);
            JSONObject param = new JSONObject().put("poll", pollP);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(param.toString());
            wr.flush();

            conn.connect();

            responseCode = conn.getResponseCode();
            InputStream response = conn.getInputStream();
            String jsonResult = inputStreamToString(response).toString();
            JSONObject jPoll = new JSONObject(jsonResult);
            poll = new Gson().fromJson(jPoll.getJSONObject("poll").toString(), Poll.class);
        } catch (ProtocolException e) {
            e.printStackTrace();
            showErrorToast("Error submitting poll");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorToast("Error submitting poll");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showErrorToast("Error submitting poll");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorToast("Error submitting poll");
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorToast("Error submitting poll");
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
            showErrorToast("Error submitting poll");
        }
        return answer;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity, "", "Submitting poll, please wait", false);
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            onPollCreated();
            Toast.makeText(activity, "Poll created", Toast.LENGTH_SHORT).show();
        } else
            showErrorToast("Error submitting poll");

    }

    private void showErrorToast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnPOSTPollListener {
        void onPollCreated(Poll poll);
    }
}
