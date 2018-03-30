package edu.umsl.quizlet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by landon on 3/2/17.
 * <p>
 * This fragment performs a download task in the background and returns a string from the task. I
 * will add POST functionality in the future, but it is not implemented yet. void is returned if
 * there is a 404 error or something other than a success response code.
 * <p>
 * To use this fragment, implement the HttpWorkerFragment.MainFragmentListener interface, which
 * consists of a single dataDownloadComplete function that takes in a string for the text that the
 * http request returned. Don't forget to setListener.
 */

public class HttpWorkerFragment extends android.support.v4.app.Fragment {
    private Context mContext;
    public static final String HTTP_WORKER_FRAG = "HTTP_WORKER_FRAGMENT";

    private WeakReference<MainFragmentListener> mlistener;

    interface MainFragmentListener {
        void dataDownloadComplete(String downloadText);
    }

    public void setListener(MainFragmentListener mlistener) {
        this.mlistener = new WeakReference<>(mlistener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    // Pass in downloadURL and requestMethod as strings
    void startDownloadTask(String downloadURL, String requestMethod, String... params) {
        if (mContext == null) Log.e("httpWorkerFragment", "mContext is null");
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String[] preParams;
            if (params.length > 0) {
                preParams = new String[params.length + 2];
                preParams[0] = downloadURL;
                preParams[1] = requestMethod;
                System.arraycopy(params, 0, preParams, 2, params.length);
            } else {
                preParams = new String[2];
                preParams[0] = downloadURL;
                preParams[1] = requestMethod;
            }

            new DownloadTask().execute(preParams);
        }
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return downloadFromUrl(params[0], params[1], Arrays.copyOfRange(params, 2, params.length));
        }

        @Override
        protected void onPostExecute(String result) {
            if (mlistener != null) {
                mlistener.get().dataDownloadComplete(result);
            }
        }
    }

    private String downloadFromUrl(String requestUrl, String requestMethod, String... params) {
        InputStream inputStream = null;

        try {
            HttpURLConnection connection;
            try {
                URL url = new URL(requestUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod(requestMethod);
                if (requestMethod.equals("POST")) {
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    String data = "";
                    for (int i = 0; i < params.length; i++) {
                        data += params[i];
                    }
                    Log.e("HTTPPOST_DATA", data);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(data);
                    wr.flush();
                    wr.close();
                }
                connection.connect();

            } catch (UnknownHostException e) {
                Log.e("HOST ERROR", e.getLocalizedMessage());
                return null;
            } catch (ConnectException e) {
                Log.e("CONNECTION ERROR", e.getLocalizedMessage());
                return null;
            } catch (Error e) {
                Log.e("ERROR", e.getLocalizedMessage());
                return null;
            }

            int responseCode = connection.getResponseCode();

            switch (responseCode) {
                case 200:
                case 201:
                    inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                    StringBuilder builder = new StringBuilder();
                    String readLine;
                    while ((readLine = reader.readLine()) != null) {
                        builder.append(readLine);
                    }
                    reader.close();
                    connection.disconnect();
                    return builder.toString();
                case 404:
                    connection.disconnect();
                    return null;
                default:
                    connection.disconnect();
                    return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}