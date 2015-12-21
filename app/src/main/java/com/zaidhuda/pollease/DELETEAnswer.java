package com.zaidhuda.pollease;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Zaid on 21/12/2015.
 */
public class DELETEAnswer extends AsyncTask<String, Void, String> {
    private Activity activity;
    private Fragment fragment;
    private int choiceId;
    private int responseCode;
    private Poll poll;
    private String deleteChoiceUrl;
    private ProgressDialog progressDialog;
    private OnDELETEAnswerListener mListener;

    public DELETEAnswer(Poll poll, int choiceId, Fragment fragment) {
        this.activity = fragment.getActivity();
        this.choiceId = choiceId;
        this.poll = poll;
        activity = fragment.getActivity();
        deleteChoiceUrl = activity.getResources().getString(R.string.delete_choice_url)
                .replace(":poll_id", String.valueOf(poll.getId())).replace(":choice_id", String.valueOf(choiceId));
        this.execute(deleteChoiceUrl);

        mListener = (OnDELETEAnswerListener) fragment;
    }

    private void onAnswerAccepted() {
        mListener.onAnswerAccepted(choiceId);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(deleteChoiceUrl + "?password=" + poll.getPassword());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("password", poll.getPassword());

            conn.connect();

            responseCode = conn.getResponseCode();
            System.out.println(responseCode);
        } catch (ProtocolException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error deleting answer", Toast.LENGTH_LONG).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error deleting answer", Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error deleting answer", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error deleting answer", Toast.LENGTH_LONG).show();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(activity, "", "Deleting answer, please wait", false);
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (responseCode == 204) {
            onAnswerAccepted();
            Toast.makeText(activity, "Answer deleted", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(activity, "Failed deleting answer", Toast.LENGTH_SHORT).show();
        mListener = null;
    }

    public interface OnDELETEAnswerListener {
        void onAnswerAccepted(int choiceId);
    }
}
