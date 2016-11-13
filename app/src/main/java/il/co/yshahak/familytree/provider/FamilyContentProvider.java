package il.co.yshahak.familytree.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

@SuppressWarnings("ConstantConditions")
public class FamilyContentProvider extends ContentProvider {

    static final int PERSON = 1;
    static final int RELATIONS = 2;

    static final UriMatcher uriMatcher;
    final static String authority = FamilyContract.CONTENT_AUTHORITY;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(authority, FamilyContract.PATH_PERSON, PERSON);
        uriMatcher.addURI(authority, FamilyContract.PATH_RELATION, RELATIONS);

    }

    private FamilyDbHelper dbHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new FamilyDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PERSON: {
                long rowID = db.insert(FamilyContract.PersonEntry.TABLE_NAME, null, values);
                if (rowID > 0) {
                    returnUri = FamilyContract.PersonEntry.buildPersonUri(rowID);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case RELATIONS: {
                long _id = db.insert(FamilyContract.RelationEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = FamilyContract.RelationEntry.buildRelationUri(_id);
                else
                    throw new SQLException("Failed to insert person: ");
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case PERSON: {
                try {
                    retCursor = dbHelper.getReadableDatabase().query(
                            FamilyContract.PersonEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                } catch (IllegalArgumentException e) {
                    return null;
                }
                break;
            }
            case RELATIONS: {
                retCursor = dbHelper.getReadableDatabase().query(
                        FamilyContract.RelationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PERSON:
                count = db.delete(FamilyContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RELATIONS:
                count = db.delete(FamilyContract.RelationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PERSON:
                count = db.update(FamilyContract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case RELATIONS:
                count = db.update(FamilyContract.RelationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PERSON:
                return FamilyContract.PersonEntry.CONTENT_TYPE;
            case RELATIONS:
                return FamilyContract.RelationEntry.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}