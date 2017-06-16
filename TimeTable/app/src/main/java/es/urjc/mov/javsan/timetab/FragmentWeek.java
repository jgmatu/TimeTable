package es.urjc.mov.javsan.timetab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import es.urjc.mov.javsan.Structures.Day;

import static es.urjc.mov.javsan.timetab.TimeTabActivity.getDni;

public class FragmentWeek extends Fragment {

    private View fragmentLayout;
    private String dni;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentLayout = inflater.inflate(R.layout.week_fragment, container, false);

        dni = getDni(getActivity().getIntent().getExtras());
        if (dni.equals("")) {
            return fragmentLayout;
        }

        setDayButton(R.id.monday);
        setDayButton(R.id.tuesday);
        setDayButton(R.id.wednesday);
        setDayButton(R.id.thursday);
        setDayButton(R.id.friday);
        return fragmentLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TimeTabActivity activity = (TimeTabActivity) getActivity();

        outState.putBoolean("inDay", false);
        activity.setState(outState);
    }

    private ImageButton setDayButton(int day) {
        ImageButton imgDay = (ImageButton) fragmentLayout.findViewById(day);
        imgDay.setOnClickListener(new Day(day));

        return imgDay;
    }

    private class Day implements View.OnClickListener {

        private int day;

        Day (int d) {
            day = d;
        }

        @Override
        public void onClick(View v) {
            goShowDayTime();
        }

        private void goShowDayTime() {
            FragmentDay fragmentDay = new FragmentDay();
            Bundle msg = getActivity().getIntent().getExtras();

            msg.putString("day", getNameDay(day));

            fragmentDay.setArguments(msg);

            FragmentManager mgr = getFragmentManager();
            FragmentTransaction t = mgr.beginTransaction();

            t.replace(R.id.main_frame, fragmentDay);
            t.addToBackStack(null);
            t.commit();
        }

        private String getNameDay(int day) {
            switch (day) {
                case R.id.monday :
                    return es.urjc.mov.javsan.Structures.Day.getDay(0);
                case R.id.tuesday :
                    return es.urjc.mov.javsan.Structures.Day.getDay(1);
                case R.id.wednesday :
                    return es.urjc.mov.javsan.Structures.Day.getDay(2);
                case R.id.thursday :
                    return es.urjc.mov.javsan.Structures.Day.getDay(3);
                case R.id.friday :
                    return  es.urjc.mov.javsan.Structures.Day.getDay(4);
                default:
                    return "Bad Day";
            }
        }
    }

}
