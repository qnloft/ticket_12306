package org.ticket.model;

/**
 * @author : R&M www.rmworking.com/blog
 *         2019/1/2 10:58
 *         ticket_12306
 *         org.ticket.model
 */
public class Ticket {
    private Data data;
    private Integer httpstatus;
    private String messages;
    private Boolean status;

    public Integer getHttpstatus() {
        return httpstatus;
    }

    public void setHttpstatus(Integer httpstatus) {
        this.httpstatus = httpstatus;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
