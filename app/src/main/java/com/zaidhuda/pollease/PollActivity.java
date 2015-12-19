package com.zaidhuda.pollease;

import android.app.FragmentManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PollActivity extends AppCompatActivity implements PollQuestionFragment.OnFragmentInteractionListener {
    private FragmentManager fragmentManager;
    private Poll poll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        poll = (Poll) getIntent().getSerializableExtra("poll");

        fragmentManager = getFragmentManager();
        PollQuestionFragment pollQuestion = PollQuestionFragment.newInstance(poll);
        fragmentManager.beginTransaction()
                .replace(R.id.PollPrimaryFragment, pollQuestion)
                .addToBackStack("Question")
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void submitChoice(int selectedChoiceID) {
        PollResultFragment pollResultFragment = PollResultFragment.newInstance(poll, selectedChoiceID);
        fragmentManager.beginTransaction()
                .replace(R.id.PollPrimaryFragment, pollResultFragment)
                .addToBackStack("Result")
                .commit();
    }
}
