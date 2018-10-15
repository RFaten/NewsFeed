package com.example.android.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class ArticleAsyncTask extends AsyncTaskLoader<List<Article>> {

    /**
     * Query URL
     */
    private String mUrl;

    public ArticleAsyncTask(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {

        if (mUrl == null) {
            return null;
        }

        // Perform the HTTP request for article data and process the response.
        return QueryUtils.fetchArticleData(mUrl);
    }
}
