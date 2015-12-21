package com.zaidhuda.pollease;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class PollEditActivity extends AppCompatActivity implements PollCreateFragment.OnFragmentInteractionListener, PollEditAnswerFragment.OnFragmentInteractionListener {
    private Poll poll;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        displayPollCreateFragment();
    }

    public void displayPollCreateFragment() {
        fragmentManager = getFragmentManager();
        PollCreateFragment pollCreateFragment = PollCreateFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.edit_poll_primary_fragment, pollCreateFragment)
                .addToBackStack(null)
                .commit();
    }

    public void displayPollEditFragment(Poll poll) {
        fragmentManager = getFragmentManager();
        PollEditAnswerFragment pollEditAnswerFragment = PollEditAnswerFragment.newInstance(poll);
        fragmentManager.beginTransaction()
                .replace(R.id.edit_poll_primary_fragment, pollEditAnswerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onReceivePoll(Poll poll) {
        this.poll = poll;
        displayPollEditFragment(poll);
    }

    @Override
    public void done() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
