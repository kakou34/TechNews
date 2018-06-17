package com.example.android.technews;

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

public class NewsUtils {

    public static final String TAG_LOG = NewsUtils.class.getName();


    //create a private constructor to this class because it contains only static helper methods
    // i.e it should not be instanciated
    private NewsUtils(){}


    //Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG_LOG, "Error with creating URL ", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG_LOG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG_LOG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //converting an input stream to a string using a string builder
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

    //extract JSON object
    public static List<News> extractResultsFromJSON(String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }


        // Create an empty ArrayList that we can start adding News to
        List<News> news = new ArrayList<>();


        try {
            //parse the response given by the String JsonResponse
            // build up a list of News objects.

            JSONObject jsonRootObject = new JSONObject(jsonResponse);
            JSONObject response = jsonRootObject.getJSONObject("response");
            //extract "results" Array
            JSONArray results = response.optJSONArray("results");
            //looping through the results Array
            for(int i=0; i < results.length(); i++){
                //getting the News object at position i
                JSONObject newsObj = results.getJSONObject(i);
                //extracting fields
                String title = newsObj.getString("webTitle");
                String date = newsObj.getString("webPublicationDate");
                String section = newsObj.getString("sectionName");
                String url = newsObj.getString("webUrl");
                String author = "Unknown";

                JSONArray tags = newsObj.optJSONArray("tags");
                if(tags != null && tags.length()!=0){
                    JSONObject currentTag = tags.getJSONObject(0);
                    author = currentTag.getString("webTitle");

                }


                //creating a News object with parameters webTitle, section, date and url
                News new1 = new News(title,section, date ,url,author);
                //adding the news object to the ArrayList
                news.add(new1);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }

        // Return the list of News
        return news;
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("LOG_TAG", "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractResultsFromJSON(jsonResponse);

        // Return the list of {@link News}s
        return news;
    }
}
