package com.example.android.newsfeed;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link ArticleAdapter} knows how to create a list item layout for each article
 * in the data source (a list of {@link Article} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {

    /**
     * Constructs a new {@link ArticleAdapter}.
     *
     * @param context  of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */
    public ArticleAdapter(Activity context, List<Article> articles) {
        super(context, 0, articles);
    }

    /**
     * Returns a list item view that displays information about the article at the given position
     * in the list of articles.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        Article currentArticle = getItem(position);

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (convertView == null) {
            // inflate the layout
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            // We'll set up the ViewHolder
            holder.titleTextView = convertView.findViewById(R.id.title);
            holder.authorTextView = convertView.findViewById(R.id.author);
            holder.sectionTextView = convertView.findViewById(R.id.section);
            holder.dateTextView = convertView.findViewById(R.id.date);
            holder.timeTextView = convertView.findViewById(R.id.time);

            // store the holder with the view.
            convertView.setTag(holder);

        } else {
            // Avoid calling findViewById() on resource every time
            // just use the viewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the title string for the current Article object
        String title = currentArticle.getTitle();
        // Get the author string for the current Article object
        String author = currentArticle.getAuthor();
        author = "Author: " + author;
        // Get the section name for the current Article object
        String section = currentArticle.getSection();
        section = "Section: " + section;
        // Get the date and time string for the current Article object
        String dateAndTime = currentArticle.getTime();

        // Split the iso-8601 DateTime from the Guardian into date and time
        String[] dateArray = dateAndTime.split("T");

        // The date part of the string
        String date = dateArray[0];
        // The time part of the string after removing the seconds and Z and adding time zone string
        String time = dateArray[1].substring(0, dateArray[1].length() - 4) + " UTC";

        holder.titleTextView.setText(title);
        holder.authorTextView.setText(author);
        holder.sectionTextView.setText(section);
        holder.dateTextView.setText(date);
        holder.timeTextView.setText(time);

        return convertView;
    }

    // Initialize ViewHolder views
    static class ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        TextView sectionTextView;
        TextView dateTextView;
        TextView timeTextView;
    }
}
