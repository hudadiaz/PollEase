package com.zaidhuda.pollease;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
            InputStream response = conn.getInputStream();
            String jsonResult = inputStreamToString(response).toString();
            JSONObject jsonObject = new JSONObject(jsonResult);
            previousChoice = jsonObject.getInt("previous_selection");
            Log.d("previous", String.valueOf(previousChoice));
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
            Toast.makeText(activity, "Error..." + e.toString(), Toast.LENGTH_LONG).show();
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
        onSelectionPosted(selectedChoiceID, previousChoice);
    }

    public interface OnPOSTSelectionListener {
        void onSelectionPosted(int selectedChoiceID, int previousChoice);
    }
}
