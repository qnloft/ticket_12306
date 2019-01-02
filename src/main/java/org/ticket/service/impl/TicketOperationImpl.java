package org.ticket.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.google.common.base.Objects;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.ticket.model.Data;
import org.ticket.model.FormData;
import org.ticket.model.Ticket;
import org.ticket.service.TicketOperation;
import org.ticket.util.DateUtil;
import org.ticket.util.JsoupConnectUtil;
import org.ticket.util.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author : R&M www.rmworking.com/blog
 *         2019/1/2 11:17
 *         ticket_12306
 *         org.ticket.service
 */
public class TicketOperationImpl implements TicketOperation {

    private static final Logger logger = Logger.getLogger(TicketOperationImpl.class.getName());

    private String token;
    private String key;

    private String oldPassengerStr;
    private String passengerTicketStr;
    private String trainDate;

    @Override
    public void initSubmitTicket(FormData data, Map<String, String> getHeader, Map<String, String> cookie) {

        this.oldPassengerStr = getOldPassengerStr(data);
        this.passengerTicketStr = getPassengerStr(data);
        this.trainDate = StringUtil.urlEncode(DateUtil.getGMTDate(data.getEndTime(), DateUtil.DATE_FORMAT_YYYY_MM_DD));

        while (true) {
            try {
                // 获取车辆信息
                String[] info = search(data, getHeader);
                if (info != null && info.length > 0) {
                    // 验证用户
                    String checkUser = checkUser(getHeader);
                    // 请求下单接口
                    submitOrderRequest(info, data, getHeader);
                    // 初始化token和key
                    initDoc(cookie);
                    System.out.println(this.token + "   " + this.key);
                    if (!StringUtil.isEmpty(token) && !StringUtil.isEmpty(key)) {
                        // 验证订单
                        Ticket checkOrder = checkOrderInfo(getHeader);
                        if (checkOrder != null) {
                            if (checkOrder.getStatus() && checkOrder.getData() != null) {
                                // 查询余票
                                Ticket ticketCount = getQueueCount(info, data.getSeatType(), getHeader);
                                if (ticketCount != null) {
                                    if (ticketCount.getStatus() && ticketCount.getData() != null) {
                                        if (ticketCount.getData().getTicket() > 0) {
                                            // 提交订单
                                            Ticket result = confirmSingleForQueue(info, getHeader);
                                            if (result != null) {
                                                if (result.getStatus() && StringUtil.isEmpty(result.getMessages())) {
                                                    logger.info("下单成功了~！！");
                                                } else {
                                                    // 下单失败了
                                                    logger.warning(ticketCount.getMessages());
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        // 没有票了
                                        logger.warning(ticketCount.getMessages());
                                        break;
                                    }
                                }
                            } else {
                                // 订单校验失败了~！
                                logger.warning(checkOrder.getMessages());
                                break;
                            }
                        }
                    }
                }
            } catch (JSONException | IOException | InterruptedException json) {
                logger.warning(json.getMessage());
            }
        }

    }

    /**
     * 查询车票信息
     *
     * @param data
     * @param header
     * @return
     */
    private String[] search(FormData data, Map<String, String> header) {
        String url = StringUtil.replace("https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date={}&leftTicketDTO.from_station={}&leftTicketDTO.to_station={}&purpose_codes=ADULT"
                , data.getEndTime(), data.getStartStation(), data.getEndStation());
        Long buyTicketTimeSleep;
        while (true) {
            String result = null;
            long millis = new Random().nextInt(3000);
            try {
                result = JsoupConnectUtil.connectionJson(url, null, 4000, null, null, header, Connection.Method.GET, null, null, "UTF-8");
                Ticket ticket = JSON.parseObject(result, Ticket.class);
                if (ticket != null) {
                    logger.info("查询到票信息，处理中....");
                    if (ticket.getStatus() && ticket.getHttpstatus() == 200) {
                        if (ticket.getData() != null) {
                            Data resultData = ticket.getData();
                            List<String> ticketDatas = resultData.getResult();
                            if (ticketDatas != null && ticketDatas.size() != 0) {
                                for (String ticketData : ticketDatas) {
                                    String[] datas = ticketData.split("\\|");
                                    if (datas.length > 1) {
                                        for (String s : data.getTrainInfo()) {
                                            if (Objects.equal(datas[3], s)) {
                                                if (!StringUtil.isEmpty(datas[0])) {
                                                    // 坐席是否有票
                                                    if (printDatas(datas, data.getSeatType())) {
                                                        return datas;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    logger.info("接口中没有数据返回....");
                }
                // 支持设置开始时间，如果开始时间小于当前时间，则计算休眠时间，让线程休眠到设置的指定时间的前10S
                if (!StringUtil.isEmpty(data.getBuyTicketTime())) {
                    buyTicketTimeSleep = DateUtil.parseStrToDate(data.getBuyTicketTime(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS).getTime() - System.currentTimeMillis();
                    if (buyTicketTimeSleep > millis + 10000L) {
                        millis = buyTicketTimeSleep - 10000L;
                    }
                }
                logger.info("查询的票没有开售~！线程等待.....【" + millis + "】 毫秒");
                Thread.sleep(millis);

            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
    }

    /**
     * 打印车辆信息
     *
     * @param datas
     * @param seatType 坐席类型
     * @return
     */
    private Boolean printDatas(String[] datas , Integer seatType) {
        StringBuilder sb = new StringBuilder();
        sb.append("车次：");
        sb.append(datas[3]);
        sb.append(" 软卧：");
        sb.append(Objects.equal(datas[23], "") ? "--" : datas[23]);
        sb.append(" 硬卧：");
        sb.append(Objects.equal(datas[28], "") ? "--" : datas[28]);
        sb.append(" 硬座：");
        sb.append(Objects.equal(datas[29], "") ? "--" : datas[29]);
        sb.append(" 无座：");
        sb.append(Objects.equal(datas[26], "") ? "--" : datas[26]);
        System.out.println(sb.toString());
        if (seatType == null) {
            return false;
        }
//        硬座
        if (seatType == 1) {
            return Objects.equal("有", datas[29]);
        } else if (seatType == 3) {
//            硬卧
            return Objects.equal("有", datas[28]);
        } else if (seatType == 4) {
//            软卧
            return Objects.equal("有", datas[23]);
        }
        return false;
    }

    /**
     * 用户验证接口
     *
     * @param header
     * @return
     * @throws IOException
     */
    private String checkUser(Map<String, String> header) throws IOException {
        String url = "https://kyfw.12306.cn/otn/login/checkUser";
        String result = null;
        result = JsoupConnectUtil.connectionJson(url, null, 4000, null, null, header, Connection.Method.POST, null, null, "UTF-8");
        System.out.println(result);
        return result;
    }

    /**
     * 提交订单接口
     *
     * @param datas
     * @return
     */
    private Ticket submitOrderRequest(String[] datas, FormData data, Map<String, String> header) throws IOException {
        if (datas.length > 1) {
            String url = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";

            String text = "secretStr=" + datas[0] +
                    "&train_date=" + data.getEndTime() +
                    "&back_train_date=" + data.getStartTime() +
                    "&tour_flag=dc" +
                    "&purpose_codes=ADULT" +
                    "&query_from_station_name=" + data.getFormName() +
                    "&query_to_station_name=" + data.getToName();
            System.out.println("---------------提交订单-----------------------");
            System.out.println(text);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("text", text);
            String result = JsoupConnectUtil.connectionJson(url, paramMap, 4000, null, null, header, Connection.Method.POST, null, null, "UTF-8");
            System.out.println(result);

            System.out.println("--------------------------------------");
        }
        return null;
    }


    /**
     * 初始化数据
     *
     * @param cookie
     * @throws IOException
     */
    private void initDoc(Map<String, String> cookie) throws IOException {
        String url = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
        Document result = null;

        result = JsoupConnectUtil.connectionUrl(url, 4000, Connection.Method.POST, cookie);
        if (result != null) {
            String html = result.html();
            // 获取token和key
            String tokenReg = "globalRepeatSubmitToken = '(.+?)'";
            String keyReg = "key_check_isChange':'(.+?)'";
            this.token = StringUtil.getMatcher(tokenReg, html);
            this.key = StringUtil.getMatcher(keyReg, html);
        }


    }

    /**
     * 乘车人信息查询接口
     *
     * @param header
     * @return
     * @throws IOException
     */
    private String getPassenger(Map<String, String> header) throws IOException {
        String url = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("text", this.token);
        String result = null;
        result = JsoupConnectUtil.connectionJson(url, paramMap, 4000, null, null, header, Connection.Method.POST, null, null, "UTF-8");
        System.out.println(result);
        return null;
    }

    /**
     * 订单验证接口
     *
     * @return
     */
    private Ticket checkOrderInfo(Map<String, String> header) throws IOException, InterruptedException {
        String url = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
        String text = "cancel_flag=2" +
                "&bed_level_order_num=000000000000000000000000000000" +
                "&passengerTicketStr=" + this.passengerTicketStr +
                "&oldPassengerStr=" + this.oldPassengerStr +
                "&tour_flag=dc" +
                "&randCode=" +
                "&whatsSelect=1" +
                "&_json_att=" +
                "&REPEAT_SUBMIT_TOKEN=" + this.token;
        System.out.println("---------------订单验证-----------------------");
        System.out.println(text);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("text", text);
        String result = JsoupConnectUtil.connectionJson(url, paramMap, 4000, null, null, header, Connection.Method.POST, null, null, "UTF-8");
        System.out.println(result);
        Ticket checkOrderInfo = null;
        if (result != null) {
            checkOrderInfo = JSON.parseObject(result, Ticket.class);
            if (checkOrderInfo.getStatus()) {
                Data data = checkOrderInfo.getData();
                if (data != null) {
                    if (data.getIfShowPassCodeTime() != null) {
                        // 线程休息，等待12306处理
                        Thread.sleep(data.getIfShowPassCodeTime() + 1000);
                    }
                }
            }
        }
        System.out.println("--------------------------------------");
        return checkOrderInfo;
    }

    /**
     * 余票数量查询接口
     *
     * @param datas
     * @return
     */
    private Ticket getQueueCount(String[] datas, Integer seatType, Map<String, String> header) throws IOException {
        if (datas.length > 1) {
            String url = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
            String text = "train_date=" + this.trainDate + "+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)" +
                    "&train_no=" + datas[2] +
                    "&stationTrainCode=" + datas[3] +
                    "&seatType=" + seatType +
                    "&fromStationTelecode=" + datas[4] +
                    "&toStationTelecode=" + datas[5] +
                    "&leftTicket=" + datas[12] +
                    "&purpose_codes=00" +
                    "&train_location=" + datas[15] +
                    "&_json_att=" +
                    "&REPEAT_SUBMIT_TOKEN=" + this.token;
            System.out.println("---------------余票数量-----------------------");
            System.out.println(text);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("text", text);
            String result = JsoupConnectUtil.connectionJson(url, paramMap, 4000, null, null, header, Connection.Method.POST, null, null, "UTF-8");
            System.out.println(result);
            if (!StringUtil.isEmpty(result)) {
                return JSON.parseObject(result, Ticket.class);
            }
            System.out.println("--------------------------------------");
        }

        return null;
    }

    /**
     * 下单接口
     *
     * @param datas
     */
    private Ticket confirmSingleForQueue(String[] datas, Map<String, String> header) throws IOException {
        String url = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";

        String text = "passengerTicketStr=" + this.passengerTicketStr +
                "&oldPassengerStr=" + this.oldPassengerStr +
                "&randCode=" +
                "&purpose_codes=00" +
                "&key_check_isChange=" + this.key +
                "&leftTicketStr=" + datas[12] +
                "&train_location=" + datas[15] +
                "&choose_seats=" +
                "&seatDetailType=000" +
                "&whatsSelect=1" +
                "&roomType=00" +
                "&dwAll=N" +
                "&_json_att=" +
                "&REPEAT_SUBMIT_TOKEN=" + this.token;
        System.out.println("---------------下单接口-----------------------");
        System.out.println(text);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("text", text);
        String result = JsoupConnectUtil.connectionJson(url, paramMap, 4000, null, null, header, Connection.Method.POST, null, null, "UTF-8");
        System.out.println(result);
        if (!StringUtil.isEmpty(result)) {
            return JSON.parseObject(result, Ticket.class);
        }
        System.out.println("--------------------------------------");
        return null;
    }

    private String getPassengerStr(FormData data) {
        if (data.getName().length > 0 && data.getIdCard().length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.getName().length; i++) {
                if (i == 0) {
                    sb.append(data.getSeatType());
                    sb.append(",");
                } else {
                    sb.append("N_3");
                    sb.append(",");
                }
                sb.append("0,1,");
                sb.append(data.getName()[i]);
                sb.append(",1,");
                sb.append(data.getIdCard()[i]);
                if (i == 0) {
                    sb.append(",");
                    sb.append(data.getIphone());
                    sb.append(",");
                } else {
                    sb.append(",,");
                }
                if (i == data.getName().length - 1) {
                    sb.append("N");
                }
            }
            return sb.toString();
        }
        return null;
    }

    private String getOldPassengerStr(FormData data) {
        if (data.getName().length > 0 && data.getIdCard().length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.getName().length; i++) {
                if (i == 0) {
                    sb.append(data.getName()[i]);
                    sb.append(",");
                } else {
                    sb.append(",1_");
                    sb.append(data.getName()[i]);
                    sb.append(",");
                }
                sb.append("1,");
                sb.append(data.getIdCard()[i]);
                if (i == data.getName().length - 1) {
                    sb.append(",1_");
                }
            }
            return sb.toString();
        }
        return null;
    }
}
