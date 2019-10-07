package com.example.easyfit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserDataEntry extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_data_entry);

        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDataSubmit();

            }
        });


    } // onCreate() metoda

    /******************metoda koja obrađuje podatke korisnika i unosi ih u BP *********************/
    public void userDataSubmit() {

        //Test
        //Toast.makeText(this, "Submit pressed!", Toast.LENGTH_LONG).show();

        //1. Ime
        EditText editTextName = findViewById(R.id.name_edittext);

        //2. VIsina
        EditText editTextHeight = findViewById(R.id.height_edittext);

        //3. Težina
        EditText editTextWeight = findViewById(R.id.weight_edittext);

        //4. Godine
        EditText editTextAge = findViewById(R.id.age_edittext);

        //5. Spol
        Switch switchGender = findViewById(R.id.gender_switch);

        //6. Aktivnost
        RadioGroup radioGroupAL = findViewById(R.id.radioGroup);
        RadioButton lvl1 = findViewById(R.id.radButton1);
        RadioButton lvl2 = findViewById(R.id.radButton2);
        RadioButton lvl3 = findViewById(R.id.radButton3);
        RadioButton lvl4 = findViewById(R.id.radButton4);

        //7. Cilj
        Spinner spinnerGoal = findViewById(R.id.spinner_goal);

        //provjera ako ima praznih unosa

            if (TextUtils.isEmpty(editTextName.getText().toString())) {
                editTextName.setError("Ovo polje ne može biti prazno!");
                Toast.makeText(UserDataEntry.this, "Unesi svoje ime!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(editTextHeight.getText().toString())) {
                editTextHeight.setError("Ovo polje ne može biti prazno!");
                Toast.makeText(UserDataEntry.this, "Unesi svoju visinu!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(editTextWeight.getText().toString())) {
                editTextWeight.setError("Ovo polje ne može biti prazno!");
                Toast.makeText(UserDataEntry.this, "Unesi svoju težinu!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(editTextAge.getText().toString())) {
                editTextAge.setError("Ovo polje ne može biti prazno!");
                Toast.makeText(UserDataEntry.this, "Unesi svoje godine!", Toast.LENGTH_LONG).show();
                return;

            } else if (!lvl1.isChecked() && !lvl2.isChecked() && !lvl3.isChecked() && !lvl4.isChecked()) {
                Toast.makeText(UserDataEntry.this, "Odaberi razinu tjelesne aktivnosti!", Toast.LENGTH_LONG).show();
                return;
            }


            String stringName = editTextName.getText().toString();
            int heightInt = Integer.parseInt(editTextHeight.getText().toString());
            int weightInt = Integer.parseInt(editTextWeight.getText().toString());
            int ageInt = Integer.parseInt(editTextAge.getText().toString());
            int genderInt = 0;
            int activityLevelInt = 0;
            int goalInt;


        //Provjera ako su unosi u određenom rangu
        if ((heightInt < 100) || (heightInt > 250)) {
            Toast.makeText(UserDataEntry.this, "Nepravilan unos - visina!", Toast.LENGTH_LONG).show();
        }
        else if ((weightInt < 30) || (weightInt > 250)) {
            Toast.makeText(UserDataEntry.this, "Nepravilan unos - težina!", Toast.LENGTH_LONG).show();
        }
        else if (ageInt < 18 && ageInt > 0) {
            Toast.makeText(UserDataEntry.this, "Aplikacija nije namijenjena mlađima od 18!", Toast.LENGTH_LONG).show();
        }
        else if (ageInt > 100) {
            Toast.makeText(UserDataEntry.this, "Nepravilan unos - godine!", Toast.LENGTH_LONG).show();
        }
        else if (ageInt <= 0) {
            Toast.makeText(UserDataEntry.this, "Godine ne mogu biti 0 ili negativan broj!", Toast.LENGTH_LONG).show();
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
            String stringDate = getCurrentDate();

            // Unos u bazu podataka - tablica user
            SQLiteAdapter sqLiteAdapter = new SQLiteAdapter(this);
            sqLiteAdapter.open();

            // checkEntry()
            String stringNameSQL = sqLiteAdapter.checkEntry(stringName);
            int heightIntSQL = sqLiteAdapter.checkEntry(heightInt);
            int weightIntSQL = sqLiteAdapter.checkEntry(weightInt);
            int ageIntSQL = sqLiteAdapter.checkEntry(ageInt);
            int genderIntSQL = sqLiteAdapter.checkEntry(genderInt);
            int activityLevelIntSQL = sqLiteAdapter.checkEntry(activityLevelInt);
            int goalIntSQL = sqLiteAdapter.checkEntry(goalInt);
            int BMRSQL = sqLiteAdapter.checkEntry(BMR);
            double BMISQL = sqLiteAdapter.checkEntry(BMI);
            String stringDateSQL = sqLiteAdapter.checkEntry(stringDate);

            String stringInput = "NULL, " + stringNameSQL + "," + heightIntSQL + "," + weightIntSQL + "," + ageIntSQL + "," + genderIntSQL + "," + activityLevelIntSQL + "," + goalIntSQL + "," + BMRSQL + "," + BMISQL + "," + stringDateSQL;

            sqLiteAdapter.add("user", "_id, user_name, user_height, user_weight, user_age, user_gender, user_activity, user_goal, user_BMR, user_BMI, user_date", stringInput);
            sqLiteAdapter.close();

            //Natrag na MainActivity
            startActivity();

            // Test
            // Toast.makeText(UserDataEntry.this, goalInt + " " + activityLevelInt + " " + genderInt, Toast.LENGTH_LONG).show();
        }
    } // method userDataSubmit()

    /***********************************************************************************************
    BMR formula koristi visinu, težinu, godine i spol kako bi se utvrdio bazalni metabolizam
    Jedini faktor koji ne ulazi je postotak masnog tkiva.
    Ova metoda će dodatno prilagoditi BMR ovisno o cilju korisnika
    te razini aktivnosti   ************************************************************************/
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

    /** Izračun BMI se temelji na odnosu tjelesne težine (kg) i kvadrata visine osobe (u metrima):
     * BMI = težina (kg) / visina (m) * visina (m)       ******************************************/
    public double calculateBMI(int height, int weight) {
        double BMI;
        float heightMt = (float) height/100;
        BMI = (float) weight / (heightMt*heightMt);

        BMI = (double) Math.round(BMI * 100d) / 100d;       //  zaokruživanje na 2 decimale
        return BMI;
    }

    /**************************** metoda koja vraća današnji datum ********************************/
    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
        Date today = calendar.getTime();
        String date = df.format(today);
        return date;
    }


    public void startActivity() {
        Intent intent = new Intent(UserDataEntry.this, MainActivity.class);
        startActivity(intent);
    }

} // public class UserDataEntry
