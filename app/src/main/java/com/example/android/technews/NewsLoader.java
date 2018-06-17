package com.example.android.technews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class NewsLoader extends AsyncTaskLoader<List<News>> {

        /** Query URL */
        private String mUrl;

       //construct news loader
        public NewsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        /**
         * This is on a background thread.
         */
        @Override
        public List<News> loadInBackground() {
            if (mUrl == null) {
                return null;
            }

            // Perform the network request, parse the response, and extract a list of news.
            List<News> news = NewsUtils.fetchNewsData(mUrl);
            return news;
        }
}
