package uk.org.pentlandscouts.events.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.Id;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@DynamoDbBean
@DynamoDBTable(tableName = "Person")
public class Person {

    @Id
    private String uid = "";

    private String sortKey = "";

    private String firstName = "";

    private String lastName = "";

    private String dob= "";

    private String scoutSection = "";

    private String sectionName ="";
    private String scoutGroup = "";

    private String position = "";

    private String medicine = "";

    private String allergies = "";


//private Event[] events; //A list of Events the person is registered on ??

    public Person()
    {

    }

    public Person(String firstName, String lastName, String dob, String sortKey)
    {
        //Genrate a UUID from the current time
        this.setUid(generateType1UUID().toString());

        this.setUid(uid);
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
    @NotNull
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }


    @DynamoDBAttribute(attributeName = "firstName")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "firstname-lastname-index",attributeName = "firstName")
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


    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong | variant3BitFlag;
    }

    private static long get64MostSignificantBitsForVersion1() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
        final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
        final long version = 1 << 12;
        final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
        return time_low | time_mid | version | time_hi;
    }

    public static UUID generateType1UUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(uid, person.uid) && Objects.equals(sortKey, person.sortKey) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(dob, person.dob) && Objects.equals(scoutSection, person.scoutSection) && Objects.equals(sectionName, person.sectionName) && Objects.equals(scoutGroup, person.scoutGroup) && Objects.equals(position, person.position) && Objects.equals(medicine, person.medicine) && Objects.equals(allergies, person.allergies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, sortKey, firstName, lastName, dob, scoutSection, sectionName, scoutGroup, position, medicine, allergies);
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
                '}';
    }
}
