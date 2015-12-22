package com.zaidhuda.pollease.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.objects.Choice;
import com.zaidhuda.pollease.objects.Poll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PollResultBarChartFragment extends Fragment {
    private static final String POLL = "poll";
    private static final String SELECTEDCHOICEID = "selectedChoiceID";
    protected List<Integer> colors = Arrays.asList(Color.rgb(236, 64, 122), Color.rgb(92, 107, 192), Color.rgb(38, 198, 218),
            Color.rgb(156, 204, 101), Color.rgb(255, 202, 40), Color.rgb(141, 110, 99),
            Color.rgb(120, 144, 156), Color.rgb(239, 83, 80), Color.rgb(126, 87, 194),
            Color.rgb(41, 182, 246), Color.rgb(102, 187, 106), Color.rgb(255, 238, 88),
            Color.rgb(255, 112, 67), Color.rgb(171, 71, 188), Color.rgb(66, 165, 245),
            Color.rgb(38, 166, 154), Color.rgb(212, 225, 87), Color.rgb(255, 167, 38), Color.rgb(189, 189, 189));
    private int selectedChoiceID;
    private Poll poll;
//    private Typeface tf;

    public PollResultBarChartFragment() {
        // Required empty public constructor
    }

    public static PollResultBarChartFragment newInstance(Poll poll, int selectedChoiceID) {
        PollResultBarChartFragment fragment = new PollResultBarChartFragment();
        Bundle args = new Bundle();
        args.putSerializable(POLL, poll);
        args.putInt(SELECTEDCHOICEID, selectedChoiceID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            poll = (Poll) getArguments().getSerializable(POLL);
            selectedChoiceID = getArguments().getInt(SELECTEDCHOICEID);
//            poll.addVoteTo(selectedChoiceID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_poll_result_bar_chart, container, false);
        ((TextView) v.findViewById(R.id.question_TEXT)).setText(poll.getQuestion());

        v.findViewById(R.id.barChart).invalidate();
        builBarChart(v);

        return v;
    }

    public void builBarChart(View v) {
        // create a new chart object
        BarChart mChart = (BarChart) v.findViewById(R.id.barChart);
        mChart.setDescription("");

        ArrayList<Choice> choices = new ArrayList<>();

        for (Choice choice : poll.getChoices()) {
            if (choice.getVoteCount() > 0)
                choices.add(choice);
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for (int i = 0; i < choices.size(); i++) {
            xVals.add(choices.get(i).getAnswer());
            entries.add(new BarEntry(choices.get(i).getVoteCount(), i));
        }


        final BarDataSet setSurv1 = new BarDataSet(entries, "Difficulty level");

        ArrayList<BarDataSet> survData = new ArrayList<BarDataSet>() { //# of bars
            {
                add(setSurv1);
            }
        };

        BarData dataSurv = new BarData(xVals, survData);

        mChart.setData(dataSurv);
    }
}
