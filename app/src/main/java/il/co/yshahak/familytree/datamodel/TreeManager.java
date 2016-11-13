package il.co.yshahak.familytree.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import il.co.yshahak.familytree.R;
import il.co.yshahak.familytree.provider.FamilyContract;

/**
 * class that manage tree data model
 */
public class TreeManager {

    /**
     * we will seek for the depth person exist in the DB, as the Person inserted by depth order
     * @param context for ContentResolver
     * @return the id of the root person(may be two, for couple, we don't care who we start with)
     */
    public static int getTreeFirstDepthId(Context context){
        Cursor cursor = context.getContentResolver().query(
                FamilyContract.PersonEntry.CONTENT_URI,
                new String [] {FamilyContract.PersonEntry._ID, "MIN(" + FamilyContract.PersonEntry.COLUMN_DEPTH + ")"},
                null,
                null,
                null);
        int id = -1;
        if (cursor != null ){
            if (cursor.moveToFirst()) {
                id = cursor.getInt(FamilyContract.PersonEntry.COLUMN_INDEX_ID);
            }
            cursor.close();
        }
        return id;
    }

    /**
     * convert Person record from Person Table in DB to Person object
     * @param cursor in the desire position
     * @return Person object
     */
    private static Person getPersonFromCursor(Cursor cursor) {
        Person person = new Person();
        long personId = cursor.getLong(FamilyContract.PersonEntry.COLUMN_INDEX_ID);
        String name = cursor.getString(FamilyContract.PersonEntry.COLUMN_INDEX_PERSON_NAME);
        int depth = cursor.getInt(FamilyContract.PersonEntry.COLUMN_INDEX_DEPTH);
        boolean isMale = cursor.getInt(FamilyContract.PersonEntry.COLUMN_INDEX_IS_MALE) == 1;
        long bd = cursor.getLong(FamilyContract.PersonEntry.COLUMN_INDEX_BIRTH_DAY);
        person.setPersonId(personId);
        person.setName(name);
        person.setMale(isMale);
        person.setDepth(depth);
        person.setBirthDay(bd);
        return person;
    }

