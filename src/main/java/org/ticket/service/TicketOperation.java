package org.ticket.service;

import org.ticket.model.FormData;

import java.util.Map;

/**
 * @author : R&M www.rmworking.com/blog
 *         2019/1/2 11:12
 *         ticket_12306
 *         org.ticket.service
 */
public interface TicketOperation {
    /**
     * @param data
     * @param getHeader
     * @param cookie
     */
    void initSubmitTicket(FormData data , Map<String, String> getHeader ,Map<String, String> cookie);
}
