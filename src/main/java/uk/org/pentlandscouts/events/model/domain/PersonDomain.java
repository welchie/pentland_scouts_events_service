package uk.org.pentlandscouts.events.model.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import uk.org.pentlandscouts.events.utils.EventUtils;

import java.util.Objects;

public class PersonDomain implements Comparable<PersonDomain>{


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


    private String checkedIn = "false";


    /**
     * URL to be used in the Web App to provide the URL to the Person page + UID
     */
    private String url = "";


//private Event[] events; //A list of Events the person is registered on ??

    public PersonDomain() {

    }

    public PersonDomain(String firstName, String lastName, String dob, String sortKey, String uid) {
        //Generate a UUID from the current time
        this.setUid(uid);
        this.setSortKey(sortKey);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setDob(dob);
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getScoutSection() {
        return scoutSection;
    }

    public void setScoutSection(String scoutSection) {
        this.scoutSection = scoutSection;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getScoutGroup() {
        return scoutGroup;
    }

    public void setScoutGroup(String scoutGroup) {
        this.scoutGroup = scoutGroup;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactNo() {
        return emergencyContactNo;
    }

    public void setEmergencyContactNo(String emergencyContactNo) {
        this.emergencyContactNo = emergencyContactNo;
    }

    public String getEmergencyRelationship() {
        return emergencyRelationship;
    }

    public void setEmergencyRelationship(String emergencyRelationship) {
        this.emergencyRelationship = emergencyRelationship;
    }
    public String getDietary() {
        return dietary;
    }

    public void setDietary(String dietary) {
        this.dietary = dietary;
    }

    public String getPhotoPermission() {
        return photoPermission;
    }

    public void setPhotoPermission(String photoPermission) {
        this.photoPermission = photoPermission;
    }

    public String getSubCamp() {
        return subCamp;
    }

    public void setSubCamp(String subCamp) {
        this.subCamp = subCamp;
    }

    public String getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(String checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDomain that = (PersonDomain) o;
        return Objects.equals(uid, that.uid) && Objects.equals(sortKey, that.sortKey) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(dob, that.dob) && Objects.equals(scoutSection, that.scoutSection) && Objects.equals(sectionName, that.sectionName) && Objects.equals(scoutGroup, that.scoutGroup) && Objects.equals(position, that.position) && Objects.equals(medicine, that.medicine) && Objects.equals(allergies, that.allergies) && Objects.equals(dietary, that.dietary) && Objects.equals(contactEmail, that.contactEmail) && Objects.equals(contactPhoneNo, that.contactPhoneNo) && Objects.equals(emergencyContactName, that.emergencyContactName) && Objects.equals(emergencyContactNo, that.emergencyContactNo) && Objects.equals(emergencyRelationship, that.emergencyRelationship) && Objects.equals(photoPermission, that.photoPermission) && Objects.equals(subCamp, that.subCamp) && Objects.equals(checkedIn, that.checkedIn) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, sortKey, firstName, lastName, dob, scoutSection, sectionName, scoutGroup, position, medicine, allergies, dietary, contactEmail, contactPhoneNo, emergencyContactName, emergencyContactNo, emergencyRelationship, photoPermission, subCamp, checkedIn, url);
    }

    @Override
    public String toString() {
        return "PersonDomain{" +
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
                ", checkedIn='" + checkedIn + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public int compareTo(PersonDomain obj)
        {
// we sort objects on the basis of Student Name using compareTo of String Class
        return this.sortKey.compareTo(obj.sortKey);
        }
}


