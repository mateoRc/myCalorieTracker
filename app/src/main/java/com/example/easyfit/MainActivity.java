package com.example.easyfit;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.widget.Toast;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        EditUserDataFragment.OnFragmentInteractionListener,
        FoodFragment.OnFragmentInteractionListener,
        FoodEntry.OnFragmentInteractionListener,
        DiaryFragment.OnFragmentInteractionListener
        {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("easyFit");

        //Navigacija
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //STETHO
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        // baza podataka - za početan unos/provjeru elemenata
        SQLiteAdapter db = new SQLiteAdapter(this);
        db.open();

        //ako je broj redova u tablici food == 0, ispuni tablicu slijedećim vrijednostima
        int nrow = db.countAllRecordsNotes("food");
        if (nrow == 0) {
            db.add("food", "_id, food_name, food_description, food_calories", "NULL, 'Šunka', '100 grama narezak', '350'");
            db.add("food", "_id, food_name, food_description, food_calories", "NULL, 'Jaje', 'Tvrdo kuhano', '200'");
            db.add("food", "_id, food_name, food_description, food_calories", "NULL, 'Čokolada', '100 grama', '300'");
            db.add("food", "_id, food_name, food_description, food_calories","NULL, 'Zobene pahuljice', '50 grama', '100'");
        }

        //provjera ako postoji korisnik u bazi podataka
        nrow = db.countAllRecordsNotes("user");
        if (nrow == 0) {
            Intent intent1 = new Intent(MainActivity.this, UserDataEntry.class);
            startActivity(intent1);
        }

        // test update()
        /*
        long id = 1;
        String value = "Mitijooo";
        String valueSQL = db.checkEntry(value);
        db.update("user", "user_id", id, "user_name", valueSQL);
        */

        db.close();

        //inicijalizaicija fragmenta
        if (nrow != 0) {
            init();
        }

    }

    /*************************************inicijalizacija fragmenta *******************************/
    private void init() {

        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = HomeFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout1, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    /********************************* Odabir fragmenata u navigaciji Menu-a **********************/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //spremi korsinikov odabir u fragmentClass
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_home) {
                fragmentClass = HomeFragment.class;
        } else if (id == R.id.nav_profile) {
                fragmentClass = EditUserDataFragment.class;
        } else if (id == R.id.nav_food) {
                fragmentClass = FoodFragment.class;
        } else if (id == R.id.nav_diary) {
                fragmentClass = DiaryFragment.class;
        } else if (id == R.id.nav_exit) {
                finish();
                System.exit(0);
        }

        //item -> fragment
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Mijenjanje prikaza na odabrani fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        try{
            fragmentManager.beginTransaction().replace(R.id.frameLayout1, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
