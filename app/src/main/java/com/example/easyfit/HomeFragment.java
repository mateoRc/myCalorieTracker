package com.example.easyfit;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;



import android.os.Handler;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private View mainView;

    private ProgressBar pgBar;
    private int progress = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Potreban prazan konstruktor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    /*************************** na učitavanju fragmenta koristi dizajn... *************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_home, container, false);
        return mainView;
    }

    /***************************** na učitavanju fragmenta... **********************************/
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Pozdravlja korisnika u naslovu
        greetingsUser();

        //Metoda za prikaz današnjeg datuma
        automateDate();

        //Button za dodati hranu
        foodEaten();

        //Button za izbrisati dnevni unos
        deleteList();

        //Prikaz što smo danas dodali
        displayList();

        //Ažuriranje promjena u dnevnik
        updateDiary();
    }

    /********************************* Pozdravlja korisnika ***************************************/
    private void greetingsUser() {
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();
        String fieldsUser[] = new String[]{
                "user_name",
                "user_gender"
        };
        Cursor cursorUser = sqLiteAdapter.select("user", fieldsUser);
        String userName = cursorUser.getString(0);
        String userGenderString = cursorUser.getString(1);

        int userGender = Integer.parseInt(userGenderString);

        //Promijeni naslov kada se učita fragment, ovisno o spolu
        if (userGender == 0)
                ((MainActivity) getActivity()).getSupportActionBar().setTitle("Dobrodošao, " + userName + "!");
        else if (userGender == 1)
                ((MainActivity) getActivity()).getSupportActionBar().setTitle("Dobrodošla, " + userName + "!");

    }

    /***************** azuriranje iznosa kalorija (zbroj kalorija u dnevniku prehrane) ************/
    private void updateCalDisplay(String date) {
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();

                String dateSQL = sqLiteAdapter.checkEntry(date);

                String fields[] = new String[]{
                        "fe_name",
                        "fe_date",
                        "fe_calories"
                };

                Cursor cursorFe = sqLiteAdapter.selectWhere("food_entry", fields, "fe_date", dateSQL);

                String fieldsUser[] = new String[]{
                        "_id",
                        "user_BMR",
                        "user_BMI",
                };

                //Početni podaci o korisniku
                Cursor cursorUser = sqLiteAdapter.select("user", fieldsUser);
                String stringBMR = cursorUser.getString(1);
                String stringBMI = cursorUser.getString(2);
                int intBMR = Integer.parseInt(stringBMR);
                double doubleBMI = Double.parseDouble(stringBMI);

                TextView bmiDisplay = getActivity().findViewById(R.id.textViewBMI_display);
                try {
                    bmiDisplay.setText("Vaš BMI: " + stringBMI);
                }catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

                TextView bmiMessage = getActivity().findViewById(R.id.textViewBMI_message);
                if (doubleBMI<18.5) {
                    bmiMessage.setText("Pothranjeni ste!");
                }
                else if (doubleBMI >= 18.5 && doubleBMI <= 25) {
                    bmiMessage.setText("Normalne ste tjelesne težine.");
                }
                else if (doubleBMI > 25) {
                    bmiMessage.setText("Pretili ste!");
                }

                String insFields = "_id, eaten_cal_date, eaten_cal";

                    int eatenCal = 0;


                    int intCursorCount = cursorFe.getCount();
                    for (int i = 0; i < intCursorCount; i++) {
                        String stringFdCal = cursorFe.getString(2);
                        if (stringFdCal.isEmpty()) continue;
                        int intFd = Integer.parseInt(stringFdCal);

                        eatenCal += intFd;

                        try {
                            updateProgressBar(intBMR, intFd);
                        }
                        catch (Exception e) {


                        }
                        cursorFe.moveToNext();

                    }
                    String insVal = "NULL," + dateSQL + "," + eatenCal;
                    sqLiteAdapter.add("eaten_cal", insFields, insVal);

                    //Test
                    //String s = " " + eatenCal;
                    //Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();

                    //izračun preostalog iznosa kalorija koji korisnik smije unijeti
                    int displayCal = intBMR - eatenCal;
                    String s = "" + displayCal;

                    TextView textViewdisplayCal = getActivity().findViewById(R.id.amount_home_textview);

                    if (displayCal >= 0) textViewdisplayCal.setText("Preostali dnevni unos: " + s);
                    else {
                        int cal = Math.abs(displayCal);
                        String s1 = "" + cal;

                        textViewdisplayCal.setText("Prekoračio si dnevni unos za " + s1 + " kalorija!");

                    }
                    sqLiteAdapter.close();

    }

    /************************************** ažuriranje progress bar-a *****************************/
    private void updateProgressBar(int max, final int input) {

        pgBar = getActivity().findViewById(R.id.progressBar);
        pgBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        progress = pgBar.getProgress();
        pgBar.setMax(max);

        progress += input;
        pgBar.setProgress(progress);

        if (progress == max || progress > max){
            pgBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }

    }

    /*****************************   Button - dodavanje hrane   ***********************************/
    private void foodEaten() {

        Button homeBtnAdd = getActivity().findViewById(R.id.button_add_home);
        homeBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodEntry();
            }
        });

    }

    /***************************** promijeni layout na zadani ID **********************************/
    private void setMainVIew(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }

    /*****************************  izbriši današnju listu - button na početnoj********************/
    private void deleteList() {
        Button homeBtnReset = getActivity().findViewById(R.id.button_reset);
        homeBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFood();
            }
        });
    }

    /*****************************  izbriši današnju listu ****************************************/
    public void deleteFood() {
        //promijeni na zadani layout
        int id = R.layout.fragment_food_delete;
        setMainVIew(id);
        //makni pozdrav
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");
        //button za poništiti radnju
        Button buttonCancel = getActivity().findViewById(R.id.buttonNO);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cancel();
            }
        });

        Button buttonConfirm = getActivity().findViewById(R.id.buttonYES);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmDelete();
            }
        });
    }

    public void Cancel(){
        //vrati korisnika
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, new HomeFragment(), HomeFragment.class.getName()).commit();
    }

    public void ConfirmDelete() {

        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();
        sqLiteAdapter.deleteAll("food_entry");

        try {

            String date = getCurrentDate();
            String dateSQL = sqLiteAdapter.checkEntry(date);
            String fieldsDiary[] = new String[] {
                    "_id",
                    "diary_cal_date",
                    "diary_cal"
            };
            Cursor cursorDiary = sqLiteAdapter.selectWhere("diary", fieldsDiary, "diary_cal_date", dateSQL);
            cursorDiary.moveToLast();
            int idDiary = cursorDiary.getInt(0);
            sqLiteAdapter.delete("diary", "_id", idDiary);
            //sqLiteAdapter.deleteAll("diary");
            Toast.makeText(getActivity(), "Izbrisana stavka iz dnevnika!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            //Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "Popis je prazan!", Toast.LENGTH_SHORT).show();
        }

        returnUser();
        sqLiteAdapter.close();

    }





    /*************************** Prikaz hrane u listi *********************************************/
    private void displayList() {
        String date = getCurrentDate();

        //ispiši sve unose iz tablice food_entry sa datumom date
        displayFoodByDate(date);

        //prikaz broja kalorija (iznad home buttona)
        updateCalDisplay(date);
    }

    /************************ prikaz hrane za današnji datum **************************************/
    private void displayFoodByDate(String date) {
        SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
        sqLiteAdapter.open();

        String dateSQL = sqLiteAdapter.checkEntry(date);

        String fields[] = new String[] {
                "fe_name",
                "fe_date",
                "fe_calories"
        };

        Cursor cursor = sqLiteAdapter.selectWhere("food_entry", fields, "fe_date", dateSQL);

        /************************ Dinamičko dodavanje redova **************************************/
        try {
        int count = cursor.getCount();

        TableLayout stk = getActivity().findViewById(R.id.table_main);

        TextView tv0 = new TextView(getActivity());
        TableRow tbrow0 = new TableRow(getActivity());
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(getActivity());
        tbrow0.addView(tv1);

        for (int i = 0; i < count; i++) {

                String name = cursor.getString(0);
                String cal = cursor.getString(2);

                stk.addView(tbrow0);
                TableRow tbrow = new TableRow(getActivity());
                TextView t1v = new TextView(getActivity());
                t1v.setText(name);
                t1v.setTextColor(Color.BLUE);
                t1v.setGravity(Gravity.START);
                t1v.setWidth(400);
                t1v.setTextSize(16);
                tbrow.addView(t1v);

                TextView t2v = new TextView(getActivity());
                t2v.setText(cal);
                t2v.setTextColor(Color.BLUE);
                t2v.setGravity(Gravity.END);
                t2v.setTextSize(16);
                tbrow.addView(t2v);

                stk.addView(tbrow);

                cursor.moveToNext();

                stk.removeView(tbrow0);
        }
        }catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        sqLiteAdapter.close();
    }

    /*********************** Otvori slijedeći fragment (foodEntry) ********************************/
    private void foodEntry() {
        Fragment fragment = null;
        Class fragClass = null;
        fragClass = FoodEntry.class;

        try {
            fragment = (Fragment) fragClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, fragment).commit();
    }

    /****************** metoda za vraćanje korisnika na željeni fragment(klasu) *******************/
    public void returnUser() {

        //Vraćanje korisnika na HomeFragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, new HomeFragment(), HomeFragment.class.getName()).commit();
    }

    /********************** metoda koja vraća današnji datum u odabranom formatu ******************/
    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        Date today = calendar.getTime();
        String date = df.format(today);
        return date;
    }

    /******************************* prikaz današnjeg datuma **************************************/
    private void automateDate() {
        Date date = new Date();
        TextView textView = getActivity().findViewById(R.id.date_home_textview);
        textView.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
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


/****************************************** premještanje metode u HomeFragment ********************

 /*************** metoda ubacuje podatke/ažurira BP  ********************/
public void updateDiary() {
    SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
    sqLiteAdapter.open();

    //Današnji dan
    String date = getCurrentDate();

    String fields[] = new String[] {
            "fe_name",
            "fe_date",
            "fe_calories"
    };

    //Polja za provjeru podataka
    String fieldsDiary[] = new String[] {
            "_id",
            "diary_cal_date",
            "diary_cal"
    };

    //polja za unos podataka
    String fieldsDiary2[] = new String[] {
            "diary_cal_date",
            "diary_cal"
    };

    int calSum = 0;

    try {

        String dateSQL = sqLiteAdapter.checkEntry(date);
        Cursor cursor = sqLiteAdapter.selectWhere("food_entry", fields, "fe_date", dateSQL);

        String dateInput = cursor.getString(1);

        //cursor.moveToFirst();
        int countCursor = cursor.getCount();

        // Petlja kojom ažuriramo kalorije
        for (int i = 0; i < countCursor; i++) {
            //String dateVal = cursor.getString(1);
            String cal = cursor.getString(2);
            int calVal = Integer.parseInt(cal);
            calSum += calVal;
            cursor.moveToNext();
        }

        Cursor cursorDiary = sqLiteAdapter.select("diary", fieldsDiary);
        //Pomakni kursor na zadnju stavku (dan)
        cursorDiary.moveToLast();

        int nrow = sqLiteAdapter.countAllRecordsNotes("diary");

        if (nrow == 0) {
            int calSumSQL = sqLiteAdapter.checkEntry(calSum);
            String values = dateSQL + "," + calSumSQL; //dateSQL
            sqLiteAdapter.add("diary", "diary_cal_date, diary_cal", values);
            Toast.makeText(getActivity(), "Dodana je prva stavka", Toast.LENGTH_SHORT).show();
        }
        else {
            //u varijablu diaryDate spremamo datum iz posljednje stavke
            String diaryDate = cursorDiary.getString(1);

            //Test
            //Toast.makeText(getActivity(), diaryDate , Toast.LENGTH_SHORT).show();
            //probati zamijeniti idDiary sa countDiaryEntry - ako uspije mogu izbrisati fieldsDiary2

            //Ako datum iz zadnje stavke nije jednak trenutnom datumu, dodaj novi redak u tablici
            if (!diaryDate.equals(date)) {
                //unos za novi dan u Bazu podataka
                //Toast.makeText(getActivity(), dateSQL, Toast.LENGTH_SHORT).show();
                int calSumSQL = sqLiteAdapter.checkEntry(calSum);
                String values = dateSQL + "," + calSumSQL; //dateSQL
                sqLiteAdapter.add("diary", "diary_cal_date, diary_cal", values);
                Toast.makeText(getActivity(), "Dodana je nova stavka", Toast.LENGTH_SHORT).show();

            }
            //Ako već imamo unos za trenutni datum, ažuriraj redak u tablici
            else {
                cursorDiary.moveToLast();
                int idDiary = cursorDiary.getInt(0);

                int iDdiarySQL = sqLiteAdapter.checkEntry(idDiary);
                int calSumSQL = sqLiteAdapter.checkEntry(calSum);
                String calStr = calSumSQL + "";
                String stringInput[] = new String[]{dateInput, calStr}; //dateinput
                sqLiteAdapter.updateFields("diary", "_id", idDiary, fieldsDiary2, stringInput);
                //Toast.makeText(getActivity(), "Dnevnik je ažuriran!", Toast.LENGTH_SHORT).show();
            }
        }
    } catch (Exception e) {
        //Toast.makeText(getActivity(), "Exception:" + e.toString()+ " Datum: " + date, Toast.LENGTH_LONG).show();
        //e.printStackTrace();

        int nrow = sqLiteAdapter.countAllRecordsNotes("diary");
        if (nrow == 0) {
            Toast.makeText(getActivity(), "Dnevnik je prazan!", Toast.LENGTH_SHORT).show();
        }
    }
    sqLiteAdapter.close();
}

}
