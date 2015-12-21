package com.zaidhuda.pollease;


import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PollEditAnswerFragment extends ListFragment implements POSTChoice.OnPOSTChoiceListener {
    private static final String POLL = "poll";
    private View view;
    private Poll poll;
    private String choice;
    private EditText choiceET;
    private OnFragmentInteractionListener mListener;
    private POSTChoice postChoice;
    private List<String> choices;

    public PollEditAnswerFragment() {
    }

    public static PollEditAnswerFragment newInstance(Poll poll) {
        PollEditAnswerFragment fragment = new PollEditAnswerFragment();
        Bundle args = new Bundle();
        args.putSerializable(POLL, poll);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            poll = (Poll) getArguments().getSerializable(POLL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_poll_edit, container, false);
        ((TextView) view.findViewById(R.id.question_TEXT)).setText(poll.getQuestion());

        choices = new ArrayList<>();
        for (Choice choice : poll.getChoices()) {
            choices.add(choice.getAnswer());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, choices);
        setListAdapter(adapter);

        choiceET = (EditText) view.findViewById(R.id.choice_editText);
        view.findViewById(R.id.choice_create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = choiceET.getText().toString();
                choiceET.setText("");
                if (choice.length() > 0) {
                    submitAnswer(choice);
                }
            }
        });

        return view;
    }

    public void submitAnswer(String choice) {
        postChoice = new POSTChoice(poll, choice, this);
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

    public void closePoll() {
        if (mListener != null)
            mListener.done();
    }

    @Override
    public void onCreateAnswer(Choice choice) {
        choices.add(choice.getAnswer());

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
//        adapter.add(choice.getAnswer());
        adapter.notifyDataSetChanged();
        poll.addChoices(choice);
    }

    public interface OnFragmentInteractionListener {
        void done();
    }
}
