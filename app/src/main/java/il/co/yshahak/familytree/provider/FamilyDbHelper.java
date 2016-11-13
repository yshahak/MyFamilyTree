package il.co.yshahak.familytree.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class FamilyDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "familyTree.db";

    FamilyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_PERSON_TABLE =
                " CREATE TABLE " + FamilyContract.PersonEntry.TABLE_NAME + " (" +
                        FamilyContract.PersonEntry._ID + " INTEGER PRIMARY KEY," +
                        FamilyContract.PersonEntry.COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
                        FamilyContract.PersonEntry.COLUMN_IS_MALE + " INTEGER  NOT NULL, " +
                        FamilyContract.PersonEntry.COLUMN_BIRTH_DAY + " INTEGER, " +
                        FamilyContract.PersonEntry.COLUMN_DEPTH + " INTEGER NOT NULL" +
                        " );";

        final String CREATE_RELATION_TABLE =
                "CREATE TABLE " + FamilyContract.RelationEntry.TABLE_NAME + " (" +
                        FamilyContract.RelationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FamilyContract.RelationEntry.COLUMN_FIRST_ID + " INTEGER NOT NULL, " +
                        FamilyContract.RelationEntry.COLUMN_SECOND_ID + " INTEGER NOT NULL, " +
                        FamilyContract.RelationEntry.COLUMN_RELATION_TYPE + " INTEGER NOT NULL" +
                        " );";
        sqLiteDatabase.execSQL(CREATE_RELATION_TABLE);
        sqLiteDatabase.execSQL(CREATE_PERSON_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FamilyContract.RelationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FamilyContract.PersonEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
