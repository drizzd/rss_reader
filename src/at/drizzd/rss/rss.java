package at.drizzd.rss;

import android.app.ListActivity;
import android.os.Bundle;
import android.database.MatrixCursor;
import android.widget.SimpleCursorAdapter;

public class rss extends ListActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String column = "asdf";
        MatrixCursor cursor = new MatrixCursor(new String[] {column});
        cursor.addRow(new Object[] {"x"});
        cursor.addRow(new Object[] {"y"});
        cursor.addRow(new Object[] {"z"});
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[] {column},
                new int[] {android.R.id.text1});
        setListAdapter(adapter);
    }
}
