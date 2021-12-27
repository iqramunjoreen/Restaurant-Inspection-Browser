package com.sfu.cmpt276groupproject.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent Restaurant inspections, and has necessary information including
 * the tracking number, date of inspection, list of violations, etc.
 */
public class Inspection {

    private String trackingNumber;
    private String date;
    private String type; // routine/ follow-up
    private int numberOfCritical;
    private int numberOfNonCritical;
    private Hazard hazard; //low, medium, high
    private List<Violation> violations = new ArrayList<>();

    /* Constructor */
    public Inspection(String trackingNumber, String date, String type, int numberOfCritical,
                      int numberOfNonCritical, Hazard hazard, List<Violation> violations) {
        this.trackingNumber = trackingNumber;
        this.date = date;
        this.type = type;
        this.numberOfCritical = numberOfCritical;
        this.numberOfNonCritical = numberOfNonCritical;
        this.hazard = hazard;
        this.violations = violations;
    }

    /* Getter methods */
    public String getTrackingNumber() { return trackingNumber; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public int getNumberOfCritical() { return numberOfCritical; }
    public int getNumberOfNonCritical() { return numberOfNonCritical; }
    public Hazard getHazard() { return hazard; }
    public List<Violation> getViolations() { return violations; }

    /* Setter methods */
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setDate(String date) { this.date = date; }
    public void setType(String type) { this.type = type; }
    public void setNumberOfCritical(int numberOfCritical) { this.numberOfCritical = numberOfCritical; }
    public void setNumberOfNonCritical(int numberOfNonCritical) { this.numberOfNonCritical = numberOfNonCritical; }
    public void setHazard(Hazard hazard) { this.hazard = hazard; }
    public void setViolations(List<Violation> violations) { this.violations = violations; }

}
