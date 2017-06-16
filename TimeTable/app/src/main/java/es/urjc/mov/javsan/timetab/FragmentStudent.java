package es.urjc.mov.javsan.timetab;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static es.urjc.mov.javsan.timetab.TimeTabActivity.getDni;

public class FragmentStudent extends Fragment {

    private View fragmentLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentLayout = inflater.inflate(R.layout.student_fragment, container, false);

        String dni = getDni(getActivity().getIntent().getExtras());
        if (dni.equals("")) {
            return fragmentLayout;
        }

        DBGetStudent db = new DBGetStudent(dni, getContext());
        db.execute();
        return fragmentLayout;
    }

    private class DBGetStudent extends AsyncTask<String, String, String> {

        private String dni;
        private Context context;

        DBGetStudent(String d, Context c) {
            dni = d;
            context = c;
        }

        @Override
        protected String doInBackground(String... params) {
            DataBaseTimeTabs dBStudentHelper = new DataBaseTimeTabs(context);
            String result = String.format("%s\n", "*** STUDENT DATES *** ");

            result += String.format("%s\n", "**************************** ");
            result += dBStudentHelper.selectStudent(dni);
            dBStudentHelper.close();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView t = (TextView) fragmentLayout.findViewById(R.id.student_result);

            t.setText(result);
        }
    }
}
