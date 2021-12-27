package com.sfu.cmpt276groupproject.Model;

/**
 * This class represents a violation of the Food Safe Act with information about the specific violation
 */
public class Violation {

    private String shortDescription;
    private String longDescription;
    private String severity;

    /* Constructor */
    public Violation(String shortDescription, String longDescription, String severity) {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.severity = severity;
    }

    /* Getter methods */
    public String getShortDescription() {
        return shortDescription;
    }
    public String getLongDescription() {
        return longDescription;
    }
    public String getSeverity() {
        return severity;
    }

    /* Setter methods */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
    public void setSeverity(String severity) {
        this.severity = severity;
    }

}
