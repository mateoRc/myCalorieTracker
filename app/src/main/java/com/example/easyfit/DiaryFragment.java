package com.example.easyfit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryFragment extends Fragment {

    private View mainView;
    private Cursor listCursor;

    //action buttons
    private MenuItem menuItemAdd;
    private MenuItem menuItemDelete;

    //Fragment variables, necessary for aking fragment run
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //constructor
    public DiaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiaryFragment.
     */
    // TODO: Rename and change types and number of parameters

    // Creating fragment
    public static DiaryFragment newInstance(String param1, String param2) {
        DiaryFragment fragment = new DiaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //run method when created
    // set toolbar menu items
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Naslov
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Dnevnik");

        //ažuriraj dnevnik
        //updateDiary();

        //ažuriraj listu
        populateList();

    }
    //set man View var to the view, so we can change views in fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_diary, container, false);
        // Inflate the layout for this fragment
        return mainView;
    }

    private void setMainVIew(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    //Create action icon on toolbar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //TODO add your menu entries here

        //((MainActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_food, menu);
    }

    //Prikaz elemenata u obliku arrayL
    public void populateList() {

        //baza podataka
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();

        //hrana (food tablica)
        String fields[] = new String[]{
                "_id",
                "diary_cal_date",
                "diary_cal"
        };

        listCursor = sqLiteAdapter.select("diary", fields);

        //Odabir ListView-a za popuniti
        ListView items = getActivity().findViewById(R.id.list_view_diary);

        //Postavljanje CursorAdaptera
        DiaryCursorAdapter diaryCursorAdapter = new DiaryCursorAdapter(getActivity(), listCursor);

        //Spajanje cursoradaptera na ListView
        items.setAdapter(diaryCursorAdapter);

        //onclicklistener
        /*
        items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(i);
            }
        });*/

        sqLiteAdapter.close();
    }




    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        Date today = calendar.getTime();
        String date = df.format(today);
        return date;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        Toast.makeText(getActivity(), "TestOnNavItemSel", Toast.LENGTH_LONG).show();
        return true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
