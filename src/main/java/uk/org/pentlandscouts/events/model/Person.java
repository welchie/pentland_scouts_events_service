package uk.org.pentlandscouts.events.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.util.Objects;

@DynamoDbBean
@DynamoDBTable(tableName = "Person")
public class Person implements Comparable<Person>{


    //photo_permission, allergies, medical, dietary
    @Id
    private String uid = "";

    private String sortKey = "";

    private String firstName = "";

    private String lastName = "";

    private String dob = "";

    private String scoutSection = "";

    private String sectionName = "";
    private String scoutGroup = "";

    private String position = "";

    private String medicine = "";

    private String allergies = "";

    private String dietary = "";

    //Standard contact Details
    private String contactEmail = "";

    private String contactPhoneNo = "";

    //Emergency Contact Details
    private String emergencyContactName = "";

    private String emergencyContactNo = "";

    private String emergencyRelationship = "";


    private String photoPermission = "";

    private String subCamp = "";


//private Event[] events; //A list of Events the person is registered on ??

    public Person() {

    }

    public Person(String firstName, String lastName, String dob, String sortKey) {
        //Generate a UUID from the current time
        this.setUid(EventUtils.generateType1UUID().toString());
        this.setSortKey(sortKey);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setDob(dob);
    }

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


    @DynamoDBAttribute(attributeName = "firstName")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "firstname-lastname-index", attributeName = "firstName")
    @DynamoDbSecondarySortKey(indexNames = "firstname-lastname-index")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDBAttribute(attributeName = "lastName")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "firstname-lastname-index", attributeName = "lastName")
    @DynamoDbSecondaryPartitionKey(indexNames = "firstname-lastname-index")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DynamoDBAttribute(attributeName = "dob")
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @DynamoDBAttribute(attributeName = "scoutSection")
    public String getScoutSection() {
        return scoutSection;
    }

    public void setScoutSection(String scoutSection) {
        this.scoutSection = scoutSection;
    }

    @DynamoDBAttribute(attributeName = "sectionName")
    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    @DynamoDBAttribute(attributeName = "scoutGroup")
    public String getScoutGroup() {
        return scoutGroup;
    }

    public void setScoutGroup(String scoutGroup) {
        this.scoutGroup = scoutGroup;
    }

    @DynamoDBAttribute(attributeName = "position")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @DynamoDBAttribute(attributeName = "medicine")
    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    @DynamoDBAttribute(attributeName = "allergies")
    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    @DynamoDBAttribute(attributeName = "contactEmail")
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @DynamoDBAttribute(attributeName = "contactPhoneNo")
    public String getContactPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    @DynamoDBAttribute(attributeName = "emergencyContactName")
    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    @DynamoDBAttribute(attributeName = "emergencyContactNo")
    public String getEmergencyContactNo() {
        return emergencyContactNo;
    }

    public void setEmergencyContactNo(String emergencyContactNo) {
        this.emergencyContactNo = emergencyContactNo;
    }

    @DynamoDBAttribute(attributeName = "emergencyRelationship")
    public String getEmergencyRelationship() {
        return emergencyRelationship;
    }

    public void setEmergencyRelationship(String emergencyRelationship) {
        this.emergencyRelationship = emergencyRelationship;
    }

    @DynamoDBAttribute(attributeName = "dietary")
    public String getDietary() {
        return dietary;
    }

    public void setDietary(String dietary) {
        this.dietary = dietary;
    }

    @DynamoDBAttribute(attributeName = "photoPermission")
    public String getPhotoPermission() {
        return photoPermission;
    }

    public void setPhotoPermission(String photoPermission) {
        this.photoPermission = photoPermission;
    }

    @DynamoDBAttribute(attributeName = "subCamp")
    public String getSubCamp() {
        return subCamp;
    }

    public void setSubCamp(String subCamp) {
        this.subCamp = subCamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(uid, person.uid) && Objects.equals(sortKey, person.sortKey) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(dob, person.dob) && Objects.equals(scoutSection, person.scoutSection) && Objects.equals(sectionName, person.sectionName) && Objects.equals(scoutGroup, person.scoutGroup) && Objects.equals(position, person.position) && Objects.equals(medicine, person.medicine) && Objects.equals(allergies, person.allergies) && Objects.equals(dietary, person.dietary) && Objects.equals(contactEmail, person.contactEmail) && Objects.equals(contactPhoneNo, person.contactPhoneNo) && Objects.equals(emergencyContactName, person.emergencyContactName) && Objects.equals(emergencyContactNo, person.emergencyContactNo) && Objects.equals(emergencyRelationship, person.emergencyRelationship) && Objects.equals(photoPermission, person.photoPermission) && Objects.equals(subCamp, person.subCamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, sortKey, firstName, lastName, dob, scoutSection, sectionName, scoutGroup, position, medicine, allergies, dietary, contactEmail, contactPhoneNo, emergencyContactName, emergencyContactNo, emergencyRelationship, photoPermission, subCamp);
    }

    @Override
    public String toString() {
        return "Person{" +
                "uid='" + uid + '\'' +
                ", sortKey='" + sortKey + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob='" + dob + '\'' +
                ", scoutSection='" + scoutSection + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", scoutGroup='" + scoutGroup + '\'' +
                ", position='" + position + '\'' +
                ", medicine='" + medicine + '\'' +
                ", allergies='" + allergies + '\'' +
                ", dietary='" + dietary + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", contactPhoneNo='" + contactPhoneNo + '\'' +
                ", emergencyContactName='" + emergencyContactName + '\'' +
                ", emergencyContactNo='" + emergencyContactNo + '\'' +
                ", emergencyRelationship='" + emergencyRelationship + '\'' +
                ", photoPermission='" + photoPermission + '\'' +
                ", subCamp='" + subCamp + '\'' +
                '}';
    }

public int compareTo(Person obj)
        {
// we sort objects on the basis of Student Name using compareTo of String Class
        return this.sortKey.compareTo(obj.sortKey);
        }
}


