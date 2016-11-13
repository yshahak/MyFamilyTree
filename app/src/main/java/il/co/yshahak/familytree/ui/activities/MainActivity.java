package il.co.yshahak.familytree.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import il.co.yshahak.familytree.R;
import il.co.yshahak.familytree.datamodel.Node;
import il.co.yshahak.familytree.datamodel.Relationship;
import il.co.yshahak.familytree.datamodel.TreeManager;
import il.co.yshahak.familytree.ui.customview.TreeGroup;

public class MainActivity extends AppCompatActivity {

    public static final int CODE_PERSON_FORM_ROOT = 100;

    private int currentRootId;
    private TreeGroup treeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRootId = TreeManager.getTreeFirstDepthId(this);
        if (currentRootId > 0){
            setContentView(R.layout.activity_main);
            treeGroup = (TreeGroup) findViewById(R.id.tree_group_layout);
            setUi();
        } else {
            setContentView(R.layout.activity_main_first_run);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == CODE_PERSON_FORM_ROOT) { //after first creation we inflating differnt layout
                recreate();
            } else {
                treeGroup.removeAllViews();
                setUi();
            }
        }
    }



    private void setUi(){
        Node root = TreeManager.buildTreeFromID(this, currentRootId);
        treeGroup.init(root);
        treeGroup.findViewById(R.id.separators).setVisibility(View.INVISIBLE);
    }

    /**
     * change person id we start the draw the tree from
     * @param newId the new person id
     */
    public void setCurrentRootId(int newId){
        if (newId != currentRootId) {
            currentRootId = newId;
            treeGroup.removeAllViews();
            setUi();
        }
    }

    /*
         * when DB is empty we open form activity with depth = 1000, as this will be the entry point of depth, assuming we won't
         * need more than 1000 generation depth
         */
    public void openPersonForm(View view) {
        Intent intent = new Intent(this, PersonFormActivity.class);
        intent.putExtra(PersonFormActivity.EXTRE_DEPTH, 1000);
        intent.putExtra(PersonFormActivity.EXTRE_VIEW_TYPE, Relationship.VIEW_TYPE_ROOT);
        startActivityForResult(intent, CODE_PERSON_FORM_ROOT);
    }
}
