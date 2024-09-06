package com.novatronrehberim.models;

public class TestResults {
    private int score;
    private int[] questionsPerType = new int[21];

    public int getScore(){
        return this.score;
    }

    public int[] getQuestionsPerType(){
        return this.questionsPerType;
    }

    public void setScore(int score){
        this.score = score;
    }

    public void setQuestionsPerType(int[] questionsPerType){
        this.questionsPerType  = questionsPerType;
    }
}
