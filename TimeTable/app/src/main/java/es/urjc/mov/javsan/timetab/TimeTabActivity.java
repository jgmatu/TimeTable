package es.urjc.mov.javsan.timetab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import es.urjc.mov.javsan.Structures.Day;

public class TimeTabActivity extends AppCompatActivity {

    private boolean student;
    private boolean inDay;
    private String day;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.conf, menu);
        return true;
    }

    // Handle item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.student_menu:
                student = true;
                goToStudentFragment();
                return true;

            case R.id.time_menu:
                student = false;
                goToTimeTableFragment();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String getDni(Bundle msg) {
        if (msg == null) {
            return "";
        }
        String dni = msg.getString("dni");
        if (dni == null) {
            return "";
        }
        return dni;
    }

    public void setState(Bundle outState) {
        day = outState.getString("day");
        inDay = outState.getBoolean("inDay");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetab);
        student = inDay = false;
        day = "?";

        recoverState(savedInstanceState);
        if (student) {
            goToStudentFragment();
        } else if (inDay) {
            goToDay();
        } else {
            goToTimeTableFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("STATE", String.format("Student %b, inDay : %b, day : %s", student, inDay, day));
        outState.putBoolean("student", student);
        outState.putBoolean("inDay", inDay);
        outState.putString("day", day);
    }

    private void goToStudentFragment() {
        FragmentStudent student = new FragmentStudent();

        student.setArguments(getIntent().getExtras());
        FragmentManager mgr = getSupportFragmentManager();

        clearStack(mgr);

        FragmentTransaction t = mgr.beginTransaction();
        t.replace(R.id.main_frame, student);
        t.addToBackStack(null);
        t.commit();
    }

    private void goToTimeTableFragment() {
        FragmentWeek timeTable = new FragmentWeek();

        timeTable.setArguments(getIntent().getExtras());
        FragmentManager mgr = getSupportFragmentManager();

        clearStack(mgr);

        FragmentTransaction t = mgr.beginTransaction();
        t.replace(R.id.main_frame, timeTable);
        t.addToBackStack(null);
        t.commit();
    }

    private void goToDay () {
        FragmentDay timeTable = new FragmentDay();
        Bundle msg = getIntent().getExtras();
        msg.putString("day", day);

        timeTable.setArguments(msg);
        FragmentManager mgr = getSupportFragmentManager();

        clearStack(mgr);

        FragmentTransaction t = mgr.beginTransaction();
        t.replace(R.id.main_frame, timeTable);
        t.addToBackStack(null);
        t.commit();

    }

    private void clearStack(FragmentManager fm) {
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void recoverState (Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        student = savedInstanceState.getBoolean("student");
        inDay = savedInstanceState.getBoolean("inDay");
        day = savedInstanceState.getString("day");
    }
}