package com.zaidhuda.pollease;

/**
 * Created by Zaid on 17/12/2015.
 */
public class Choice {
    private final int ID;
    private String answer;
    private int count;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
