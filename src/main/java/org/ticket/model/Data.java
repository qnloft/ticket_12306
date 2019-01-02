package org.ticket.model;

import java.util.List;

/**
 * @author : R&M www.rmworking.com/blog
 *         2019/1/2 10:59
 *         ticket_12306
 *         org.ticket.model
 */
public class Data {
    private String flag;
    private List<String> result;
    private String ifShowPassCode;
    private String canChooseBeds;
    private String canChooseSeats;
    private String choose_Seats;
    private String isCanChooseMid;
    /**
     * 下单等待时间
     */
    private Integer ifShowPassCodeTime;
    private Boolean submitStatus;
    private String smokeStr;

    /**
     * 总票数
     */
    private Integer count;
    /**
     * 当前余票
     */
    private Integer ticket;
    private Boolean op_2;
    private Integer countT;
    private Boolean op_1;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public String getIfShowPassCode() {
        return ifShowPassCode;
    }

    public void setIfShowPassCode(String ifShowPassCode) {
        this.ifShowPassCode = ifShowPassCode;
    }

    public String getCanChooseBeds() {
        return canChooseBeds;
    }

    public void setCanChooseBeds(String canChooseBeds) {
        this.canChooseBeds = canChooseBeds;
    }

    public String getCanChooseSeats() {
        return canChooseSeats;
    }

    public void setCanChooseSeats(String canChooseSeats) {
        this.canChooseSeats = canChooseSeats;
    }

    public String getChoose_Seats() {
        return choose_Seats;
    }

    public void setChoose_Seats(String choose_Seats) {
        this.choose_Seats = choose_Seats;
    }

    public String getIsCanChooseMid() {
        return isCanChooseMid;
    }

    public void setIsCanChooseMid(String isCanChooseMid) {
        this.isCanChooseMid = isCanChooseMid;
    }

    public Integer getIfShowPassCodeTime() {
        return ifShowPassCodeTime;
    }

    public void setIfShowPassCodeTime(Integer ifShowPassCodeTime) {
        this.ifShowPassCodeTime = ifShowPassCodeTime;
    }

    public Boolean getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(Boolean submitStatus) {
        this.submitStatus = submitStatus;
    }

    public String getSmokeStr() {
        return smokeStr;
    }

    public void setSmokeStr(String smokeStr) {
        this.smokeStr = smokeStr;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTicket() {
        return ticket;
    }

    public void setTicket(Integer ticket) {
        this.ticket = ticket;
    }

    public Boolean getOp_2() {
        return op_2;
    }

    public void setOp_2(Boolean op_2) {
        this.op_2 = op_2;
    }

    public Integer getCountT() {
        return countT;
    }

    public void setCountT(Integer countT) {
        this.countT = countT;
    }

    public Boolean getOp_1() {
        return op_1;
    }

    public void setOp_1(Boolean op_1) {
        this.op_1 = op_1;
    }
}
