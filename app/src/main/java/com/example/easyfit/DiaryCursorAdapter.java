package com.example.easyfit;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DiaryCursorAdapter extends CursorAdapter {
    public DiaryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you dont bind any data to the view at this point.

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_diary_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Polja koja koristimo u novom pogledu
        TextView textViewListID = view.findViewById(R.id.textView_list_name_diary);
        TextView textViewListCals = view.findViewById(R.id.textView_diary_list_Number);
        TextView textViewListDate = view.findViewById(R.id.textView_diary_sub_name);

        //Vrijednosti iz kursora
        int getID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String getDate = cursor.getString(cursor.getColumnIndexOrThrow("diary_cal_date"));
        int getCal = cursor.getInt(cursor.getColumnIndexOrThrow("diary_cal"));

        //Ispuni polja sa vrijednostima iz kursora
        textViewListID.setText(String.valueOf(getID));
        textViewListCals.setText(String.valueOf(getCal));
        textViewListDate.setText(getDate);
    }
}