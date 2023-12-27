package uk.org.pentlandscouts.events.model.domain;

import java.io.Serializable;
import java.util.Objects;

public class EmergencyContactDetails implements Serializable {

    private String uid = "";

    private String firstName = "";

    private String lastName = "";

    private String dob= "";



    private String emergencyContactName = "";

    private String emergencyContactNo = "";

    private String emergencyContactRelationship = "";


    public EmergencyContactDetails()
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

    public String getEmergencyContactRelationship() {
        return emergencyContactRelationship;
    }

    public void setEmergencyContactRelationship(String emergencyContactRelationship) {
        this.emergencyContactRelationship = emergencyContactRelationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmergencyContactDetails that = (EmergencyContactDetails) o;
        return Objects.equals(uid, that.uid) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(dob, that.dob) && Objects.equals(emergencyContactName, that.emergencyContactName) && Objects.equals(emergencyContactNo, that.emergencyContactNo) && Objects.equals(emergencyContactRelationship, that.emergencyContactRelationship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, firstName, lastName, dob, emergencyContactName, emergencyContactNo, emergencyContactRelationship);
    }

    @Override
    public String toString() {
        return "EmergencyContactDetails{" +
                "uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob='" + dob + '\'' +
                ", emergencyContactName='" + emergencyContactName + '\'' +
                ", emergencyContactNo='" + emergencyContactNo + '\'' +
                ", emergencyContactRelationship='" + emergencyContactRelationship + '\'' +
                '}';
    }
}
