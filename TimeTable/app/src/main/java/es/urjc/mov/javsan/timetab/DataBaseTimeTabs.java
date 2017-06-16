package es.urjc.mov.javsan.timetab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.system.StructPollfd;
import android.util.StringBuilderPrinter;

import es.urjc.mov.javsan.Structures.Group;
import es.urjc.mov.javsan.Structures.Groups;
import es.urjc.mov.javsan.Structures.Student;


public class DataBaseTimeTabs extends SQLiteOpenHelper {
    public static final String EMPTYTIMETAB = "";

    private final static String NAME = "timeTable";
    private final static int VERSION = 1;

    private final String STUDENTS = "students";
    private final String GROUPS = "groups";
    private final String TIMES = "times";

    private final String[] DAYS = {"week_monday", "week_tuesday", "week_wednesday", "week_thursday" , "week_friday"};

    private final String CREATESTUDENTS = "CREATE TABLE " + STUDENTS + " (\n" +
            "\tDNI_id TEXT PRIMARY KEY,\n" +
            "\tNAME TEXT,\n" +
            "\tLASTNAME TEXT,\n" +
            "\tLOCATION TXT,\n" +
            "\tEXPIRES TXT" +
            ");\n";

    private final String CREATEGROUP = "CREATE TABLE " + GROUPS + " (\n" +
            "\tGROUP_id TEXT PRIMARY KEY,\n" +
            "\tSUBJECT TEXT,\n" +
            "\tTEACHER TEXT,\n" +
            "\tCLASSROOM TEXT,\n" +
            "\tFLOOR TEXT,\n" +
            "\tBUILDING TEXT,\n" +
            "\tDAY TEXT,\n" +
            "\tHBEGIN INTEGER,\n" +
            "\tMBEGIN INTEGER,\n" +
            "\tHEND INTEGER,\n" +
            "\tMEND INTEGER\n" +
            ");\n";

    private final String CREATETIMES = "CREATE TABLE " + TIMES + " (\n" +
            "\tGROUP_id TEXT NOT NULL,\n" +
            "\tDNI_id TEXT NOT NULL,\n" +
            "\tPRIMARY KEY (GROUP_id, DNI_id),\n" +
            "\tFOREIGN KEY (GROUP_id) REFERENCES " + GROUPS + " (GROUP_id)\n" +
            "\t\tON DELETE CASCADE ON UPDATE NO ACTION,\n" +
            "\tFOREIGN KEY (DNI_id) REFERENCES " + STUDENTS + " (DNI_id)\n" +
            "\t\t ON DELETE CASCADE ON UPDATE NO ACTION\n" +
            ");\n";

