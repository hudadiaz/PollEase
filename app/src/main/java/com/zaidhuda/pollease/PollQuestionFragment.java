package com.zaidhuda.pollease;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class PollQuestionFragment extends Fragment {
    private static final String POLL = "poll";
    private static final String USER = "user";
    private String SELECTION_URL;

    private Poll poll;
    private User user;
    private int selectedChoiceID;
    private int previousChoice;
    private View view;

    private OnFragmentInteractionListener mListener;

    public PollQuestionFragment() {
    }

    public static PollQuestionFragment newInstance(Poll poll, User user) {
        PollQuestionFragment fragment = new PollQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(POLL, poll);
        args.putSerializable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            poll = (Poll) getArguments().getSerializable(POLL);
            user = (User) getArguments().getSerializable(USER);
        }
        SELECTION_URL = getResources().getString(R.string.selection_url).replace(":poll_id", String.valueOf(poll.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_poll_question, container, false);
        ((TextView) view.findViewById(R.id.question_TEXTVIEW)).setText(poll.getQuestion());
        view.findViewById(R.id.submitChoice_BUTTON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitChoice();
            }
        });
        buildChoices(poll);
        return view;
    }

    public void buildChoices(Poll poll) {
        final Context ctx = this.getActivity();
        RadioGroup rg = new RadioGroup(ctx);
        for(Choice choice : poll.getChoices()) {
            ChoiceRadioButton crb = new ChoiceRadioButton(ctx);
            crb.setChoiceID(choice.getId());
            crb.setText(choice.getAnswer());
            crb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rg.addView(crb);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (view != null) {
                        view.findViewById(R.id.submitChoice_BUTTON).setEnabled(true);
                        selectedChoiceID = ((ChoiceRadioButton) view.findViewById(group.getCheckedRadioButtonId())).getChoiceID();
                    }
                }
            });
        }
        if (view != null) {
            ((LinearLayout) view.findViewById(R.id.choices_CONTAINER)).addView(rg);
        }
    }

    public void submitChoice() {
        POSTAnswer task = new POSTAnswer();
        task.execute();
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
        void showResult(int selectedChoiceID, int previousChoice);
    }

    private class POSTAnswer extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(SELECTION_URL.replace(":id", String.valueOf(selectedChoiceID)));
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
                Toast.makeText(getActivity().getApplicationContext(), "Error..." + e.toString(), Toast.LENGTH_LONG).show();
            }
            return answer;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "", "Submitting vote, please wait", false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (mListener != null) {
                mListener.showResult(selectedChoiceID, previousChoice);
            }
        }
    }
}
