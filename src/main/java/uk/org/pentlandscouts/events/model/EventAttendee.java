package uk.org.pentlandscouts.events.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.util.Objects;

@DynamoDbBean
@DynamoDBTable(tableName = "EventAttendee")
public class EventAttendee {

    @Id
    private String uid = "";

    private String sortKey = "";

    private String eventUid = "";

    private String personUid = "";

    private Boolean checkedIn = false;

    @DynamoDbPartitionKey
    @DynamoDBHashKey(attributeName = "uid")
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @DynamoDBAttribute(attributeName = "sortKey")
    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    @DynamoDBAttribute(attributeName = "eventUid")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "event_person-index",attributeName = "eventUid")
    @DynamoDbSecondarySortKey(indexNames = "event-person-index")
    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String name) {
        this.eventUid = eventUid;
    }

    @DynamoDBAttribute(attributeName = "personUid")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "event_person-index", attributeName = "personUid")
    @DynamoDbSecondaryPartitionKey(indexNames = "event_person-index")
    public String getPersonUid() {
        return personUid;
    }

    public void setPersonUid(String personUid) {
        this.personUid = personUid;
    }

    @DynamoDBAttribute(attributeName = "checkedIn")
    public Boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn =checkedIn;
    }

    public EventAttendee()
    {

    }

    public EventAttendee(String eventUid, String personUid)
    {
        this.setUid(EventUtils.generateType1UUID().toString());
        this.setEventUid(eventUid);
        this.setPersonUid(personUid);
        this.setCheckedIn(false);
        this.setSortKey(this.getEventUid() + this.getPersonUid() );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventAttendee that = (EventAttendee) o;
        return Objects.equals(uid, that.uid) && Objects.equals(sortKey, that.sortKey) && Objects.equals(eventUid, that.eventUid) && Objects.equals(personUid, that.personUid) && Objects.equals(checkedIn, that.checkedIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, sortKey, eventUid, personUid, checkedIn);
    }

    @Override
    public String toString() {
        return "EventAttendees{" +
                "uid='" + uid + '\'' +
                ", sortKey='" + sortKey + '\'' +
                ", eventUid='" + eventUid + '\'' +
                ", perosnUid='" + personUid + '\'' +
                ", checkedIn=" + checkedIn +
                '}';
    }
}
