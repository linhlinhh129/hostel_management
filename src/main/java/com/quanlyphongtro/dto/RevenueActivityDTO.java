package com.quanlyphongtro.dto;

public class RevenueActivityDTO {
    private String actorName;
    private String actionDescription;
    private String timeLabel;

    public RevenueActivityDTO() {}

    public RevenueActivityDTO(String actorName, String actionDescription, String timeLabel) {
        this.actorName = actorName;
        this.actionDescription = actionDescription;
        this.timeLabel = timeLabel;
    }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    public String getTimeLabel() { return timeLabel; }
    public void setTimeLabel(String timeLabel) { this.timeLabel = timeLabel; }
}
