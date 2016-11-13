package il.co.yshahak.familytree.datamodel;

import java.util.ArrayList;

/**
 * class representing family cell with at least one descendant
 * @see Person
 */
@SuppressWarnings("WeakerAccess")
public class Node {

    private Person theMale;
    private Person theFemale;
    private ArrayList<Node> childs = new ArrayList<>();

    public void setChilds(ArrayList<Node> childs) {
        this.childs = childs;
    }

    public void setTheMale(Person theMale) {
        this.theMale = theMale;
    }

    public void setTheFemale(Person theFemale) {
        this.theFemale = theFemale;
    }

    public ArrayList<Node> getChilds() {
        return childs;
    }

    public Person getTheMale() {
        return theMale;
    }

    public Person getTheFemale() {
        return theFemale;
    }

    public int getDepth(){
        return getSinglePerson().getDepth();
    }

    /**
     * check if this node contain single person or married couple
     * @return true if married, false if single
     */
    public boolean isMarried(){
        return theMale != null && theFemale != null;
    }

    /**
     * if the Node contain single Person, ruturn it;
     * @return the single Person inside this Node
     */
    public Person getSinglePerson(){
        return theMale != null ? theMale : theFemale;
    }

    /**
     * add Person to it right spot inside the node
     * @param person the person to add
     */
    public void addPerson(Person person) {
        if (person.isMale()){
            setTheMale(person);
        } else {
            setTheFemale(person);
        }
    }
}
