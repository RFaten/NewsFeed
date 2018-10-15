package com.example.android.newsfeed;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsfeed.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving article data from the Guardian.
 */
public class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    // The url connection read time in milliseconds
    private static final int readTimeoutInMilliseconds = 10000;

    // The url connection time out in milliseconds
    private static final int connectTimeoutInMilliseconds = 15000;

    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the url is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(readTimeoutInMilliseconds);
            urlConnection.setConnectTimeout(connectTimeoutInMilliseconds);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the articles json request.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */

    private static List<Article> extractFeatureFromJson(String articleJSONResponse) {

        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(articleJSONResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // build up a list of Article objects with the corresponding data.

            // Create a JSONObject from the JSON response string
            JSONObject root = new JSONObject(articleJSONResponse);

            JSONObject response = root.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features (or articles)
            JSONArray results = response.optJSONArray("results");

            // For each article in the articleArray, create an {@link Article} object
            for (int i = 0; i < results.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject article = results.getJSONObject(i);

                // Extract the value for the key called "webPublicationDate"
                String date = article.getString("webPublicationDate");
                // Extract the value for the key called "webTitle"
                String title = article.getString("webTitle");
                // Extract the value for the key called"sectionName"
                String section = article.getString("sectionName");
                // Extract the value for the key called "webUrl"
                String url = article.getString("webUrl");
                String author;

                // Extract the JSONArray associated with the key called "tags",
                // which represents a list of tags
                JSONArray tags = article.optJSONArray("tags");
                if (tags.length() > 0) {
                    // Get the first tag which represents the "contributor" tag
                    JSONObject contributor = tags.getJSONObject(0);

                    // Extract the value for the key called "webTitle"
                    author = contributor.getString("webTitle");
                } else {
                    author = "No Info";
                }

                // Create a new {@link Article} object with the title, author, date,
                // and url from the JSON response.
                Article articleObject = new Article(title, author, section, date, url);

                // Add the new {@link Article} to the list of article.
                articles.add(articleObject);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        return articles;
    }

    /**
     * Query the Guardian data set and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        // Return the list of {@link Article}s
        return extractFeatureFromJson(jsonResponse);
    }
}

