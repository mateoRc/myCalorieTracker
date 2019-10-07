package com.example.easyfit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodFragment extends Fragment {

    private View mainView;
    private Cursor listCursor;

    //action buttons
    private MenuItem menuItemAdd;
    private MenuItem menuItemDelete;


    //holder for buttons on toolbar
    private String currentId;
    private String currentName;

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
    public FoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodFragment.
     */
    // TODO: Rename and change types and number of parameters
    // Creating fragment
    public static FoodFragment newInstance(String param1, String param2) {
        FoodFragment fragment = new FoodFragment();
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
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Prehrana");

        //ispuni listu u FoodFragment
        populateList();

        //kreiraj menu
        setHasOptionsMenu(true);

    }
    //set man View var to the view, so we can change views in fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_food, container, false);
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

        ((MainActivity)getActivity()).getMenuInflater().inflate(R.menu.menu_food, menu);

        //Assign menu items to vars
        menuItemAdd = menu.findItem(R.id.menu_food_plus);
        menuItemDelete = menu.findItem(R.id.menu_food_delete);

        //hide as default
        menuItemAdd.setVisible(true);
        menuItemDelete.setVisible(false);

    }

    //Klik na ikone na toolbar-u (dodaj, izbriši)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Test
        //Toast.makeText(getActivity(), "X: " + id, Toast.LENGTH_LONG).show();

        if (id == R.id.menu_food_plus) {

            //Intent intent = new Intent(FoodFragment.this, )
            //Test
            //Toast.makeText(getActivity(), "DODAJ HRANU", Toast.LENGTH_LONG).show();
            createNewFood();
        }
        if (id == R.id.menu_food_delete) {
            deleteFood();
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateList() {

        //baza podataka
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();

        //hrana (food tablica)
        String fields[] = new String[]{
                "_id",
                "food_name",
                "food_description",
                "food_calories"
        };
        listCursor = sqLiteAdapter.select("food", fields);

        //Odabir ListView-a za popuniti
        ListView items = getActivity().findViewById(R.id.list_view_food);

        //Postavljanje CursorAdaptera
        FoodCursorAdapter foodCursorAdapter = new FoodCursorAdapter(getActivity(), listCursor);

        //Spajanje cursoradaptera na ListView
        items.setAdapter(foodCursorAdapter);

        //onclicklistener
        items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(i);
            }
        });
        sqLiteAdapter.close();
    }

    public void listItemClicked(int listItemClicked){
        //promijeni layout, dodaj gumb za izbrisati, sakrij gumb za dodati
        int id = R.layout.fragment_food_details;
        setMainVIew(id);
        menuItemDelete.setVisible(true);
        menuItemAdd.setVisible(false);

        //cursor na ID koji smo kliknuli
        listCursor.moveToPosition(listItemClicked);

        //ID i ime kursora
        currentId = listCursor.getString(0);
        currentName = listCursor.getString(1);

        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();

        String fields[] = new String[] {
                "_id",
                "food_name",
                "food_description",
                "food_calories"
        };

        //Prebaci view na odabrani element koji ima slijedeći id->
        String idSQL = sqLiteAdapter.checkEntry(currentId);
        Cursor cursor = sqLiteAdapter.selectWhere("food", fields, "_id", idSQL);

        //Vrijednosti kursora u String
        //String stringID = cursor.getString(0);
        String stringName = cursor.getString(1);
        String stringDesc = cursor.getString(2);
        String stringCal = cursor.getString(3);

        //Postavljanje vrijednosti na TextView
        TextView foodNameTV = getView().findViewById(R.id.textViewNameItem);
        foodNameTV.setText(stringName);
        TextView foodDescTV = getView().findViewById(R.id.textViewDescItem);
        foodDescTV.setText(stringDesc);
        TextView foodCalTV = getView().findViewById(R.id.textViewCalNumItem);
        foodCalTV.setText(stringCal);

        sqLiteAdapter.close();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    public boolean onNavigationItemSelected(MenuItem item){
        Toast.makeText(getActivity(), "TestOnNavItemSel", Toast.LENGTH_LONG).show();
        return true;
    }

    //kreiraj novu hranu
    public void createNewFood() {
        int id = R.layout.fragment_food_add;
        setMainVIew(id);

        //listener za gumb "dodaj"
        Button buttonSave = getActivity().findViewById(R.id.button_saveFood);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFoodToDB();
            }
        });
    }

    //izbrisi hranu
    public void deleteFood() {
         //promijeni na zadani layout
        int id = R.layout.fragment_food_delete;
        setMainVIew(id);

        //button za poništiti radnju
        Button buttonCancel = getActivity().findViewById(R.id.buttonNO);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFoodCancel();
            }
        });

        Button buttonConfirm = getActivity().findViewById(R.id.buttonYES);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFoodConfirm();
            }
        });
    }

    public void deleteFoodCancel(){
        //vrati korisnika
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, new FoodFragment(), FoodFragment.class.getName()).commit();
    }

    public void deleteFoodConfirm() {

        //Baza podataka
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();

        int id = Integer.parseInt(currentId);

        int idSQL = sqLiteAdapter.checkEntry(id);

        //brisanje iz baze podataka
        sqLiteAdapter.delete("food", "_id", idSQL);

        //TEST
        Toast.makeText(getActivity(), "Izbrisano", Toast.LENGTH_SHORT).show();

        returnUser();

        sqLiteAdapter.close();
    }

    public void returnUser() {

        //Vraćanje korisnika
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, new FoodFragment(), FoodFragment.class.getName()).commit();

    }

    public void saveFoodToDB() {
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());

        sqLiteAdapter.open();

        EditText name = getActivity().findViewById(R.id.foodName_editText);
        EditText calNum = getActivity().findViewById(R.id.foodCalNum_editText);
        EditText desc = getActivity().findViewById(R.id.foodDesc_editText);

        //provjera ako je korisnik unio prazan string
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Ovo polje je obavezno!");
            return;
        }
        else if (TextUtils.isEmpty(calNum.getText().toString())) {
            calNum.setError("Ovo polje je obavezno!");
            return;
        }
        else if (TextUtils.isEmpty(desc.getText().toString())) {
            desc.setError("Ovo polje je obavezno");
            return;
        }

        String foodNameString = sqLiteAdapter.checkEntry(name.getText().toString());
        String foodDescString = sqLiteAdapter.checkEntry(desc.getText().toString());

        String foodCalNumString = calNum.getText().toString();
        double foodCalNumDouble = Double.parseDouble(foodCalNumString);

        String fields = "_id, " + "food_name, " + "food_description, " + "food_calories";
        String values = "NULL, " + foodNameString + ", " + foodDescString + ", " + foodCalNumDouble;

        sqLiteAdapter.add("food", fields, values);
        Toast.makeText(getActivity(), "Hrana uspješno dodana!", Toast.LENGTH_LONG).show();

        returnUser();

        sqLiteAdapter.close();
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
        // TODO: Updatedate argument type and name
        void onFragmentInteraction(Uri uri);
    }



}
