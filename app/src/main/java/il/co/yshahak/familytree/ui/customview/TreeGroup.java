package il.co.yshahak.familytree.ui.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import il.co.yshahak.familytree.R;
import il.co.yshahak.familytree.datamodel.Node;


/**
 * Custom LinearLayout who hold one depth of the tree
 */
public class TreeGroup extends LinearLayout {


    public TreeGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TreeGroup(Context context) {
        super(context);
    }

    /**
     * init this layer
     * @param node to populate the container from
     */
    public void init(Node node) {
        NodeView nodeView = (NodeView) LayoutInflater.from(getContext()).inflate(R.layout.cell_node, this, false);
        addView(nodeView);
        nodeView.init(node);

        if (node.getChilds().size() > 0) { //there is childs to add
            addChild(node);
        } else {
            findViewById(R.id.separator_vertical_down).setVisibility(INVISIBLE);
        }
    }

    /**
     * inflate node for next depth of this layer
     * @param node to populate with
     */
    private void addChild(Node node){
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(HORIZONTAL);
        params.weight = 1;
        addView(linearLayout);
        int index = 0;
        for (Node nodeChild : node.getChilds()){
            TreeGroup treeGroup = (TreeGroup) LayoutInflater.from(getContext()).inflate(R.layout.tree_group_layout, linearLayout, false);
            linearLayout.addView(treeGroup, params);
            treeGroup.init(nodeChild);
            if (index == 0){
                treeGroup.findViewById(R.id.separator_left).setVisibility(INVISIBLE);
            }
            if (index == node.getChilds().size() - 1){
                treeGroup.findViewById(R.id.separator_right).setVisibility(INVISIBLE);
            }
            index++;

        }
    }


}
