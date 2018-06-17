package com.example.android.technews;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    //public constructor to create a News adapter
    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        //get the news at the current position
        News currentNews = getItem(position);

        //set the text of the title text view to contain the news webTitle
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        String webTitle = currentNews.getWebTitle();
        titleTextView.setText(webTitle);

        //set the text of section textView to contain the section's name of the news.
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);
        String sectionName = currentNews.getSection();
        sectionTextView.setText(sectionName);

        //set the text of date textView to contain date of the news.
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        String dateStr = currentNews.getDate();
        dateTextView.setText(dateStr);

        //set the text of author textView to contain the author's name.
        //or show unknoun if no author is found
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        String authorStr = currentNews.getAuthor();
        authorTextView.setText(authorStr);

        // Return the whole list item layout (containing 3 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
