package com.example.zen.taskchecker.Modal;

public class Data {
    private String note;
    private String date;
    private String title,id;

    Data()
    {

    }

    public Data(String note, String date, String title, String id) {
        this.note = note;
        this.date = date;
        this.title = title;
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
