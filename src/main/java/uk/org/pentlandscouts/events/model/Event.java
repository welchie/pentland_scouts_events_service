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
@DynamoDBTable(tableName = "Event")
public class Event {

    @Id
    private String uid = "";

    private String sortKey = "";

    private String name = "";

    private String venue = "";

    private String startDate = "";

    private String endDate = "";

    private Integer attendanceLimit = 0;

    private String emergencyContactNo = "";

    private String emergencyContactName = "";

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

    @DynamoDBAttribute(attributeName = "name")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "name-venue-index",attributeName = "name")
    @DynamoDbSecondarySortKey(indexNames = "name-venue-index")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "venue")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "name-venue-index", attributeName = "venue")
    @DynamoDbSecondaryPartitionKey(indexNames = "name-venue-index")
    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    @DynamoDBAttribute(attributeName = "startDate")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @DynamoDBAttribute(attributeName = "endDate")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @DynamoDBAttribute(attributeName = "attendanceLimit")
    public Integer getAttendanceLimit() {
        return attendanceLimit;
    }

    public void setAttendanceLimit(Integer attendanceLimit) {
        this.attendanceLimit = attendanceLimit;
    }

    @DynamoDBAttribute(attributeName = "emergencyContactNo")
    public String getEmergencyContactNo() {
        return emergencyContactNo;
    }

    public void setEmergencyContactNo(String emergencyContactNo) {
        this.emergencyContactNo = emergencyContactNo;
    }

    @DynamoDBAttribute(attributeName = "emergencyContactName")
    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public Event()
    {

    }

    public Event(String name, String venue, String startDate, String endDate)
    {
        this.setUid(EventUtils.generateType1UUID().toString());
        this.setName(name);
        this.setVenue(venue);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setSortKey(this.getName() + this.getVenue() + this.getStartDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(uid, event.uid) && Objects.equals(sortKey, event.sortKey) && Objects.equals(name, event.name) && Objects.equals(venue, event.venue) && Objects.equals(startDate, event.startDate) && Objects.equals(endDate, event.endDate) && Objects.equals(attendanceLimit, event.attendanceLimit) && Objects.equals(emergencyContactNo, event.emergencyContactNo) && Objects.equals(emergencyContactName, event.emergencyContactName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, sortKey, name, venue, startDate, endDate, attendanceLimit, emergencyContactNo, emergencyContactName);
    }

    @Override
    public String toString() {
        return "Event{" +
                "uid='" + uid + '\'' +
                ", sortKey='" + sortKey + '\'' +
                ", name='" + name + '\'' +
                ", venue='" + venue + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", attendanceLimit=" + attendanceLimit +
                ", emergencyContactNo='" + emergencyContactNo + '\'' +
                ", emergencyContactName='" + emergencyContactName + '\'' +
                '}';
    }
}
