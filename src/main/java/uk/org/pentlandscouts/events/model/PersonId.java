package uk.org.pentlandscouts.events.model;

import java.io.Serializable;

public class PersonId implements Serializable {

    private String uid;

    private String sortKey;

    public PersonId()
    {
        this(null,null);
    }

    public PersonId(String uid, String sortKey)
    {
        this.uid = uid;
        this.sortKey = sortKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

}
