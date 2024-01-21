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

    //first_name, last_name, age_at_start, contact_email_address, photo_permission, allergies, medical, dietary
    @Id
    private String uid = "";

    private String sortKey = "";

    private String eventUid = "";

    private String personUid = "";

    private String checkedIn = "false";

    private String photoPermission = "false";

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
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "event-person-index",attributeName = "eventUid")
    @DynamoDbSecondarySortKey(indexNames = "event-person-index")
    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    @DynamoDBAttribute(attributeName = "personUid")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "event-person-index", attributeName = "personUid")
    @DynamoDbSecondaryPartitionKey(indexNames = "event-person-index")
    public String getPersonUid() {
        return personUid;
    }

    public void setPersonUid(String personUid) {
        this.personUid = personUid;
    }

    @DynamoDBAttribute(attributeName = "checkedIn")
    public String getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(String checkedIn) {
        this.checkedIn =checkedIn;
    }

    @DynamoDBAttribute(attributeName = "photoPermission")
    public String getPhotoPermission() {
        return photoPermission;
    }

    public void setPhotoPermission(String photoPermission) {
        this.photoPermission =photoPermission;
    }

    public EventAttendee()
    {

    }

    public EventAttendee(String eventUid, String personUid, String photoPermission)
    {
        this.setUid(EventUtils.generateType1UUID().toString());
        this.setEventUid(eventUid);
        this.setPersonUid(personUid);
        this.setCheckedIn("false");
        this.setPhotoPermission(photoPermission);
        this.setSortKey(this.getEventUid() + this.getPersonUid() );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventAttendee that = (EventAttendee) o;
        return Objects.equals(uid, that.uid) && Objects.equals(sortKey, that.sortKey) && Objects.equals(eventUid, that.eventUid) && Objects.equals(personUid, that.personUid) && Objects.equals(checkedIn, that.checkedIn) && Objects.equals(photoPermission, that.photoPermission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, sortKey, eventUid, personUid, checkedIn, photoPermission);
    }

    @Override
    public String toString() {
        return "EventAttendee{" +
                "uid='" + uid + '\'' +
                ", sortKey='" + sortKey + '\'' +
                ", eventUid='" + eventUid + '\'' +
                ", personUid='" + personUid + '\'' +
                ", checkedIn=" + checkedIn +
                ", photoPermission=" + photoPermission +
                '}';
    }
}
