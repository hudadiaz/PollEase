package com.zaidhuda.pollease.AsyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zaidhuda.pollease.Objects.Choice;
import com.zaidhuda.pollease.Objects.Poll;
import com.zaidhuda.pollease.R;

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
public class POSTChoice extends AsyncTask<String, Void, String> {
    private Activity activity;
    private Fragment fragment;
    private Choice choice;
    private String choiceUrl;
    private String answer;
    private Poll poll;
    private ProgressDialog progressDialog;
    private OnPOSTChoiceListener mListener;
    private int responseCode;

    public POSTChoice(Poll poll, String choice, Fragment fragment) {
        this.activity = fragment.getActivity();
        this.answer = choice;
        this.poll = poll;
        activity = fragment.getActivity();
        choiceUrl = activity.getResources().getString(R.string.choices_url).replace(":poll_id", String.valueOf(poll.getId()));
        this.execute(choiceUrl);

        mListener = (OnPOSTChoiceListener) fragment;
    }

    private void onCreateAnswer(Choice choice) {
        mListener.onCreateAnswer(choice);
    }

    public void detachListener() {
        mListener = null;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(choiceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            JSONObject param = new JSONObject();
            JSONObject choiceP = new JSONObject().put("answer", answer);
            param.put("choice", choiceP);
            param.put("password", poll.getPassword());

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(param.toString());
            wr.flush();

            conn.connect();

            responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                InputStream response = conn.getInputStream();
                String jsonResult = inputStreamToString(response).toString();
                JSONObject jChoice = new JSONObject(jsonResult);
                choice = new Gson().fromJson(jChoice.getJSONObject("choice").toString(), Choice.class);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            showErrorToast("Error submitting answer");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorToast("Error submitting answer");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showErrorToast("Error submitting answer");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorToast("Error submitting answer");
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorToast("Error submitting answer");
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
            showErrorToast("Error submitting answer");
        }
        return answer;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity, "", "Submitting answer, please wait", false);
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            onCreateAnswer(choice);
            Toast.makeText(activity, "Choice added", Toast.LENGTH_SHORT).show();
        } else
            showErrorToast("Error submitting answer");
    }

    private void showErrorToast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnPOSTChoiceListener {
        void onCreateAnswer(Choice choice);
    }
}
