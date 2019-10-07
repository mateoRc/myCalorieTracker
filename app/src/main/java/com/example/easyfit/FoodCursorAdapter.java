package com.example.easyfit;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class FoodCursorAdapter extends CursorAdapter {
    public FoodCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_food_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView textViewListName = view.findViewById(R.id.textView_list_name);
        TextView textViewListNumber = view.findViewById(R.id.textView_list_Number);
        TextView textViewListDescription = view.findViewById(R.id.textView_sub_name);

        // Extract properties from cursor
        int getID = cursor.getInt(cursor.getColumnIndexOrThrow("food_calories"));
        String getName = cursor.getString(cursor.getColumnIndexOrThrow("food_name"));
        String getDescription = cursor.getString(cursor.getColumnIndexOrThrow("food_description"));

        // Populate fields with extracted properties
        textViewListName.setText(getName);
        textViewListNumber.setText(String.valueOf(getID));
        textViewListDescription.setText(String.valueOf(getDescription));
    }
}