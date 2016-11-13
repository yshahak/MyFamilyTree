package il.co.yshahak.familytree.datamodel;


/**
 * Class represent one person in the tree
 */
@SuppressWarnings("WeakerAccess")
public class Person {
    private int depth;
    private long personId;
    private String name;
    private boolean isMale;
    private long birthDay;


    public Person() {

    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public long getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public boolean isMale() {
        return isMale;
    }

}
