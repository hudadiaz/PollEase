package com.zaidhuda.pollease.AsyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.objects.Poll;

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

    private void onAnswerDeleted() {
        mListener.onAnswerDeleted(choiceId);
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
        } catch (ProtocolException e) {
            e.printStackTrace();
            showErrorToast("Failed deleting answer");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            showErrorToast("Failed deleting answer");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showErrorToast("Failed deleting answer");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorToast("Failed deleting answer");
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
        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            onAnswerDeleted();
            Toast.makeText(activity, "Answer deleted", Toast.LENGTH_SHORT).show();
        } else
            showErrorToast("Failed deleting answer");
        mListener = null;
    }

    private void showErrorToast(final String msg) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnDELETEAnswerListener {
        void onAnswerDeleted(int choiceId);
    }
}
