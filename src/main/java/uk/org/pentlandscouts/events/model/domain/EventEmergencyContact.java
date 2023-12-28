package uk.org.pentlandscouts.events.model.domain;

public class EventEmergencyContact {

    private String uid = "";

    private String name = "";

    private String venue = "";

    private String startDate = "";

    private String endDate = "";
    private String contactNo = "";

    private String contactName = "";

    public EventEmergencyContact()
    {}

    public EventEmergencyContact(String uid, String name, String venue, String startDate, String endDate, String contactNo, String contactName)
    {
        this.setUid(uid);
        this.setName(name);
        this.setVenue(venue);
        this.setContactName(contactName);
        this.setContactNo(contactNo);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String toString() {
        return "EventEmergencyContact{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", venue='" + venue + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", contactName='" + contactName + '\'' +
                '}';
    }
}
