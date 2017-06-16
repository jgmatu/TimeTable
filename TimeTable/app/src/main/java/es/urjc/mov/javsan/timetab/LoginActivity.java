package es.urjc.mov.javsan.timetab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.urjc.mov.javsan.Structures.Expired;
import es.urjc.mov.javsan.Structures.Groups;
import es.urjc.mov.javsan.Structures.Student;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via user/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUserView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUserView = (AutoCompleteTextView) findViewById(R.id.user);
        populateAutoComplete();

        Button mUserSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUserView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid user, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid ¡user address.
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUserValid(user)) {
            mUserView.setError(getString(R.string.error_invalid_user));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(user);
            mAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
    }

    private boolean isUserValid(String user) {
        //TODO: Replace this with your own logic
        return true;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only user.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary user addresses first. Note that there won't be
                // a primary user address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> users = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            users.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addUsersToAutoComplete(users);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        ;
    }

    private void addUsersToAutoComplete(List<String> usersCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, usersCollection);

        mUserView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
        int ADDRESS = 0;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String IP = "10.0.2.2";
        private final int PORT = 2000;

        private final String dni;
        private String error;

        UserLoginTask(String d) {
            error = "?";
            dni = d;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DataBaseTimeTabs dbTimeTable = new DataBaseTimeTabs(getApplicationContext());

            if (dbTimeTable.existStudent(dni) && !isExpired(dbTimeTable)) {
                return true;
            }

            TimeTabClient tb = requestStudentTimeTab(dni);
            if (tb == null) {
                return false;
            }

            if (dbTimeTable.existStudent(dni)) {
                updateTimes(tb, dbTimeTable);
            } else {
                insertNewTimes(tb, dbTimeTable);
            }
            dbTimeTable.close();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                startTimeTable();
            } else {
                mUserView.setError(error);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private boolean isExpired(DataBaseTimeTabs dBHelper) {
            Expired expired = new Expired(dBHelper.selectExpired(dni));

            return expired.isExpired();
        }

        private void startTimeTable() {
            Intent activityIntent = new Intent(LoginActivity.this, TimeTabActivity.class);
            Bundle activityInfo = new Bundle();

            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            activityInfo.putString("dni", dni);
            activityIntent.putExtras(activityInfo);

            startActivity(activityIntent);
        }

        private TimeTabClient requestStudentTimeTab(String d) {
            TimeTabClient timeTabClient = new TimeTabClient();

            try {
                error = timeTabClient.connect(IP, PORT);
                if (!error.equals("")) {
                    return null;
                }
                error = timeTabClient.sendRequestTimeTab(d);
                if (!error.equals("")) {
                    return null;
                }
                error = timeTabClient.receiveReplyTimeTab();
                if (!error.equals("")) {
                    return  null;
                }
                error = timeTabClient.close();
                if (!error.equals("")) {
                    return null;
                }
            } catch (IOException e) {
                error = "ERROR RECEIVING MESSAGE : " + e.toString();
                return null;
            }
            return timeTabClient;
        }

        private void updateTimes(TimeTabClient tb, DataBaseTimeTabs db) {
            Student student = tb.getStudent();
            Groups groups = tb.getGroups();

            db.updateStudent(student);
            db.updateGroups(groups);
            db.updateTimes(student, groups);
        }

        private void insertNewTimes(TimeTabClient tb, DataBaseTimeTabs db) {
            Student student = tb.getStudent();
            Groups groups = tb.getGroups();

            db.insertStudent(student);
            db.insertGroups(groups);
            db.insertTimes(student, groups);
        }
     }
}

