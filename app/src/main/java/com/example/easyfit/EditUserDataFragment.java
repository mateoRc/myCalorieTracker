package com.example.easyfit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditUserDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditUserDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditUserDataFragment extends Fragment {

    private View mainView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditUserDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditUserDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditUserDataFragment newInstance(String param1, String param2) {
        EditUserDataFragment fragment = new EditUserDataFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView =  inflater.inflate(R.layout.fragment_edit_user_data, container, false);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Naslov
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Izmjeni osobne informacije");

        Button editUserDataButton = getActivity().findViewById(R.id.edit_submit_button);
        editUserDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserDataSubmit();
            }
        });
    }

    /************************** metoda za izmjenu osobnih podataka *******************************/
    public void editUserDataSubmit() {

        //1. Korisnik nema opciju izmjene imena
        //2. VIsina
        EditText editTextHeight = getActivity().findViewById(R.id.edit_height_edittext);

        //3. Tezina
        EditText editTextWeight = getActivity().findViewById(R.id.edit_weight_edittext);

        //4. Godine
        EditText editTextAge = getActivity().findViewById(R.id.edit_age_edittext);

        //5. Spol
        Switch switchGender = getActivity().findViewById(R.id.gender_switch_edit);

        //6. Aktivnost
        RadioGroup radioGroupAL = getActivity().findViewById(R.id.radioGroupEdit);
        RadioButton lvl1 = getActivity().findViewById(R.id.radButton1Edit);
        RadioButton lvl2 = getActivity().findViewById(R.id.radButton2Edit);
        RadioButton lvl3 = getActivity().findViewById(R.id.radButton3Edit);
        RadioButton lvl4 = getActivity().findViewById(R.id.radButton4Edit);

        //7. Cilj
        Spinner spinnerGoal = getActivity().findViewById(R.id.spinner_goal_edit);

        //provjera ako ima praznih unosa

         if (TextUtils.isEmpty(editTextHeight.getText().toString())) {
            editTextHeight.setError("Ovo polje ne može biti prazno!");
            Toast.makeText(getActivity(), "Unesi svoju visinu!", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(editTextWeight.getText().toString())) {
            editTextWeight.setError("Ovo polje ne može biti prazno!");
            Toast.makeText(getActivity(), "Unesi svoju težinu!", Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(editTextAge.getText().toString())) {
            editTextAge.setError("Ovo polje ne može biti prazno!");
            Toast.makeText(getActivity(), "Unesi svoje godine!", Toast.LENGTH_LONG).show();
            return;

        } else if (!lvl1.isChecked() && !lvl2.isChecked() && !lvl3.isChecked() && !lvl4.isChecked()) {
            Toast.makeText(getActivity(), "Odaberi razinu tjelesne aktivnosti!", Toast.LENGTH_LONG).show();
            return;
        }

        //String stringName = editTextName.getText().toString();
        int heightInt = Integer.parseInt(editTextHeight.getText().toString());
        int weightInt = Integer.parseInt(editTextWeight.getText().toString());
        int ageInt = Integer.parseInt(editTextAge.getText().toString());
        int genderInt = 0;
        int activityLevelInt = 0;
        int goalInt;

        //Provjera ako su unosi u određenom rangu
        if ((heightInt < 100) || (heightInt > 250)) {
            Toast.makeText(getActivity(), "Nepravilan unos - visina!", Toast.LENGTH_LONG).show();
        }
        else if ((weightInt < 30) || (weightInt > 250)) {
            Toast.makeText(getActivity(), "Nepravilan unos - težina!", Toast.LENGTH_LONG).show();
        }
        else if (ageInt < 18 && ageInt > 0) {
            Toast.makeText(getActivity(), "Aplikacija nije namijenjena mlađima od 18!", Toast.LENGTH_LONG).show();
        }
        else if (ageInt > 100) {
            Toast.makeText(getActivity(), "Nepravilan unos - godine!", Toast.LENGTH_LONG).show();
        }
        else if (ageInt <= 0) {
            Toast.makeText(getActivity(), "Godine ne mogu biti 0 ili negativan broj!", Toast.LENGTH_LONG).show();
        }
        else {

            if (switchGender.isChecked()) genderInt = 1;        //FEMALE - ON == 1
            //else genderInt = 0;                               //MALE  - OFF == 0

            if (lvl1.isChecked()) activityLevelInt = 0;
            else if (lvl2.isChecked()) activityLevelInt = 1;
            else if (lvl3.isChecked()) activityLevelInt = 2;
            else if (lvl4.isChecked()) activityLevelInt = 3;

            //String StringGoal = spinnerGoal.getSelectedItem().toString();
            goalInt = spinnerGoal.getSelectedItemPosition();

            //BMR i BMI metode
            int BMR = calculateBMR(heightInt, weightInt, ageInt, genderInt, activityLevelInt, goalInt);
            double BMI = calculateBMI(heightInt, weightInt);

            //Datum
            //String stringDate = getCurrentDate();

            // Unos u bazu podataka - tablica user
            SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(getActivity());
            sqLiteAdapter.open();

            // checkEntry()
            //String stringNameSQL = sqLiteAdapter.checkEntry(stringName);
            int heightIntSQL = sqLiteAdapter.checkEntry(heightInt);
            int weightIntSQL = sqLiteAdapter.checkEntry(weightInt);
            int ageIntSQL = sqLiteAdapter.checkEntry(ageInt);
            int genderIntSQL = sqLiteAdapter.checkEntry(genderInt);
            int activityLevelIntSQL = sqLiteAdapter.checkEntry(activityLevelInt);
            int goalIntSQL = sqLiteAdapter.checkEntry(goalInt);
            int BMRSQL = sqLiteAdapter.checkEntry(BMR);
            double BMISQL = sqLiteAdapter.checkEntry(BMI);
            //String stringDateSQL = sqLiteAdapter.checkEntry(stringDate);

            String heightIntSQLstring = "" + heightIntSQL;
            String weightIntSQLstring = "" + weightIntSQL;
            String ageIntSQLstring = "" + ageIntSQL;
            String genderIntSQLstring = "" + genderIntSQL;
            String activitySQLstring = "" + activityLevelIntSQL;
            String goalIntSQLstring = "" + goalIntSQL;
            String BMRSQLstring = "" + BMRSQL;
            String BMISQLstring = "" + BMISQL;

            String stringInput[] = new String[] {heightIntSQLstring, weightIntSQLstring, ageIntSQLstring, genderIntSQLstring, activitySQLstring, goalIntSQLstring, BMRSQLstring, BMISQLstring};

            String fields[] = new String[] {
                    "user_height",
                    "user_weight",
                    "user_age",
                    "user_gender",
                    "user_activity",
                    "user_goal",
                    "user_BMR",
                    "user_BMI"
            };

            long id = 1;

            try {
                sqLiteAdapter.updateFields("user", "_id", id, fields, stringInput);
                Toast.makeText(getActivity(), "Informacije USPJEŠNO ažurirane!", Toast.LENGTH_SHORT).show();
            }catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "informacije NISU uspješno ažurirane!", Toast.LENGTH_SHORT).show();
            }
            sqLiteAdapter.close();
        }
    } //metoda userDataSubmit()

    public int calculateBMR(int height, int weight, int age, int gender, int actLvl, int goal) {
        double BMR;
        if (gender == 1)    BMR = ((10 + weight)) + (6.25 * height) - (5 * age) - 161;
        else                BMR = ((10 * weight) + (6.25 * height) - (5 * age) + 5);

        switch (actLvl){
            case 0:
                BMR = BMR * 1.2;
                break;
            case 1:
                BMR = BMR * 1.375;
                break;
            case 2:
                BMR = BMR * 1.55;
                break;
            case 3:
                BMR = BMR * 1.725;
                break;
        }

        switch (goal) {
            case 0:
                BMR = BMR - 500;
                break;
            case 1:
                BMR = BMR + 500;
                break;
            case 2:
                BMR = BMR;
                break;
        }

        int BMRint = (int) BMR;
        return BMRint;
    } // calcBMR() method

    public double calculateBMI(int height, int weight) {
        double BMI;
        float heightMt = (float) height/100;
        BMI = (float) weight / (heightMt*heightMt);

        BMI = (double) Math.round(BMI * 100d) / 100d;       //  zaokruživanje na 2 decimale
        return BMI;
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
