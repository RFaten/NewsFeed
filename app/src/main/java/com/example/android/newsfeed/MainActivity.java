package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    /**
     * URL to query the Guardian data set for article information
     */
    private static final String Guardian_REQUEST_URL = "https://content.guardianapis.com/search?&format=json" +
            "&page-size=20&show-tags=contributor&api-key=0181325f-1a41-4be9-94e2-6ccd47132dff";
    /**
     * Constant value for the article loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int ARTICLE_LOADER_ID = 1;
    /**
     * Adapter for the list of articles
     */
    ArticleAdapter adapter;
    // TextView to display the empty state(No internet or No data to display)
    TextView emptyStateTextView;

    // Loading progressBar to display while waiting to receive data from the internet
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        final ListView listView = findViewById(R.id.list);

        // Create a new adapter that takes an empty list of articles as input
        adapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the list view
        listView.setAdapter(adapter);

        emptyStateTextView = findViewById(R.id.empty_state);
        listView.setEmptyView(emptyStateTextView);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Find the current article that was clicked on
                Article currentArticle = adapter.getItem(i);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getUrl());
                // Create a new intent to view the article URI
                Intent intent = new Intent(Intent.ACTION_VIEW, articleUri);
                // Send the intent to launch a new activity
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String section = sharedPrefs.getString(getString(R.string.settings_section_key), "");
        section = section.replaceAll(" ", "").toLowerCase();

        String author = sharedPrefs.getString(getString(R.string.settings_author_key), "");
        author = author.replaceAll(" ", "").toLowerCase();

        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key), "");

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(Guardian_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        if (!"".equals(section)) {
            // Append query parameter and its value. For example, the `section=film`
            uriBuilder.appendQueryParameter("section", section);
        }

        if (!"".equals(author)) {
            author = "profile/" + author;
            // Append query parameter and its value. For example, the `tag=profile/peterbradshaw`
            uriBuilder.appendQueryParameter("tag", author);
        }

        if ("newest".equals(orderBy)) {
            // Append query parameter and its value. For example, the `order-by=newest`
            uriBuilder.appendQueryParameter("order-by", orderBy);
        }

        if ("true".equals(orderBy)) {
            // Append query parameter and its value. For example, the `show-most-viewed=true`
            uriBuilder.appendQueryParameter("show-most-viewed", orderBy);
            uriBuilder.appendQueryParameter("order-by", getString(R.string.order_by_relevance));
        }

        // Return the completed uri `https://content.guardianapis.com/search?format=json&order-by=newest&page-size=20&show-tags=contributor&api-key=0181325f-1a41-4be9-94e2-6ccd47132dff&section=film
        return new ArticleAsyncTask(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Clear the adapter of previous article data
        adapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            adapter.addAll(articles);
        }

        emptyStateTextView.setText(R.string.no_data_to_display);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();

    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
