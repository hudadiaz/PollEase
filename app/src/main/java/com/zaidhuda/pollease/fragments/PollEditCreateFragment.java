package com.zaidhuda.pollease.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zaidhuda.pollease.AsyncTasks.POSTPoll;
import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.objects.Poll;

public class PollEditCreateFragment extends Fragment implements POSTPoll.OnPOSTPollListener {
    private View view;
    private String question, password;
    private EditText questionET, passwordET;
    private OnFragmentInteractionListener mListener;
    private POSTPoll postPoll;
    private TextView counter;
    private Button submit;
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        String max = "/255";

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String temp = s.length() + max;
            counter.setText(temp);
            if (s.length() > Integer.parseInt(max.substring(1, max.length()))) {
                counter.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
                submit.setEnabled(false);
            } else {
                counter.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
                submit.setEnabled(true);
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };
    private CheckBox rememberPass;

    public PollEditCreateFragment() {
    }

    public static PollEditCreateFragment newInstance() {
        return new PollEditCreateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.new_poll_title);
        view = inflater.inflate(R.layout.fragment_poll_edit_create, container, false);
        questionET = (EditText) view.findViewById(R.id.question_editText);
        questionET.addTextChangedListener(mTextEditorWatcher);
        passwordET = (EditText) view.findViewById(R.id.password_editText);
        counter = (TextView) view.findViewById(R.id.text_counter);
        rememberPass = (CheckBox) view.findViewById(R.id.remember_password_check);
        submit = (Button) view.findViewById(R.id.poll_create_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question = questionET.getText().toString();
                password = passwordET.getText().toString();
                if (question.length() > 0) {
                    submitPoll(question, password);
                } else
                    Toast.makeText(getActivity(), "Enter a question!", Toast.LENGTH_SHORT).show();
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

    public void submitPoll(String question, String password) {
        postPoll = new POSTPoll(question, password, this);
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

    @Override
    public void onPollCreated(Poll poll) {
        poll.setPassword(password);
        if (mListener != null)
            mListener.onReceivePoll(poll, rememberPass.isChecked());
        postPoll.detachListener();
    }

    public interface OnFragmentInteractionListener {
        void onReceivePoll(Poll poll, boolean remember);
    }
}
