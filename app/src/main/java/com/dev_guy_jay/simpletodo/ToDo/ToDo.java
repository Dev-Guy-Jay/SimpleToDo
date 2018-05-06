package com.dev_guy_jay.simpletodo.ToDo;

public class ToDo {

    String mTitle;
    String mDescription;
    String mDateCreated;
    String mReminderDate;
    String mReminderTime;

    public ToDo(String title, String description, String dateCreated, String reminderDate, String reminderTime){
        mTitle = title;
        mDescription = description;
        mDateCreated = dateCreated;
        mReminderDate = reminderDate;
        mReminderTime = reminderTime;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getDateCreated() {
        return mDateCreated;
    }

    public String getReminderDate(){return mReminderDate;}

    public String getReminderTime() {return mReminderTime;}
}
