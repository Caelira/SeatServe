package org.example.angelsburgerpos.model;

public class ArchiveRecord {
    private String date;
    private String type;
    private String id;
    private String name;
    private String details;
    private String reason;

    public ArchiveRecord(String date, String type, String id, String name, String details, String reason) {
        this.date = date;
        this.type = type;
        this.id = id;
        this.name = name;
        this.details = details;
        this.reason = reason;
    }

    public String getDate() { return date; }
    public String getType() { return type; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDetails() { return details; }
    public String getReason() { return reason; }
}