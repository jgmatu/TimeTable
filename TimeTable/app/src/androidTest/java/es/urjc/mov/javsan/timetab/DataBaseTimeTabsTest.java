package es.urjc.mov.javsan.timetab;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.urjc.mov.javsan.Structures.Group;
import es.urjc.mov.javsan.Structures.Groups;
import es.urjc.mov.javsan.Structures.Student;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class DataBaseTimeTabsTest {
    private DataBaseTimeTabs dbHelper;

    @Before
    public void connectDb() throws Exception {
        Context appContext = getTargetContext();
        dbHelper = new DataBaseTimeTabs(appContext);
        dbHelper.onUpgrade(dbHelper.getWritableDatabase(),  0 , 1);
    }

    @Test
    public void dbTestingStudents_isCorrect() throws Exception {
        String dni = "TestOk";
        Student student = getStudent(dni);

        assertTrue(!dbHelper.existStudent(dni));
        dbHelper.insertStudent(student);
        assertTrue(dbHelper.existStudent(dni));
    }

    @Test
    public void dbTestingGroups_isCorrect() throws Exception {
        String idGroup = "GroupOk";
        Groups groups = getGroups(idGroup);

        assertTrue(!existGroups(groups));
        dbHelper.insertGroups(groups);
        assertTrue(existGroups(groups));
    }

    @Test
    public void dbTestingTimes_isCorrect() throws Exception {
        String dni = "Test";
        String idGroup = "A1";
        Groups groups = getGroups(idGroup);
        Student student = getStudent(dni);

        dbHelper.insertStudent(student);
        dbHelper.insertGroups(groups);
        dbHelper.insertTimes(student , groups);

        assertTrue(dbHelper.existStudent(dni));
        assertTrue(existGroups(groups));
    }

    @After
    public void closeDb() throws Exception {
        dbHelper.close();
    }

    private boolean existGroups(Groups groups) {
        boolean exist = true;

        for (Group g : groups.getGroups()) {
            exist = exist && dbHelper.existGroup(g);
        }
        return exist;
    }

    private Student getStudent (String dni) {
        String[] fields = {dni, "Test", "Test", "Test", "Test"};
        Student s = new Student(fields);

        return s;
    }

    private Groups getGroups(String idGroup) {
        String[] cR = {"0", "0", "test"};
        String[] sub = {"Test", "Test"};
        String[] time = {"Test", "0", "0", "0", "0"};
        Group g = new Group(idGroup, cR, sub, time);

        Groups groups = new Groups();
        groups.add(g);

        return  groups;
    }
}