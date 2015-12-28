package com.zaidhuda.pollease.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.fragments.PollEditAnswerFragment;
import com.zaidhuda.pollease.fragments.PollEditCreateFragment;
import com.zaidhuda.pollease.fragments.PollEditPasswordFragment;
import com.zaidhuda.pollease.helpers.PollDataSource;
import com.zaidhuda.pollease.objects.Poll;

public class PollEditActivity extends AppCompatActivity implements PollEditCreateFragment.OnFragmentInteractionListener,
        PollEditAnswerFragment.OnFragmentInteractionListener, PollEditPasswordFragment.OnFragmentInteractionListener {
    private Poll poll;
    private FragmentManager fragmentManager;
    private PollDataSource pollDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        poll = (Poll) getIntent().getSerializableExtra("poll");
        pollDataSource = new PollDataSource(this);

        if (poll != null) {
            pollDataSource.open();
            try {
                poll.setPassword(pollDataSource.getPoll(poll.getId()).getPassword());
            } catch (SQLException e) {
                System.out.println("cannot get poll");
            }
            displayPollPasswordFragment();
        }
        else
            displayPollCreateFragment();
    }

    public void displayPollCreateFragment() {
        fragmentManager = getFragmentManager();
        PollEditCreateFragment pollEditCreateFragment = PollEditCreateFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.edit_poll_primary_fragment, pollEditCreateFragment)
                .addToBackStack(null)
                .commit();
    }

    private void displayPollPasswordFragment() {
        fragmentManager = getFragmentManager();
        PollEditPasswordFragment pollEditPasswordFragment = PollEditPasswordFragment.newInstance(poll);
        fragmentManager.beginTransaction()
                .replace(R.id.edit_poll_primary_fragment, pollEditPasswordFragment)
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
    public void onReceivePoll(Poll poll, boolean remember) {
        this.poll = poll;
        if (remember) {
            pollDataSource.open();
            try {
                pollDataSource.updatePoll(poll);
            } catch (SQLException e) {
                Log.d("DB", "Can'y update poll");
            }
            pollDataSource.close();
        }
        displayPollEditFragment(poll);
    }

    @Override
    public void done() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPasswordAccepted(String password, boolean remember) {
        poll.setPassword(password);
        if (remember) {
            pollDataSource.open();
            try {
                pollDataSource.updatePoll(poll);
            } catch (SQLException e) {
                Log.d("DB", "Can'y update poll");
            }
            pollDataSource.close();
        }
        displayPollEditFragment(poll);
    }
}
