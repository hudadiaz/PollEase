package com.zaidhuda.pollease;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class PollQuestionFragment extends Fragment implements POSTSelection.OnPOSTSelectionListener {
    private static final String POLL = "poll";
    private static final String USER = "user";
    POSTSelection postSelection;
    private String SELECTION_URL;
    private Poll poll;
    private User user;
    private int selectedChoiceID;
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
        postSelection = new POSTSelection(user, poll, selectedChoiceID, this);
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
    public void onSelectionPosted(int selectedChoiceID, int previousChoice) {
        if (mListener != null) {
            mListener.showResult(selectedChoiceID, previousChoice);
            postSelection.detachListener();
        }
    }

    public interface OnFragmentInteractionListener {
        void showResult(int selectedChoiceID, int previousChoice);
    }
}
