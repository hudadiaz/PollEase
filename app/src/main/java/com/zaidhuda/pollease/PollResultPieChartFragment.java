package com.zaidhuda.pollease;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollResultPieChartFragment extends Fragment {
    private static final String POLL = "poll";
    private static final String SELECTEDCHOICEID = "selectedChoiceID";
    private static final String PREVIOUSCHOICE = "previousChoice";
    protected List<Integer> colors = Arrays.asList(Color.rgb(236, 64, 122), Color.rgb(92, 107, 192), Color.rgb(38, 198, 218),
            Color.rgb(156, 204, 101), Color.rgb(255, 202, 40), Color.rgb(141, 110, 99),
            Color.rgb(120, 144, 156), Color.rgb(239, 83, 80), Color.rgb(126, 87, 194),
            Color.rgb(41, 182, 246), Color.rgb(102, 187, 106), Color.rgb(255, 238, 88),
            Color.rgb(255, 112, 67), Color.rgb(171, 71, 188), Color.rgb(66, 165, 245),
            Color.rgb(38, 166, 154), Color.rgb(212, 225, 87), Color.rgb(255, 167, 38), Color.rgb(189, 189, 189) );
    private int selectedChoiceID;
    private int previousChoice;
    private Poll poll;
//    private Typeface tf;

    public PollResultPieChartFragment() {
        // Required empty public constructor
    }

    public static PollResultPieChartFragment newInstance(Poll poll, int selectedChoiceID, int previousChoice) {
        PollResultPieChartFragment fragment = new PollResultPieChartFragment();
        Bundle args = new Bundle();
        args.putSerializable(POLL, poll);
        args.putInt(SELECTEDCHOICEID, selectedChoiceID);
        args.putInt(PREVIOUSCHOICE, previousChoice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            poll = (Poll) getArguments().getSerializable(POLL);
            selectedChoiceID = getArguments().getInt(SELECTEDCHOICEID);
            previousChoice = getArguments().getInt(PREVIOUSCHOICE);

            poll.updateVoteCount(selectedChoiceID, previousChoice);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_poll_result_pie_chart, container, false);
        ((TextView) v.findViewById(R.id.question_TEXT)).setText(poll.getQuestion());

        buildPieChart(v);

        return v;
    }

    protected void buildPieChart(View v) {
        PieChart mChart;
        mChart = (PieChart) v.findViewById(R.id.pieChart);
        mChart.setDescription("");
//        tf = Typeface.createFromAsset(getActivity().getBaseContext().getAssets(), "opensanslight.ttf");

//        mChart.setCenterTextTypeface(tf);
        mChart.setCenterText(generateCenterText());
        mChart.setCenterTextSize(10f);
//        mChart.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(50f);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.PIECHART_CENTER);
        l.setEnabled(false);

        mChart.setData(generatePieData());
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    protected PieData generatePieData() {
        ArrayList<Choice> choices = new ArrayList<>();

        for (Choice choice : poll.getChoices()) {
            if (choice.getVoteCount() > 0)
                choices.add(choice);
        }

        ArrayList<Entry> entries1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for(int i = 0; i < choices.size(); i++) {
            xVals.add(choices.get(i).getAnswer());
            entries1.add(new Entry(((float) choices.get(i).getVoteCount()/poll.getVoteCasted()*100), i));
        }

//        Collections.shuffle(colors);

        PieDataSet ds1 = new PieDataSet(entries1, poll.getQuestion());
        ds1.setColors(colors);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);
        ds1.setValueFormatter(new PercentFormatter());
        PieData d = new PieData(xVals, ds1);

        return d;
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString(poll.getVoteCasted() + "\nresponded");
        s.setSpan(new RelativeSizeSpan(4f), 0, s.length()-9, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), s.length()-9, s.length(), 0);
        return s;
    }
}
