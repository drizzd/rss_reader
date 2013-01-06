package at.drizzd.rss;

import android.util.Log;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import java.net.URL;
import java.lang.Thread;

import java.net.MalformedURLException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

public class rss extends ListActivity
{
    boolean mOnline = true;
    String TAG = getClass().getSimpleName();
    ArrayList mEntries = new ArrayList();
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
        Thread loader = new Thread(new Runnable() {
            public void run() {
                if (mOnline) {
                    runRssFeed();
                } else {
                    loadEntries();
                }
            }
        });

        loader.start();
    }

    public void onStop() {
        mStatus = Status.PENDING;
        super.onStop();
    }

    private void runRssFeed() {
        while (mStatus == Status.RUNNING) {
            loadRssFeed();
            runOnUiThread(new Runnable() {
                public void run() {
                    updateList();
                }
            });
            nap();
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
        synchronized(mEntries) {
            mEntries = headlines;
        }
    }

    private void loadEntries() {
        String[] alphabet = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "x", "y", "z"
        };
        synchronized(mEntries) {
            mEntries.clear();
        }
        for (int i = 0; i < alphabet.length; i++) {
            synchronized(mEntries) {
                mEntries.add(alphabet[i]);
                Log.d(TAG, "[" + i + "] " + alphabet[i]);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    updateList();
                }
            });
            if (mStatus != Status.RUNNING) {
                break;
            }
            nap();
        }
    }

    private void updateList() {
        mAdapter.clear();
        synchronized(mEntries) {
            for (int i = 0; i < mEntries.size(); i++) {
                mAdapter.add(mEntries.get(i));
            }
        }
    }

    private void nap() {
        try {
            Thread.sleep(1000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
