package il.co.yshahak.familytree.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import il.co.yshahak.familytree.R;
import il.co.yshahak.familytree.datamodel.Node;
import il.co.yshahak.familytree.datamodel.Person;
import il.co.yshahak.familytree.datamodel.Relationship;
import il.co.yshahak.familytree.datamodel.TreeManager;
import il.co.yshahak.familytree.ui.activities.MainActivity;
import il.co.yshahak.familytree.ui.activities.PersonFormActivity;

/**
 * Custom LinearLayout holds one Node of the tree
 */
@SuppressLint("SimpleDateFormat")

public class NodeView extends LinearLayout implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private Node node;
    private Person clickedPerson;
    private static String personPlaceHolder, personDetails;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public NodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NodeView(Context context) {
        super(context);
    }

    public void init(Node node){
        if (personPlaceHolder == null) { //using static as no need for more to duplicate it for all instances
            personPlaceHolder = getContext().getString(R.string.draw_from_here);
            personDetails = getContext().getString(R.string.person_details);
        }
        this.node = node;
        TextView male = (TextView) findViewById(R.id.dad);
        TextView female = (TextView) findViewById(R.id.mom);
        View divider = findViewById(R.id.node_divider);
        female.setOnClickListener(this);
        male.setOnClickListener(this);
        divider.setVisibility(node.isMarried() ? View.VISIBLE : View.GONE);

        if (node.getTheMale() != null){
            male.setText(node.getTheMale().getName());
            male.setTag(R.string.tag_person, node.getTheMale());
        } else {
            male.setVisibility(View.GONE);
        }
        if (node.getTheFemale() != null){
            female.setText(node.getTheFemale().getName());
            female.setTag(R.string.tag_person, node.getTheFemale());
        }else {
            female.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        clickedPerson = (Person)v.getTag(R.string.tag_person);
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        int id = node.isMarried() ? R.menu.add_menu_married : R.menu.add_menu_single;
        inflater.inflate(id, popup.getMenu());
        popup.getMenu().findItem(R.id.display_from_here).setTitle(String.format(personPlaceHolder, clickedPerson.getName()));
        popup.getMenu().findItem(R.id.person_details).setTitle(String.format(personDetails, clickedPerson.getName()));
        popup.getMenu().findItem(R.id.person_id).setTitle("ID: " + Long.toString(clickedPerson.getPersonId()));
        Date date = new Date(clickedPerson.getBirthDay());
        popup.getMenu().findItem(R.id.person_birth_day).setTitle("Birthday:" + dateFormat.format(date));

        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent intent = new Intent(getContext(), PersonFormActivity.class);
        int code;
        switch (item.getItemId()){
            case R.id.display_from_root:
                ((MainActivity)getContext()).setCurrentRootId(TreeManager.getTreeFirstDepthId(getContext()));
                return true;
            case R.id.display_from_here:
                ((MainActivity)getContext()).setCurrentRootId((int) node.getSinglePerson().getPersonId());
                return true;
            case R.id.edit_person_detail:
                intent.putExtra(PersonFormActivity.EXTRE_PERSON_NAME, clickedPerson.getName());
                intent.putExtra(PersonFormActivity.EXTRE_PERSON_BD, clickedPerson.getBirthDay());
                intent.putExtra(PersonFormActivity.EXTRE_PERSON_ID, clickedPerson.getPersonId());
                intent.putExtra(PersonFormActivity.EXTRE_DEPTH, node.getDepth());
                intent.putExtra(PersonFormActivity.EXTRE_GENDER,
                        clickedPerson.isMale() ? PersonFormActivity.EXTRE_MALE : PersonFormActivity.EXTRE_FEMALE);
                intent.putExtra(PersonFormActivity.EXTRE_VIEW_TYPE, PersonFormActivity.CODE_PERSON_FORM_EDIT);
                code = PersonFormActivity.CODE_PERSON_FORM_EDIT;
                break;
            case R.id.add_partner:
                intent.putExtra(PersonFormActivity.EXTRE_DEPTH, node.getDepth());
                intent.putExtra(PersonFormActivity.EXTRE_VIEW_TYPE, Relationship.VIEW_TYPE_MARRIAGE);
                PersonFormActivity.existPerson = clickedPerson;
                intent.putExtra(PersonFormActivity.EXTRE_GENDER,
                        clickedPerson.isMale() ? PersonFormActivity.EXTRE_FEMALE : PersonFormActivity.EXTRE_MALE);
                code = PersonFormActivity.CODE_PERSON_FORM_MARRIAGE;
                break;
            case R.id.add_child:
                PersonFormActivity.existPerson = node.getTheMale();
                PersonFormActivity.secondExistsPerson = node.getTheFemale();
                intent.putExtra(PersonFormActivity.EXTRE_VIEW_TYPE, Relationship.VIEW_TYPE_HERITAGE);
                intent.putExtra(PersonFormActivity.EXTRE_DEPTH, node.getDepth() + 1);
                code = PersonFormActivity.CODE_PERSON_FORM_HERITAGE;
                break;
            case R.id.delete_person:
                TreeManager.deletePersonFromDb(getContext(), clickedPerson);
                ((MainActivity)getContext()).recreate();
                return true;
            default:
                return true;
        }
        ((MainActivity)getContext()).startActivityForResult(intent, code);
        return true;
    }
}
