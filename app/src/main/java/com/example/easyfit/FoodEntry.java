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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodEntry.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoodEntry#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodEntry extends Fragment {

    private View mainView;
    private Cursor listCursor;  //listCursorCategory

    private MenuItem menuItemAdd;

    private MenuItem menuItemDelete;

    private String currentId;
    private String currentName;
    private String currentDesc;
    private String currentCal;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FoodEntry() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodEntry.
     */

    // TODO: Rename and change types and number of parameters
    public static FoodEntry newInstance(String param1, String param2) {
        FoodEntry fragment = new FoodEntry();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Promijeni naslov kada se učita fragment
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Novi unos");

        try {
            setHasOptionsMenu(true);
        }catch (Exception e) {
            e.printStackTrace();
        }
        populateList();
    }

    public void populateList() {
        setMainView(R.layout.fragment_food_entry);

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
        ListView items = getActivity().findViewById(R.id.list_view_food_entry);

        //Postavljanje CursorAdaptera
        FoodCursorAdapter foodCursorAdapter = new FoodCursorAdapter(getActivity(), listCursor);

        //Spajanje cursoradaptera na ListView
        items.setAdapter(foodCursorAdapter);

        items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClicked(i);
            }
        });

        sqLiteAdapter.close();
    }

    /********************** metoda za potvrdu dnevnog unosa ***************************************/
    public void listItemClicked(int listItemClicked) {
        try {
            menuItemAdd.setVisible(false);
        }catch (NullPointerException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        int id = R.layout.fragment_food_entry_food_item;
        setMainView(id);
        //cursor na ID koji smo kliknuli
        listCursor.moveToPosition(listItemClicked);

        //ID i ime kursora
        currentId = listCursor.getString(0);
        currentName = listCursor.getString(1);
        currentDesc = listCursor.getString(2);
        currentCal = listCursor.getString(3);

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
        String stringID = cursor.getString(0);
        String stringName = cursor.getString(1);
        String stringDesc = cursor.getString(2);
        String stringCal = cursor.getString(3);

        //Postavljanje vrijednosti na TextView
        TextView foodNameTV = getView().findViewById(R.id.food_entry_item_name);
        foodNameTV.setText(stringName);
        TextView foodDescTV = getView().findViewById(R.id.food_entry_item_desc);
        foodDescTV.setText(stringDesc);
        TextView foodCalTV = getView().findViewById(R.id.food_entry_item_cal);
        foodCalTV.setText(stringCal);

        //Listener za gumb "potvrdi unos"
        Button addButton = getActivity().findViewById(R.id.button_add_food_item);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoodToDB();              //addFOodToDiary
                returnUser();
                // Test
                //Toast.makeText(getActivity(), "BUTTON CLICKED", Toast.LENGTH_SHORT).show();

            }
        });

        sqLiteAdapter.close();
        //Test
        //Toast.makeText(getActivity(), "Hrana dodana!", Toast.LENGTH_SHORT).show();
    }

    /************************************* metoda koja vraća trenutni datum ***********************/
    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        Date today = calendar.getTime();
        String date = df.format(today);
        return date;
    }

    /******************** vrati korisnika na HomeFragment klasu (kod dodavanja u dnevni unos ******/
    public void returnUser() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, new HomeFragment(), HomeFragment.class.getName()).commit();
    }

    /**************************** vrati korisnika na FoodEntry klasu (kod dodavanja u bp)**********/
    public void returnUserToFE() {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, new FoodEntry(), FoodEntry.class.getName()).commit();
    }

    /******************************* dodaj novi unos u BP *****************************************/
    public void addFoodToDB() {
    //Baza podataka
    SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
    sqLiteAdapter.open();

    String date = getCurrentDate();
    String dateSQL = sqLiteAdapter.checkEntry(date);

    String foodIDStr = currentId;
    String foodIDSQL = sqLiteAdapter.checkEntry(foodIDStr);

    String foodName = currentName;
    String foodNameSQL = sqLiteAdapter.checkEntry(foodName);

    String foodCal = currentCal;
    String foodCalSQL = sqLiteAdapter.checkEntry(foodCal);

    //String fields = "fe_id, fe_name, fe_date, fe_food_id, fe_calories";
    String values = foodNameSQL + "," + dateSQL + "," + foodIDSQL + "," + foodCalSQL;
    sqLiteAdapter.add("food_entry", "fe_name, fe_date, fe_food_id, fe_calories", values);

    //Test
    //Toast.makeText(getActivity(), foodIDStr + " " + foodName + " " + foodCal + " dodana!", Toast.LENGTH_SHORT).show();
    sqLiteAdapter.close();
    }

    /*************************postavi view na id odabranog view-a *********************************/
    private void setMainView(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    /******************************* vidljivi buttoni (samo za dodati) ****************************/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //TODO add your menu entries here

        (getActivity()).getMenuInflater().inflate(R.menu.menu_food, menu);

        //Assign menu items to vars
        menuItemAdd = menu.findItem(R.id.menu_food_plus);
        menuItemDelete = menu.findItem(R.id.menu_food_delete);

        //hide as default
        menuItemAdd.setVisible(true);
        menuItemDelete.setVisible(false);
    }

    /***********************Klik na ikone na toolbar-u (dodaj)*************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_food_plus) {

            //Intent intent = new Intent(FoodFragment.this, )
            //Test
            Toast.makeText(getActivity(), "DODAJ HRANU", Toast.LENGTH_LONG).show();
            createNewFood();

        }
        if (id == R.id.menu_food_delete) {
        }
        return super.onOptionsItemSelected(item);
    }

    /*************************** prebaci na zadani view za dodati hranu ***************************/
    private void createNewFood(){
        int id = R.layout.fragment_food_add;
        setMainVIew(id);
        menuItemAdd.setVisible(false);
        //listener za gumb "dodaj"
        Button buttonSave = getActivity().findViewById(R.id.button_saveFood);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSaveClicked();
            }
        });
    }

    /********************************** metoda za spremiti unos u BP ******************************/
    public void buttonSaveClicked() {
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
            desc.setError("Ovo polje je obavezno!");
            return;
        }

        String foodNameString = sqLiteAdapter.checkEntry(name.getText().toString());
        String foodDescString = sqLiteAdapter.checkEntry(desc.getText().toString());

        String foodCalNumString = calNum.getText().toString();
        double foodCalNumDouble = Double.parseDouble(foodCalNumString);

        String fields = "_id, " + "food_name, " + "food_description, " + "food_calories";
        String values = "NULL, " + foodNameString + ", " + foodDescString + ", " + foodCalNumDouble;

        sqLiteAdapter.add("food", fields, values);
        Toast.makeText(getActivity(), "Hrana uspješno dodana u bazu podataka!", Toast.LENGTH_LONG).show();
        //Test
        //Toast.makeText(getActivity(), "Radi!", Toast.LENGTH_LONG).show();
        returnUserToFE();
        sqLiteAdapter.close();
    }

    /********************************** postavi korisnika na id pogleda (View) ********************/
    private void setMainVIew(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_food_entry, container, false);
        return mainView;
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
