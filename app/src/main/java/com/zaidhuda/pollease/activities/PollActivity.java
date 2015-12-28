package com.zaidhuda.pollease.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zaidhuda.pollease.AsyncTasks.GETPoll;
import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.fragments.PollQuestionFragment;
import com.zaidhuda.pollease.fragments.PollResultPieChartFragment;
import com.zaidhuda.pollease.objects.Poll;
import com.zaidhuda.pollease.objects.User;

public class PollActivity extends AppCompatActivity implements PollQuestionFragment.OnFragmentInteractionListener, GETPoll.OnGETPollListener {
    private String POLLS_URL;
    private String request_url = "", jsonResult;
    private FragmentManager fragmentManager;
    private Poll poll;
    private User user;
    private int selectedChoiceID;
    private int previousChoice;
    private GETPoll pollGetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        POLLS_URL = getResources().getString(R.string.polls_url);
        request_url = getIntent().getStringExtra("poll_url");
        user = (User) getIntent().getSerializableExtra("user");

        retrievePoll();
    }

    public void retrievePoll() {
        pollGetter = new GETPoll(request_url, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Intent intent = getIntent();
            finish();
            intent.putExtra("poll_url", request_url);
            intent.putExtra("user", user);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_pie_chart) {
            previousChoice = selectedChoiceID;
            displayPieChartFragment();
            return true;
        } else if (id == R.id.action_edit_poll) {
            Intent intent = new Intent(this, PollEditActivity.class);
            intent.putExtra("poll", poll);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_share_poll) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String webUrl = getString(R.string.web_poll_url).replace(":poll_id", String.valueOf(poll.getId()));
            sendIntent.putExtra(Intent.EXTRA_TEXT, poll.getQuestion() + "\n\n" + webUrl);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
//        else if (id == R.id.action_bar_chart) {
//            displayBarChartFragment();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void displayPollQuestionFragment() {
        fragmentManager = getFragmentManager();
        PollQuestionFragment pollQuestion = PollQuestionFragment.newInstance(poll, user);
        fragmentManager.beginTransaction()
                .add(R.id.PollPrimaryFragment, pollQuestion)
                .addToBackStack(null)
                .commit();
    }

    public void displayPieChartFragment() {
        PollResultPieChartFragment pollResultFragment = PollResultPieChartFragment.newInstance(poll, selectedChoiceID, previousChoice);
        fragmentManager.beginTransaction()
                .replace(R.id.PollPrimaryFragment, pollResultFragment)
                .addToBackStack(null)
                .commit();
    }

//    public void displayBarChartFragment() {
//        PollResultBarChartFragment pollResultFragment = PollResultBarChartFragment.newInstance(poll, selectedChoiceID);
//        fragmentManager.beginTransaction()
//                .replace(R.id.PollPrimaryFragment, pollResultFragment)
//                .addToBackStack(null)
//                .commit();
//    }

    @Override
    public void showResult(int selectedChoiceID, int previousChoice) {
        this.selectedChoiceID = selectedChoiceID;
        this.previousChoice = previousChoice;
        displayPieChartFragment();
    }

    @Override
    public void onPollRetrieve(Poll poll) {
        this.poll = poll;
        setTitle(poll.getPollName());
        poll.setUrl(request_url);
        displayPollQuestionFragment();
        pollGetter.detachListener();
    }
}
