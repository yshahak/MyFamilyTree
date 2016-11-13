package il.co.yshahak.familytree.provider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Tree family database.
 */
public class FamilyContract {

    static final String CONTENT_AUTHORITY = "familyProvider";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_PERSON = "person";
    static final String PATH_RELATION = "relation";

    /* Inner class that defines the table contents of the location table */
    public static final class PersonEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERSON).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSON;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSON;

        // Table name
        static final String TABLE_NAME = PATH_PERSON;

        public static final String _ID = "_id"; //the id of a person will the id for the record
        public static final String COLUMN_PERSON_NAME = "personName";
        public static final String COLUMN_IS_MALE = "isMale";
        public static final String COLUMN_BIRTH_DAY = "birthDay";
        public static final String COLUMN_DEPTH = "depth";


        public static final int COLUMN_INDEX_ID = 0;
        public static final int COLUMN_INDEX_PERSON_NAME = 1;
        public static final int COLUMN_INDEX_IS_MALE = 2;
        public static final int COLUMN_INDEX_BIRTH_DAY = 3;
        public static int COLUMN_INDEX_DEPTH = 4;


        static Uri buildPersonUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getPersonIdFromUri(Uri uri) {
            return uri.getPathSegments().get(COLUMN_INDEX_ID);
        }

        public static String getPersonNameFromUri(Uri uri) {
            return uri.getPathSegments().get(COLUMN_INDEX_PERSON_NAME);
        }
    }

    public static final class RelationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RELATION).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RELATION;

        // Table name
        static final String TABLE_NAME = PATH_RELATION;

        static final String _ID = "_id";

        public static final String COLUMN_FIRST_ID = "firstId"; // in case of MARRIAGE, male must be always first record id, and the female the second
        //in case of HERITAGE, the first id belong to Parent, the second id belong to child
        public static final String COLUMN_SECOND_ID = "secondId";
        public static final String COLUMN_RELATION_TYPE = "relationType";

        public static int INDEX_ID = 0;
        public static int INDEX_FIRST_ID = 1;
        public static int INDEX_SECOND_ID = 2;
        public static int INDEX_RELATION_TYPE = 3;


        static Uri buildRelationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
