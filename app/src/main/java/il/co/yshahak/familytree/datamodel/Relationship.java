package il.co.yshahak.familytree.datamodel;


public class Relationship {

    public static final int VIEW_TYPE_MARRIAGE = 0;
    public static final int VIEW_TYPE_HERITAGE = 1;
    public static final int VIEW_TYPE_ROOT = 2;

    private long firstId;
    private long secondId;
    private int relationType;

    public void setFirstId(long firstId) {
        this.firstId = firstId;
    }

    public void setSecondId(long secondId) {
        this.secondId = secondId;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }

    public long getFirstId() {
        return firstId;
    }

    public long getSecondId() {
        return secondId;
    }

    public int getRelationType() {
        return relationType;
    }
}
