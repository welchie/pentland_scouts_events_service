package uk.org.pentlandscouts.events.model.domain;

import java.io.Serializable;
import java.util.Objects;

public class PersonalDetails implements Serializable {

    private String uid = "";

    private String firstName = "";

    private String lastName = "";

    private String dob= "";

    private String scoutSection = "";

    private String sectionName ="";
    private String scoutGroup = "";

    private String position = "";


    public PersonalDetails()
    {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalDetails that = (PersonalDetails) o;
        return Objects.equals(uid, that.uid) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(dob, that.dob) && Objects.equals(scoutSection, that.scoutSection) && Objects.equals(sectionName, that.sectionName) && Objects.equals(scoutGroup, that.scoutGroup) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, firstName, lastName, dob, scoutSection, sectionName, scoutGroup, position);
    }

    @Override
    public String toString() {
        return "PersonalDetails{" +
                "uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob='" + dob + '\'' +
                ", scoutSection='" + scoutSection + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", scoutGroup='" + scoutGroup + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
