package com.example.android.technews;


public class News {

    private String webTitle;
    private String section;
    private String date;
    private String url;
    private String author;

    public News(String webTitle, String section, String date, String url, String author) {
        this.date = date;
        this.section = section;
        this.webTitle = webTitle;
        this.url = url;
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public String getSection() {
        return section;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getAuthor() {
        return author;
    }
}
