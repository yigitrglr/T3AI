package com.novatronrehberim.models;

public class AIPrompt {
    private String content;
    private String role;
    public String category;

    public String getContent(){
        return this.content;
    }

    public String getRole(){
        return this.role;
    }

    public String getCategory(){
        return this.category;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setRole(String role){
        this.role = role;
    }

    public void setCategory(String category){
        this.category = category;
    }

}
