package at.drizzd.rss;

import android.util.Log;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import java.util.Vector;

import java.lang.Thread;

public class rss extends ListActivity
{
    String TAG = getClass().getSimpleName();
    Vector<String> mEntries = new Vector<String>();
    ArrayAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);
        new Thread(new Runnable() {
            public void run() {
                loadEntries();
            }
        }).start();
    }

    private void loadEntries() {
        String[] alphabet = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "x", "y", "z"
        };
        synchronized(this) {
            mEntries.clear();
        }
        for (int i = 0; i < alphabet.length; i++) {
            synchronized(this) {
                mEntries.add(alphabet[i]);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    updateList();
                }
            });
            nap();
        }
    }

    private synchronized void updateList() {
        mAdapter.clear();
        for (int i = 0; i < mEntries.size(); i++) {
            mAdapter.add(mEntries.get(i));
        }
    }

    private void nap() {
        try {
            Thread.sleep(1000);
        } catch (Throwable t) {
            Log.e(TAG, "Error", t);
        }
    }
}
