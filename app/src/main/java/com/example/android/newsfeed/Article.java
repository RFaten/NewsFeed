package com.example.android.newsfeed;

/**
 * An {@link Article} object contains information related to a single article.
 */
public class Article {

    /**
     * Title of the article
     */
    private String mTitle;

    /**
     * Author of the article
     */
    private String mAuthor;

    /**
     * The section of the article
     */
    private String mSection;

    /**
     * Time of the article
     */
    private String mTime;

    /**
     * Website URL of the article
     */
    private String mUrl;

    /**
     * Constructs a new {@link Article} object.
     *
     * @param title  is the title of the article
     * @param author is the author of the article
     * @param section is the section of the article
     * @param time   is the combined date and time of publication
     * @param url    is the website URL to find more details about the article
     */
    public Article(String title, String author, String section, String time, String url) {
        mTitle = title;
        mAuthor = author;
        mSection = section;
        mTime = time;
        mUrl = url;
    }

    /**
     * Returns the title of the article.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the author's of the article name.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the section name of the article.
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Returns the date and time of the article.
     */
    public String getTime() {
        return mTime;
    }

    /**
     * Returns the website URL to find more information about the article.
     */
    public String getUrl() {
        return mUrl;
    }

}

