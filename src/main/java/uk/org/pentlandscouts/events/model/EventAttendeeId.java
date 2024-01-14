package uk.org.pentlandscouts.events.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;

public class EventAttendeeId implements Serializable {

    private String uid;

    private String sortKey;

    public EventAttendeeId()
    {
        this(null,null);
    }

    public EventAttendeeId(String uid, String sortKey)
    {
        this.uid = uid;
        this.sortKey = sortKey;
    }

    @DynamoDBHashKey
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @DynamoDBRangeKey
    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

}
