package com.zaidhuda.pollease.AsyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.objects.Poll;
import com.zaidhuda.pollease.objects.User;

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
public class POSTSelection extends AsyncTask<String, Void, String> {
    private Activity activity;
    private Fragment fragment;
    private String selectionUrl;
    private Poll poll;
    private User user;
    private int selectedChoiceID;
    private int previousChoice;
    private ProgressDialog progressDialog;
    private OnPOSTSelectionListener mListener;
    private int responseCode;

    public POSTSelection(User user, Poll poll, int selectedChoiceID, Fragment fragment) {
        this.activity = fragment.getActivity();
        this.poll = poll;
        this.user = user;
        this.selectedChoiceID = selectedChoiceID;
        selectionUrl = activity.getResources().getString(R.string.selection_url).replace(":poll_id", String.valueOf(poll.getId()));
        this.execute(selectionUrl);

        mListener = (OnPOSTSelectionListener) fragment;
    }

    private void onSelectionPosted(int selectedChoiceID, int previousChoice) {
        mListener.onSelectionPosted(selectedChoiceID, previousChoice);
    }

    public void detachListener() {
        mListener = null;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(selectionUrl.replace(":id", String.valueOf(selectedChoiceID)));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            JSONObject param = new JSONObject();
            JSONObject userP = new JSONObject().put("identifier", user.getIdentifier())
                    .put("token", user.getToken());
            JSONObject selectionP = new JSONObject().put("choice_id", selectedChoiceID);
            param.put("poll_id", poll.getId()).put("user", userP).put("selection", selectionP);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(param.toString());
            wr.flush();

            conn.connect();

            responseCode = conn.getResponseCode();
            InputStream response = conn.getInputStream();
            String jsonResult = inputStreamToString(response).toString();
            JSONObject jsonObject = new JSONObject(jsonResult);
            previousChoice = jsonObject.getInt("previous_selection");
            Log.d("previous", String.valueOf(previousChoice));
        } catch (ProtocolException e) {
            e.printStackTrace();
            showErrorToast("Error in submitting vote");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorToast("Error in submitting vote");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showErrorToast("Error in submitting vote");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorToast("Error in submitting vote");
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorToast("Error in submitting vote");
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
            showErrorToast("Error in submitting vote");
        }
        return answer;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity, "", "Submitting vote, please wait", false);
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (responseCode == HttpURLConnection.HTTP_ACCEPTED || responseCode == HttpURLConnection.HTTP_CREATED) {
            onSelectionPosted(selectedChoiceID, previousChoice);
            Toast.makeText(activity, "Vote submitted", Toast.LENGTH_SHORT).show();
        } else
            showErrorToast("Error in submitting vote");
    }

    private void showErrorToast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnPOSTSelectionListener {
        void onSelectionPosted(int selectedChoiceID, int previousChoice);
    }
}