    public DataBaseTimeTabs(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATESTUDENTS);
        db.execSQL(CREATEGROUP);
        db.execSQL(CREATETIMES);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        doReset(db);
    }

    public void insertStudent (Student s) {
        SQLiteDatabase db = getWritableDatabase();

        if (db.insert(STUDENTS, null, getStundent(s)) == -1){
            db.close();
            throw new RuntimeException("Can't insert student in database");
        }
        db.close();
    }

    public void insertGroups(Groups groups) {
        for (Group g : groups.getGroups()) {
            if (!existGroup(g)) {
                insertGroup(g);
            }
        }
    }

    public void insertTimes(Student s , Groups groups) {
        SQLiteDatabase db = getWritableDatabase();

        for (Group g : groups.getGroups()) {
            if (db.insert(TIMES, null, getTimes(s, g)) == -1) {
                db.close();
                throw new RuntimeException("Can't insert times in database");
            }
        }
        db.close();
    }

    private ContentValues getTimes(Student s, Group g) {
        ContentValues values = new ContentValues();

        values.put("DNI_id", s.getDni());
        values.put("Group_id" , g.getIdG());
        return values;
    }

    public boolean existGroup(Group g) {
        SQLiteDatabase db = getReadableDatabase();

        String [] projection = {
                "Group_id"
        };
        String selection = "Group_id" + " = ?";
        String[] selectionArgs = {
                g.getIdG()
        };
        Cursor cursor = db.query(
                GROUPS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean exist = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exist;
    }

    public boolean existStudent(String dni) {
        SQLiteDatabase db = getReadableDatabase();
        String [] projection = {
                "DNI_id"
        };
        String selection = "DNI_id" + " = ?";
        String[] selectionArgs = {
                dni
        };
        Cursor cursor = db.query(
                STUDENTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean exist = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exist;
    }

    public String selectTimes(String dni, String day) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUBJECT,TEACHER,CLASSROOM,FLOOR," +
                "BUILDING,HBEGIN,MBEGIN,HEND,MEND " +
                "FROM " + TIMES + " NATURAL JOIN " +  GROUPS +
                " WHERE times.dni_id ='"+dni+"' AND "+GROUPS+".day = '"+day+"'\n;";

        Cursor cursor = db.rawQuery(query, null);
        String result = formatResult(cursor);
        cursor.close();
        db.close();
        return result;
    }

    public String selectStudent (String dni) {
        SQLiteDatabase db  = getReadableDatabase();
        String [] projection = {
                "DNI_id",
                "NAME",
                "LASTNAME",
                "LOCATION",
                "EXPIRES"
        };

        String selection = "DNI_id" + " = ?";
        String[] selectionArgs = {
                dni
        };
        Cursor cursor = db.query(
                STUDENTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return "";
        }
        String result = studentResult(cursor);
        cursor.close();
        db.close();
        return result;
    }

    public String selectExpired(String dni) {
        SQLiteDatabase db = getReadableDatabase();

        String [] projection = {
                "EXPIRES"
        };
        String selection = "DNI_id" + " = ?";
        String[] selectionArgs = {
                dni
        };
        Cursor cursor = db.query(
                STUDENTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (!cursor.moveToFirst()) {
            db.close();
            return "";
        }
        String result = cursor.getString(cursor.getColumnIndex("EXPIRES"));
        cursor.close();
        db.close();
        return result;
    }

    public void updateGroups(Groups groups) {
        for (Group g : groups.getGroups()) {
            if (existGroup(g))  {
                updateGroup(g);
            } else {
                insertGroup(g);
            }
        }
    }

    private void updateGroup(Group g) {
        SQLiteDatabase db = getWritableDatabase();
        String[] filter = new String[]{g.getIdG()};
        if (db.update(GROUPS, getGroup(g) , "Group_id = ?", filter) == -1) {
            db.close();
            throw new RuntimeException ("Error updating data group...");
        }
        db.close();
    }

    public void updateStudent(Student s) {
        SQLiteDatabase db = getWritableDatabase();
        String [] filter = new String[]{s.getDni()};

        if (db.update(STUDENTS, getStundent(s), "DNI_id = ?", filter) == -1) {
            db.close();
            throw new RuntimeException ("Error updating data student...");
        }
        db.close();
    }

    public void updateTimes(Student s, Groups groups) {
        SQLiteDatabase db = getWritableDatabase();
        String[] filter = new String[]{s.getDni()};

        db.delete(TIMES, "DNI_id = ?", filter);
        insertTimes(s, groups);
        db.close();
    }

    private void insertGroup(Group g) {
        SQLiteDatabase db = getWritableDatabase();
        if (db.insert(GROUPS, null, getGroup(g)) == -1) {
            db.close();
            throw new RuntimeException("Can't insert Groups in database");
        }
        db.close();
    }

    private String studentResult(Cursor cursor) {
        String result = String.format("%s\n", "____________________________");

        result += String.format("DNI : %s\n",cursor.getString(cursor.getColumnIndex("DNI_id")));
        result += String.format("Name : %s\n" , cursor.getString(cursor.getColumnIndex("NAME")));
        result += String.format("LastName : %s\n", cursor.getString(cursor.getColumnIndex("LASTNAME")));
        result += String.format("Location : %s\n", cursor.getString(cursor.getColumnIndex("LOCATION")));
        result += String.format("Expires : %s\n", cursor.getString(cursor.getColumnIndex("EXPIRES")));
        result += String.format("%s\n", "____________________________");

        return result;
    }

    private ContentValues getStundent(Student s) {
        ContentValues values = new ContentValues();

        values.put("DNI_id", s.getDni());
        values.put("Name", s.getName());
        values.put("LastName", s.getLastName());
        values.put("Location", s.getLocation());
        values.put("Expires", s.getExpired());

        return values;
    }

    private String formatResult(Cursor cursor) {
        String result = "";
        int pos = 0;
        result += String.format("%s\n", "________________________________");
        while (cursor.moveToNext()) {
            result += String.format("%s\n", "________________________________");
            result += weekTimeTable(cursor);
            result += String.format("%s\n", "________________________________");
            pos = pos + 1;
        }
        if (pos == 0) {
            result += String.format("%s\n", "There is not times today");
        }
        result += String.format("%s\n", "________________________________");
        return result;
    }

    private String weekTimeTable(Cursor cursor) {
        int hBegin = cursor.getInt(cursor.getColumnIndex("HBEGIN"));
        int mBegin = cursor.getInt(cursor.getColumnIndex("MBEGIN"));
        int hEnd = cursor.getInt(cursor.getColumnIndex("HEND"));
        int mEnd = cursor.getInt(cursor.getColumnIndex("MEND"));

        String subject = cursor.getString(cursor.getColumnIndex("SUBJECT"));
        String teacher = cursor.getString(cursor.getColumnIndex("TEACHER"));
        String classRoom = cursor.getString(cursor.getColumnIndex("CLASSROOM"));
        String floor = cursor.getString(cursor.getColumnIndex("FLOOR"));
        String building = cursor.getString(cursor.getColumnIndex("BUILDING"));
        String formatHour = getHour(hBegin, mBegin, hEnd, mEnd);

        return String.format("Subject : %s\nTeacher : %s\nClass : %s\nFloor : %s\nBuilding : %s\n " +
                "Hour : %s\n", subject, teacher, classRoom, floor , building, formatHour);
    }

    private String getHour (int hBegin, int mBegin, int hEnd, int mEnd) {
        return String.format("%d:%s-%d:%s\n", hBegin, setDoubleZero(mBegin),
                hEnd, setDoubleZero(mEnd));
    }

    private String setDoubleZero(int value) {
        String zeros = String.valueOf(value);

        if (zeros.equals("0")) {
            zeros = "00";
        }
        return zeros;
    }

    private void  doReset(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TIMES);
        onCreate(db);
    }

    private ContentValues getGroup(Group g) {
        ContentValues values = new ContentValues();

        values.putAll(getClass(g.getClassRoom()));
        values.putAll(getSubject(g.getSubject()));
        values.putAll(getGroup(g.getTime()));
        values.put("Group_id", g.getIdG());
        return values;
    }

    private ContentValues getClass(String[] classRoom) {
        ContentValues values = new ContentValues();

        values.put("CLASSROOM", classRoom[0]);
        values.put("FLOOR", classRoom[1]);
        values.put("BUILDING", classRoom[2]);
        return values;
    }

    private ContentValues getSubject(String[] subject) {
        ContentValues values = new ContentValues();

        values.put("Subject", subject[0]);
        values.put("Teacher", subject[1]);
        return values;
    }

    private ContentValues getGroup(String[] time) {
        ContentValues values = new ContentValues();

        values.put("DAY", time[0]);
        values.put("HBEGIN", Integer.parseInt(time[1]));
        values.put("MBEGIN", Integer.parseInt(time[2]));
        values.put("HEND", Integer.parseInt(time[3]));
        values.put("MEND", Integer.parseInt(time[4]));
        return values;
    }
}
