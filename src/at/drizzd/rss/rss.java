package at.drizzd.rss;

import java.lang.Math;
import java.util.ArrayList;
import java.lang.IndexOutOfBoundsException;
import java.lang.Thread;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;
import android.net.Uri;


public class rss extends ListActivity
{
    boolean mOnline = true;
    String TAG = getClass().getSimpleName();
    ArrayList<String> mHeadlines = new ArrayList<String>();
    ArrayList<String> mLinks = new ArrayList<String>();
    ArrayList<String> mEntries = new ArrayList<String>();
    ArrayList<String> mEntryLinks = new ArrayList<String>();
    ArrayAdapter mAdapter;
    private volatile Status mStatus = Status.PENDING;

    public enum Status {
        PENDING,
        RUNNING
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);
    }

    public void onStart() {
        super.onStart();
        mStatus = Status.RUNNING;
        new Thread(new Runnable() {
            public void run() {
                runRssFeed();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                runEntries();
            }
        }).start();
    }

    public void onStop() {
        mStatus = Status.PENDING;
        super.onStop();
    }

    private void runRssFeed() {
        while (mStatus == Status.RUNNING) {
            loadRssFeed();
            nap(60*1000);
        }
    }

    private void loadRssFeed() {
        URL url;
        try {
            url = new URL("http://news.ycombinator.com/rss");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        ArrayList headlines = new ArrayList();
        ArrayList links = new ArrayList();
        try {
            SimpleRssReader.loadRssFeed(url, headlines, links);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "[0] " + headlines.get(0));
        synchronized (mEntries) {
            mHeadlines = headlines;
            mLinks = links;
        }
    }

    private void runEntries() {
        while (mStatus == Status.RUNNING) {
            loadEntries();
            runOnUiThread(new Runnable() {
                public void run() {
                    updateList();
                }
            });
            nap(1000);
        }
    }
    private void loadEntries() {
        synchronized (mEntries) {
            for (int i = 0; i < mHeadlines.size(); i++) {
                String headline = mHeadlines.get(i);
                boolean found = false;
                for (int j = 0; j < mEntries.size(); j++) {
                    if (mEntries.get(j).contentEquals(headline)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    /* we have a winner */
                    mEntries.add(0, headline);
                    mEntryLinks.add(0, mLinks.get(i));
                    Log.d(TAG, "[" + i + "] " + headline.substring(0, Math.min(5, headline.length())) + "...");
                    break;
                }
            }
        }
    }

    private void updateList() {
        mAdapter.clear();
        synchronized (mEntries) {
            for (int i = 0; i < mEntries.size(); i++) {
                mAdapter.add(mEntries.get(i));
            }
        }
    }

    private void nap(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri;
        try {
            synchronized (mEntries) {
                uri = Uri.parse(mEntryLinks.get(position));
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
