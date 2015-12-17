package com.zaidhuda.pollease;

import java.util.ArrayList;

/**
 * Created by Zaid on 17/12/2015.
 */
public class Poll {
    private final int ID;
    private String question;
    private ArrayList<Choice> choices;


    public Poll(int id) {
        ID = id;
        choices = new ArrayList<>();
    }

    public int getID() {
        return ID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<Choice> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<Choice> choices) {
        this.choices = choices;
    }

    public void addChoices(Choice choice) {
        this.choices.add(choice);
    }
}
