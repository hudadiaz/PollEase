package com.zaidhuda.pollease;

import java.io.Serializable;

/**
 * Created by Zaid on 17/12/2015.
 */
public class Choice implements Serializable {
    private final int ID;
    private String answer;
    private int vote_count;

    public Choice(int id) {
        ID = id;
    }

    public int getID() {
        return ID;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getVoteCount() {
        return vote_count;
    }

    public void setVoteCount(int count) {
        this.vote_count = count;
    }

    public void addVote() {
        vote_count++;
    }
}
