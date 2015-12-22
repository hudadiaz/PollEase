package com.zaidhuda.pollease.custom.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zaidhuda.pollease.R;
import com.zaidhuda.pollease.objects.Poll;

import java.util.List;

public class PollListView extends BaseAdapter {
    private Context ctx;
    private List<Poll> itemList;

    public PollListView(Context ctx, List<Poll> itemList) {
        this.ctx = ctx;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public Object getItem(int pos) {
        return itemList == null ? null : itemList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return itemList == null ? 0 : itemList.get(pos).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Poll item = itemList.get(position);
        View v = convertView;
        PollTextHolder pollTh = null;
        if (v == null) {
            LayoutInflater lInf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = lInf.inflate(R.layout.poll_list_view, null);
            TextView idTE = (TextView) v.findViewById(R.id.poll_number);
            TextView questionTE = (TextView) v.findViewById(R.id.poll_question);
            pollTh = new PollTextHolder();
            pollTh.number = idTE;
            pollTh.question = questionTE;
            v.setTag(pollTh);
        } else
            pollTh = (PollTextHolder) v.getTag();
        pollTh.number.setText("#" + String.valueOf(item.getId()));
        pollTh.question.setText(item.getQuestion());
        return v;
    }

    static class PollTextHolder {
        TextView number;
        TextView question;
    }
}
