package com.dogzz.pim.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Base async task for download data
 * Created by dogzz on 14.07.2016.
 */
public abstract class DownloadTask extends AsyncTask<String, Void, Integer> {

    protected String resultMessage;
    @Override
    protected abstract Integer doInBackground(String... urls);

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected abstract void onPostExecute(Integer result);

    protected Integer downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 50000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000 /* milliseconds */);
            conn.setConnectTimeout(30000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Network", "The response is: " + response);
            InputStream it = new BufferedInputStream(conn.getInputStream());
            InputStreamReader read = new InputStreamReader(it);
            BufferedReader buff = new BufferedReader(read);
            StringBuilder dta = new StringBuilder();
            String chunks;
            while ((chunks = buff.readLine()) != null) {
                dta.append(chunks);
            }
//            is = conn.getInputStream();
            resultMessage = dta.toString();
            // Convert the InputStream into a string
            return 1;
        } catch (Exception e) {
            resultMessage = "Error during downloading source" + e.getMessage();
            Log.e("Network", resultMessage);
            return 0;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e("Network", e.getMessage());
                    resultMessage = e.getMessage();
                }
            }
        }
    }
}
