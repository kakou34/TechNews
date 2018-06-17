     package com.example.android.technews;

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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

     public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

         // Give an ID to the Loader
         private static final int NEWS_LOADER_ID = 1;

         //Create a String for the URL to the API
         private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?q=technology&api-key=test";

         //declare a news adapter
         private NewsAdapter newsAdapter;

         private TextView emptyStateTextView;

         @Override
         public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

             SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

             String pageSize = sharedPreferences.getString(getString(R.string.settings_page_size_key),getString(R.string.settings_page_size_default));
             String orderBy  = sharedPreferences.getString(
                     getString(R.string.settings_order_by_key),
                     getString(R.string.settings_order_by_default)
             );
             // parse breaks apart the URI string that's passed into its parameter
             Uri baseUri = Uri.parse(NEWS_REQUEST_URL);

             //prepare the base URI to query params to it
             Uri.Builder uriBuilder = baseUri.buildUpon();

             // Append query parameter and its value

             uriBuilder.appendQueryParameter("pageSize",pageSize);
             uriBuilder.appendQueryParameter("orderBy",orderBy);
             uriBuilder.appendQueryParameter("userTier","developer");
             uriBuilder.appendQueryParameter("sectionId","technology");
             uriBuilder.appendQueryParameter("show-tags","contributor");


             // Create a new loader for the given URL
             return new NewsLoader(this, uriBuilder.toString());
         }

         @Override
         public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
             // Hide loading indicator because the data has been loaded
             View loadingIndicator = findViewById(R.id.loading_indicator);
             loadingIndicator.setVisibility(View.GONE);

             // Set empty state text to display "No News Found!"
             emptyStateTextView.setText(R.string.no_news);

             // Clear the adapter of previous news data
             newsAdapter.clear();

             // If there is a valid list of {@link News}s, then add them to the adapter's
             // data set. This will trigger the ListView to update.
             if (news != null && !news.isEmpty()) {
                newsAdapter.addAll(news);
             }
         }
         @Override
         public void onLoaderReset(Loader<List<News>> loader) {
             // Loader reset, so we can clear out our existing data.
             newsAdapter.clear();
         }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(newsAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news item.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news item that was clicked on
                News currentNews = newsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet);
        }
    }
         @Override
         // This method initialize the contents of the Activity's options menu
         public boolean onCreateOptionsMenu(Menu menu) {
             getMenuInflater().inflate(R.menu.main, menu);
             return true;
         }

         @Override
         // This method is called whenever an item in the options menu is selected.
         public boolean onOptionsItemSelected(MenuItem item) {
             int id = item.getItemId();
             if (id == R.id.action_settings) {
                 Intent settingsIntent = new Intent(this, SettingsActivity.class);
                 startActivity(settingsIntent);
                 return true;
             }
             return super.onOptionsItemSelected(item);
         }
}
