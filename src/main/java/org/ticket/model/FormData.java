package org.ticket.model;

/**
 * @author : R&M www.rmworking.com/blog
 *         2019/1/2 10:59
 *         ticket_12306
 *         org.ticket.model
 */
public class FormData {
    private String endTime;
    private String startTime;
    private String startStation;
    private String endStation;
    private String[] trainInfo;
    private String cookie;
    private Integer seatType;
    private String[] name;
    private String[] idCard;
    private String formName;
    private String toName;
    private String iphone;
    private String buyTicketTime;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public String[] getTrainInfo() {
        return trainInfo;
    }

    public void setTrainInfo(String[] trainInfo) {
        this.trainInfo = trainInfo;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Integer getSeatType() {
        return seatType;
    }

    public void setSeatType(Integer seatType) {
        this.seatType = seatType;
    }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public String[] getIdCard() {
        return idCard;
    }

    public void setIdCard(String[] idCard) {
        this.idCard = idCard;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getIphone() {
        return iphone;
    }

    public void setIphone(String iphone) {
        this.iphone = iphone;
    }

    public String getBuyTicketTime() {
        return buyTicketTime;
    }

    public void setBuyTicketTime(String buyTicketTime) {
        this.buyTicketTime = buyTicketTime;
    }
}
