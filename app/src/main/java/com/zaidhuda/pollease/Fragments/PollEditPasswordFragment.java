package com.zaidhuda.pollease.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zaidhuda.pollease.Objects.Poll;
import com.zaidhuda.pollease.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class PollEditPasswordFragment extends Fragment {
    private static final String POLL = "poll";
    private View view;
    private Poll poll;
    private String authorizeUrl;
    private String password;
    private EditText passwordET;
    private int responseCode;
    private OnFragmentInteractionListener mListener;

    public PollEditPasswordFragment() {
    }

    public static PollEditPasswordFragment newInstance(Poll poll) {
        PollEditPasswordFragment fragment = new PollEditPasswordFragment();
        Bundle args = new Bundle();
        args.putSerializable(POLL, poll);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            poll = (Poll) getArguments().getSerializable(POLL);
        }
        authorizeUrl = getResources().getString(R.string.authorize_url);
        authorizeUrl = authorizeUrl.replace(":poll_id", String.valueOf(poll.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.verify_password_title);
        view = inflater.inflate(R.layout.fragment_poll_edit_password, container, false);
        ((TextView) view.findViewById(R.id.question_TEXT)).setText(poll.getQuestion());
        passwordET = (EditText) view.findViewById(R.id.password_editText);
        view.findViewById(R.id.poll_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordET.getText().toString();
                submitChoice();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_poll_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.go_to_main) {
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onPasswordAccepted() {
        if (mListener != null)
            mListener.onPasswordAccepted(password);
    }

    private void submitChoice() {
        POSTAuthorize task = new POSTAuthorize();
        task.execute(authorizeUrl);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onPasswordAccepted(String password);
    }

    private class POSTAuthorize extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(authorizeUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject param = new JSONObject();
                param.put("password", password);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(param.toString());
                wr.flush();

                conn.connect();

                responseCode = conn.getResponseCode();
            } catch (ProtocolException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error in submitting password", Toast.LENGTH_LONG).show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error in submitting password", Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error in submitting password", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error in submitting password", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error in submitting password", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "", "Verifying password, please wait", false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                onPasswordAccepted();
                Toast.makeText(getActivity(), "Password match", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getActivity(), "Password mismatch", Toast.LENGTH_SHORT).show();
        }
    }
}
