package uk.org.pentlandscouts.events.model.domain;

import java.io.Serializable;
import java.util.Objects;

public class MedicalDetails implements Serializable {

    private String uid = "";

    private String firstName = "";

    private String lastName = "";

    private String dob= "";



    private String medicine = "";

    private String allergies = "";


    public MedicalDetails()
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalDetails that = (MedicalDetails) o;
        return Objects.equals(uid, that.uid) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(dob, that.dob) && Objects.equals(medicine, that.medicine) && Objects.equals(allergies, that.allergies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, firstName, lastName, dob, medicine, allergies);
    }

    @Override
    public String toString() {
        return "MedicalDetails{" +
                "uid='" + uid + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob='" + dob + '\'' +
                ", medicine='" + medicine + '\'' +
                ", allergies='" + allergies + '\'' +
                '}';
    }
}
