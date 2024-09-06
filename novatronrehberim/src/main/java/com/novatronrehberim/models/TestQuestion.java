package com.novatronrehberim.models;

public class TestQuestion {
    private int id;
    private int type;
    private int result;

    public int getId(){
        return this.id;
    }

    public int getType(){
        return this.type;
    }

    public int getResult(){
        return this.result;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setResult(int result){
        this.result = result;
    }
}
