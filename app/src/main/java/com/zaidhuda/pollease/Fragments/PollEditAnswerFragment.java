package com.zaidhuda.pollease.fragments;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zaidhuda.pollease.AsyncTasks.DELETEAnswer;
import com.zaidhuda.pollease.AsyncTasks.POSTChoice;
import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.objects.Choice;
import com.zaidhuda.pollease.objects.Poll;

import java.util.ArrayList;
import java.util.List;


public class PollEditAnswerFragment extends ListFragment implements POSTChoice.OnPOSTChoiceListener, DELETEAnswer.OnDELETEAnswerListener {
    private static final String POLL = "poll";
    private View view;
    private Poll poll;
    private String choice;
    private EditText choiceET;
    private OnFragmentInteractionListener mListener;
    private POSTChoice postChoice;
    private List<String> choices;
    private List<Integer> choicesId;
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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            poll = (Poll) getArguments().getSerializable(POLL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.poll_edit_answers_title);
        view = inflater.inflate(R.layout.fragment_poll_edit_answers, container, false);
        ((TextView) view.findViewById(R.id.question_TEXT)).setText(poll.getQuestion());

        choices = new ArrayList<>();
        choicesId = new ArrayList<>();
        for (Choice choice : poll.getChoices()) {
            choices.add(choice.getAnswer());
            choicesId.add(choice.getId());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, choices);
        setListAdapter(adapter);

        choiceET = (EditText) view.findViewById(R.id.choice_editText);
        choiceET.addTextChangedListener(mTextEditorWatcher);
        counter = (TextView) view.findViewById(R.id.text_counter);
        submit = (Button) view.findViewById(R.id.choice_create_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = choiceET.getText().toString();
                if (choice.length() > 0) {
                    submitAnswer(choice);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_poll_edit_answers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.go_to_main) {
            getActivity().finish();
        }
        else if (id == R.id.action_share_poll) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, poll.getQuestion()+"\n\n"+poll.getUrl());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Answer")
                .setMessage("Do you really want to remove\n\n" + item + "\n\nfrom the poll options? This action is irreversible.")
                .setIcon(ContextCompat.getDrawable(this.getActivity(), R.drawable.ic_warning_black_48dp))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteAnswer(choicesId.get(position));
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void submitAnswer(String choice) {
        postChoice = new POSTChoice(poll, choice, this);
    }

    public void closePoll() {
        if (mListener != null)
            mListener.done();
    }

    @Override
    public void onAnswerDeleted(int choiceId) {
        int index = choicesId.indexOf(choiceId);
        choicesId.remove(index);
        choices.remove(index);
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
        adapter.notifyDataSetChanged();
        poll.removeChoice(choiceId);
    }

    public void deleteAnswer(int index) {
        new DELETEAnswer(poll, index, this);
    }

    @Override
    public void onCreateAnswer(Choice choice) {
        choiceET.setText("");
        choices.add(choice.getAnswer());
        choicesId.add(choice.getId());
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
        adapter.notifyDataSetChanged();
        poll.addChoices(choice);
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
        void done();
    }
}
