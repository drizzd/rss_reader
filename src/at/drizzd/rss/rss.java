package at.drizzd.rss;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class rss extends ListActivity
{
    private HashMap<String, String> mapOneEntry(String column, String row)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(column, row);
        return map;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String[] entries = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "x", "y", "z"
        };
        String column = "asdf";
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < entries.length; i++) {
            list.add(mapOneEntry(column, entries[i]));
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_1,
                new String[] {column},
                new int[] {android.R.id.text1});
        setListAdapter(adapter);
    }
}
