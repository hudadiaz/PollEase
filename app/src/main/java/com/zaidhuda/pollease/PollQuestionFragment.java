package com.zaidhuda.pollease;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PollQuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PollQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PollQuestionFragment extends Fragment {
    private static final String POLL = "poll";

    private Poll poll;
    private int selectedChoiceID;
    private View view;

    private OnFragmentInteractionListener mListener;

    public static PollQuestionFragment newInstance(Poll poll) {
        PollQuestionFragment fragment = new PollQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(POLL, poll);
        fragment.setArguments(args);
        return fragment;
    }

    public PollQuestionFragment() {
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
            crb.setChoiceID(choice.getID());
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
                    Toast.makeText(ctx, "Selected choice is " + selectedChoiceID, Toast.LENGTH_LONG);

                }
            });
        }
        if (view != null) {
            ((LinearLayout) view.findViewById(R.id.choices_CONTAINER)).addView(rg);
        }
    }

    public void submitChoice() {
        if (mListener != null) {
            mListener.submitChoice(selectedChoiceID);
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
        public void submitChoice(int selectedChoiceID);
    }
}
