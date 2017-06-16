package es.urjc.mov.javsan.timetab;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import es.urjc.mov.javsan.Structures.Day;

import static es.urjc.mov.javsan.timetab.TimeTabActivity.getDni;


public class FragmentDay extends Fragment {

    private View fragmentLayout;
    private String day;
    private String result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentLayout = inflater.inflate(R.layout.week_day , container, false);
        Intent intent = getActivity().getIntent();
        Bundle msg = intent.getExtras();
        result = day = "?";

        String dni = getDni(msg);
        if (dni.equals("")) {
            return fragmentLayout;
        }

        String day = getDay(getArguments());
        if (!Day.isDay(day)) {
            return fragmentLayout;
        }
        setBackgroundDay(day);
        // Put the day selected in the fragment.
        TextView t = (TextView) fragmentLayout.findViewById(R.id.day);
        t.setText(day);

        // Set the button go back to week fragment.
        Button b = (Button) fragmentLayout.findViewById(R.id.go_week);
        b.setOnClickListener(new GoWeek());


        if (!getResult(savedInstanceState)) {
            Time time = new Time(dni, day);
            time.execute();
        }
        return fragmentLayout;
    }



    private boolean getResult(Bundle savedState) {
        if (savedState == null) {
            return false;
        }

        result = savedState.getString("result");
        if (!result.equals("?")) {
            TextView textView = (TextView) fragmentLayout.findViewById(R.id.day_time);
            textView.setText(result);
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("result", result);
        outState.putBoolean("inDay", true);
        outState.putString("day", day);

        TimeTabActivity activity = (TimeTabActivity) getActivity();
        activity.setState(outState);
    }

    private String getDay(Bundle msg) {
        if (msg == null) {
            return "";
        }

        String day = msg.getString("day");
        if (day == null) {
            return "";
        }
        return day;
    }

    private void setBackgroundDay(String day) {
        if (Day.getDay(0).equals(day)) {
            fragmentLayout.setBackgroundColor(Color.rgb(0, 204, 255));
        } else if (Day.getDay(1).equals(day)) {
            fragmentLayout.setBackgroundColor(Color.rgb(255, 100, 61));
        } else if (Day.getDay(2).equals(day)) {
            fragmentLayout.setBackgroundColor(Color.rgb(224, 224, 224));
        } else if (Day.getDay(3).equals(day)) {
            fragmentLayout.setBackgroundColor(Color.rgb(255, 41, 184));
        } else if (Day.getDay(4).equals(day)) {
            fragmentLayout.setBackgroundColor(Color.rgb(255, 255, 102));
        }
    }


    private class GoWeek implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            goToWeek();
        }

        private void goToWeek() {
            FragmentWeek fragmentWeek = new FragmentWeek();
            FragmentManager mgr = getFragmentManager();
            FragmentTransaction t = mgr.beginTransaction();

            mgr.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            t.replace(R.id.main_frame, fragmentWeek);
            t.addToBackStack(null);
            t.commit();
        }
    }

    private class Time extends AsyncTask<String, String, String> {

        private String dni;

        Time (String dn, String d) {
            dni = dn;
            day = d;
        }

        @Override
        protected String doInBackground(String... params) {
            DataBaseTimeTabs db = new DataBaseTimeTabs(getContext());

            result = db.selectTimes(dni, day);
            db.close();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            TextView t = (TextView) fragmentLayout.findViewById(R.id.day_time);
            t.setText(result);
        }
    }
}
