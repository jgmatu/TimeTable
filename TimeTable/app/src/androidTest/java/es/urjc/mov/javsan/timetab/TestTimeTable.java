package es.urjc.mov.javsan.timetab;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.urjc.mov.javsan.Structures.Day;
import es.urjc.mov.javsan.Structures.Group;
import es.urjc.mov.javsan.Structures.Groups;
import es.urjc.mov.javsan.Structures.Student;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestTimeTable {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginRule = new ActivityTestRule<>(LoginActivity.class);


    @Test
    public void test_loginIncorrect() throws Exception {
        Activity activity = mLoginRule.launchActivity(new Intent());

        checkFailDni();
        checkOkDni(activity);
    }

    private void checkFailDni() {
        String failDni = "TestFail";

        onView(withId(R.id.user)).perform(typeText(failDni) , closeSoftKeyboard());
        onView(withId(R.id.user_sign_in_button)).perform(click());
        onView(withId(R.id.user)).check(matches(withText(failDni)));
        onView(withId(R.id.user)).perform(clearText());
    }

    private void checkOkDni(Activity activity) {
        String okDni = "test";
        prepareLogin(activity, okDni);

        login(okDni);
        checkWeek();
        checkDay(activity, okDni, R.id.monday, Day.getDay(0));
        checkDay(activity, okDni, R.id.tuesday, Day.getDay(1));
        checkDay(activity, okDni, R.id.wednesday, Day.getDay(2));
        checkDay(activity, okDni, R.id.thursday, Day.getDay(3));
        checkDay(activity, okDni, R.id.friday, Day.getDay(4));
    }

    private void login(String okDni) {
        onView(withId(R.id.user)).perform(typeText(okDni), closeSoftKeyboard());
        onView(withId(R.id.user_sign_in_button)).perform(click());
    }

    private void checkWeek() {
        onView(withId(R.id.monday)).check(matches(isDisplayed()));
        onView(withId(R.id.tuesday)).check(matches(isDisplayed()));
        onView(withId(R.id.wednesday)).check(matches(isDisplayed()));
        onView(withId(R.id.thursday)).check(matches(isDisplayed()));
        onView(withId(R.id.friday)).check(matches(isDisplayed()));
    }

    private void checkDay(Activity activity, String okDni, int day, String d) {
        onView(withId(day)).perform(click());

        String result = getResult(activity, okDni, d);

        onView(withId(R.id.day_time)).check(matches(withText(result)));
        onView(withId(R.id.go_week)).perform(scrollTo(), click());
    }

    private String getResult(Activity activity, String okDni, String day) {
        DataBaseTimeTabs dataBaseTimeTabs = new DataBaseTimeTabs(activity.getApplicationContext());

        String result = dataBaseTimeTabs.selectTimes(okDni, day);
        dataBaseTimeTabs.close();

        return result;
    }

    private void prepareLogin (Activity activity, String okDni) {
        DataBaseTimeTabs dataBaseTimeTabs = new DataBaseTimeTabs(activity.getApplicationContext());

        dataBaseTimeTabs.onUpgrade(dataBaseTimeTabs.getWritableDatabase(), 0 , 1);

        String student[] = new String[]{
                okDni,
                "test",
                "test",
                "test",
                "05-05-2017"
        };
        String classRoom[] = new String[] {
                "test",
                "test",
                "test"
        };

        String subject [] = new String[] {
                "test",
                "test"
        };
        String hourA [] = new String[] {
                Day.getDay(0),
                "0",
                "0",
                "0",
                "0",
        };

        String hourB [] = new String[] {
                Day.getDay(2),
                "0",
                "0",
                "0",
                "0",
        };
        Student s = new Student(student);
        dataBaseTimeTabs.insertStudent(s);

        Groups g = new Groups();

        g.add(new Group("A1", classRoom, subject, hourA));
        g.add(new Group("B1", classRoom, subject, hourB));
        g.add(new Group("A2", classRoom, subject, hourA));
        g.add(new Group("B2", classRoom, subject, hourB));

        dataBaseTimeTabs.insertGroups(g);
        dataBaseTimeTabs.insertTimes(s, g);
        dataBaseTimeTabs.close();
    }
}