    /**
     * Add Person object as a record in Person Table inside the DB
     * @param context for ContentResolver
     * @param person to insert
     * @return Uri of the inserted record
     */
    public static Uri addPersonToDB(Context context, Person person) {
        if (getPersonById(context, person.getPersonId()) != null){ // we already of a record for this id
            Toast.makeText(context, context.getString(R.string.form_invalid_exist), Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(FamilyContract.PersonEntry._ID, person.getPersonId());
            values.put(FamilyContract.PersonEntry.COLUMN_PERSON_NAME, person.getName());
            values.put(FamilyContract.PersonEntry.COLUMN_IS_MALE, person.isMale() ? 1 : 0);
            values.put(FamilyContract.PersonEntry.COLUMN_DEPTH, person.getDepth());
            values.put(FamilyContract.PersonEntry.COLUMN_BIRTH_DAY, person.getBirthDay());
            return context.getContentResolver().insert(
                    FamilyContract.PersonEntry.CONTENT_URI,
                    values);


        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * edit Person record in Person table inside DB
     * @param context for ContentResolver
     * @param person to edit
     * @return 1 if the edit finished successfully, -1 otherwise
     */
    public static int editPersonToDB(Context context, Person person) {
        try {
            ContentValues values = new ContentValues();
            values.put(FamilyContract.PersonEntry.COLUMN_PERSON_NAME, person.getName());
            values.put(FamilyContract.PersonEntry.COLUMN_IS_MALE, person.isMale() ? 1 : 0);
            values.put(FamilyContract.PersonEntry.COLUMN_DEPTH, person.getDepth());
            values.put(FamilyContract.PersonEntry.COLUMN_BIRTH_DAY, person.getBirthDay());
            return context.getContentResolver().update(
                    FamilyContract.PersonEntry.CONTENT_URI,
                    values, FamilyContract.PersonEntry._ID + " = " + person.getPersonId(), null);


        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * delete Person record from DB
     * @param context for ContentResolver
     * @param person to delete
     */
    public static void deletePersonFromDb(Context context, Person person){
        context.getContentResolver().delete(
                FamilyContract.PersonEntry.CONTENT_URI,
                FamilyContract.PersonEntry._ID + " = " + person.getPersonId(), null);
        context.getContentResolver().delete(FamilyContract.RelationEntry.CONTENT_URI,
                FamilyContract.RelationEntry.COLUMN_FIRST_ID + " = " + person.getPersonId()
                 + " OR " + FamilyContract.RelationEntry.COLUMN_SECOND_ID + " = " + person.getPersonId(),null);
    }

    /**
     * add new Person to DB and insert record of this marriage in Relation table
     * @param context for ContentResolver
     * @param newAddPerson the new added person to the tree
     * @param existPerson the person who got married
     * @return Uri of the record inside the Relation table
     */
    public static Uri marry(Context context, Person newAddPerson, Person existPerson){
        if (addPersonToDB(context, newAddPerson) == null){ //the insertion of this person had failed
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(FamilyContract.RelationEntry.COLUMN_FIRST_ID,
                newAddPerson.isMale() ? newAddPerson.getPersonId() : existPerson.getPersonId());
        values.put(FamilyContract.RelationEntry.COLUMN_SECOND_ID,
                !newAddPerson.isMale() ? newAddPerson.getPersonId() : existPerson.getPersonId());
        values.put(FamilyContract.RelationEntry.COLUMN_RELATION_TYPE, Relationship.VIEW_TYPE_MARRIAGE);
        return context.getContentResolver().insert(
                FamilyContract.RelationEntry.CONTENT_URI,
                values);
    }

    /**
     * add child Person to Person table and Relation table
     * @param context for ContentResolver
     * @param child new inserted child
     * @param dad of the child, may be null for single mom
     * @param mom of the child, may be null for single dad
     */
    public static void addChildToParents(Context context, @NonNull Person child, @Nullable Person dad, @Nullable  Person mom){
        //add the child to the table
        if (addPersonToDB(context, child) == null) {  //may be that this id already exists in DB
            return;
        }
        if (dad != null) {
            addChild(context, child, dad);
        }
        if (mom != null) {
            addChild(context, child, mom);
        }
    }

    /**
     * add record of
     * @param context for ContentResolver
     * @param child to register in Relation table
     * @param parent to register in relation table
     */
    private static void addChild(Context context, Person child, Person parent){
        ContentValues contentValue = new ContentValues();
        contentValue.put(FamilyContract.RelationEntry.COLUMN_FIRST_ID,
                parent.getPersonId());
        contentValue.put(FamilyContract.RelationEntry.COLUMN_SECOND_ID,
                    child.getPersonId());
        contentValue.put(FamilyContract.RelationEntry.COLUMN_RELATION_TYPE, Relationship.VIEW_TYPE_HERITAGE);
        context.getContentResolver().insert(
                FamilyContract.RelationEntry.CONTENT_URI,
                contentValue);
    }


    /**
     * check if specific Persin is married
     * @param context for ContentResolver
     * @param id to query against
     * @param isMale if the person we check against is male
     * @return id of the partner, or -1 if doesn't exists
     */
    private static long checkIfIsMarried(Context context, long id, boolean isMale) {
        long seekedId = -1;
        String WHERE = isMale ? FamilyContract.RelationEntry.COLUMN_FIRST_ID : FamilyContract.RelationEntry.COLUMN_SECOND_ID;
        Cursor relationCursor = context.getContentResolver().query(
                FamilyContract.RelationEntry.CONTENT_URI,
                null,
                WHERE + " = ? AND " + FamilyContract.RelationEntry.COLUMN_RELATION_TYPE + " = ? ",
                new String[]{Long.toString(id), Integer.toString(Relationship.VIEW_TYPE_MARRIAGE)},
                null);
        if (relationCursor != null && relationCursor.moveToFirst()) {
            int index = isMale ? FamilyContract.RelationEntry.INDEX_SECOND_ID : FamilyContract.RelationEntry.INDEX_FIRST_ID;
            seekedId = relationCursor.getInt(index);
        }
        if (relationCursor != null) {
            relationCursor.close();
        }
        return seekedId;
    }

    /**
     * build our tree for this person id
     * @param context for ContentResolver
     * @param id to start the tree with
     * @return the root Node for this tree
     */
    public static Node buildTreeFromID(Context context, long id){
        return addNodeFromID(context, id);
    }

    /**
     * create Node object for specific id, in recursive
     * @param context for ContentResolver
     * @param id to create Node for
     * @return the created Node
     */
    private static Node addNodeFromID(Context context, long id){
        Person person = getPersonById(context, id);
        Node node = new Node();
        node.addPerson(person);
        long married = checkIfIsMarried(context, id, person.isMale());
        if (married > -1){
            node.addPerson(getPersonById(context, married));
        }
        List<Integer> childsId = addChildrenToPerson(context, id);
        for (int childId : childsId){
            Node childNode = addNodeFromID(context, childId);
            node.getChilds().add(childNode);
        }
        return node;
    }

    /**
     * create Person object from person id
     * @param context for ContentResolver
     * @param id to query for in Person table
     * @return the created Person object, or null if not exists
     */
    private static Person getPersonById(Context context, long id){
        Person person = null;
        Cursor cursor = context.getContentResolver().query(
                FamilyContract.PersonEntry.CONTENT_URI,
                null,
                FamilyContract.PersonEntry._ID + " = ?",
                new String[]{Long.toString(id)},
                null);
        if (cursor != null && cursor.moveToFirst()) {
            person = getPersonFromCursor(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        return person;
    }

    /**
     * find all children for Person in Relation table
     * @param context for ContentResolver
     * @param id to query for in Relation table
     * @return list for all childrens id for a Person
     */
    private static List<Integer> addChildrenToPerson(Context context, long id){
        List<Integer> list = new ArrayList<>();
        Cursor relationCursor = context.getContentResolver().query(
                FamilyContract.RelationEntry.CONTENT_URI,
                null,
                FamilyContract.RelationEntry.COLUMN_FIRST_ID + " = ? AND " + FamilyContract.RelationEntry.COLUMN_RELATION_TYPE + " = ? ",
                new String[]{Long.toString(id), Integer.toString(Relationship.VIEW_TYPE_HERITAGE)},
                null);
        if (relationCursor != null && relationCursor.moveToFirst()) {
            do {
                int seekedId = relationCursor.getInt(FamilyContract.RelationEntry.INDEX_SECOND_ID);
                list.add(seekedId);
            } while (relationCursor.moveToNext());
        }
        if (relationCursor != null) {
            relationCursor.close();
        }
        return list;
    }


}
