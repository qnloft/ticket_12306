package org.ticket;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.ticket.model.FormData;
import org.ticket.service.TicketOperation;
import org.ticket.service.impl.TicketOperationImpl;
import org.ticket.util.DateUtil;
import org.ticket.util.StringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author : R&M www.rmworking.com/blog
 *         2019/1/2 14:33
 *         ticket_12306
 *         org.ticket
 */
public class TicketMain {

    public static void main(String[] args) {
        FormData data = new FormData();
        //        当前日期
        String startTime = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
        data.setStartTime(startTime);
//        始发站 缩写
        String startStation = "BXP";
        data.setStartStation(startStation);
//        TODO 终点站英文 缩写
        String endStation = "";
        data.setEndStation(endStation);
        String formName = "北京";
        data.setFormName(formName);
//        TODO 终点站中文名称
        String toName = "";
        data.setToName(toName);
        // 席别 硬卧：3 硬座：1 软卧：4
        Integer seatType = 3;
        data.setSeatType(seatType);
        //  发车日期
        String endTime = "2019-01-29";
        data.setEndTime(endTime);
        //  抢票时间
        String buyTicketTime = startTime + " 10:00:00";
        data.setBuyTicketTime(buyTicketTime);
        //  TODO 车次 可选择多个车辆
        String[] trainInfo = {""};
        data.setTrainInfo(trainInfo);
        // TODO 乘车人 可选择多个乘车人
        String[] name = {""};
        data.setName(name);
        // TODO 身份证号 与乘车人对应
        String[] idCard = {""};
        data.setIdCard(idCard);
        // TODO 有N个联系人时，联系方式，填写一个即可
        data.setIphone("");
        new TicketMain(data);
    }

    private static final Logger logger = Logger.getLogger(TicketMain.class.getName());

    private WebDriver driver = null;
    private String cookie;
    private FormData data;

    public TicketMain(FormData data) {
        this.data = data;
        // 指定chrome驱动位置
        System.setProperty("webdriver.chrome.driver", "/Users/renmeng/Desktop/selenium/chromedriver");
        this.driver = new ChromeDriver();
        driver.manage().deleteAllCookies();
        try {
            loginInit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            quit();
        }
    }

    private void loginInit() throws InterruptedException{
        Set<Cookie> cookies;
        driver.get("https://www.12306.cn/");

        cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName());
        }
        System.out.println("------------------------");

        // 发起请求，获取页面
        driver.get("https://kyfw.12306.cn/otn/resources/login.html");
        driver.findElement(By.className("login-hd-account")).click();
        Thread.sleep(2000);

        // TODO 12306.cn网站的用户名
        driver.findElement(By.id("J-userName")).sendKeys("");
        // TODO 12306.cn网站的密码
        driver.findElement(By.id("J-password")).sendKeys("");

        Thread.sleep(5000);

        // TODO 选择好验证码后5S自动提交 ，千万别点击登陆按钮
        driver.findElement(By.id("J-login")).click();

        WebDriverWait loginWait = new WebDriverWait(driver, 10);

        loginWait.until(ExpectedConditions.elementToBeClickable(By.name("g_href")));

        driver.get("https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc");

        WebDriverWait searchWait = new WebDriverWait(driver, 10);

        searchWait.until(ExpectedConditions.elementToBeClickable(By.id("query_ticket")));


        cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getName());
        }
        System.out.println("------------------------");


        // 构建cookie
        Map<String, String> cookieMap = getCookie(cookies);

        // 开始购票
        TicketOperation ticket = new TicketOperationImpl();
        ticket.initSubmitTicket(data , getHeader() ,cookieMap);
    }

    /**
     * 组装cookie
     *
     * @param allCookies
     * @return
     */
    private Map<String, String> getCookie(Set<Cookie> allCookies) {
        Map<String, String> cookieMap = new HashMap<>();
        if (allCookies != null) {
            StringBuilder result = new StringBuilder();
            for (Cookie cookie : allCookies) {
                cookieMap.put(cookie.getName(), cookie.getValue());
            }
            cookieMap.put("current_captcha_type", "Z");
            cookieMap.put("_jc_save_fromStation", StringUtil.urlEncode(this.data.getFormName()) + "," + this.data.getStartStation());
            cookieMap.put("_jc_save_toStation", StringUtil.urlEncode(this.data.getToName()) + "," + this.data.getEndStation());
            cookieMap.put("_jc_save_fromDate", this.data.getEndTime());
            cookieMap.put("_jc_save_toDate", this.data.getStartTime());
            cookieMap.put("_jc_save_wfdc_flag", "dc");

            for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
                result.append(entry.getKey() + "=" + entry.getValue() + ";");
            }
            this.cookie = result.toString();
            System.out.println("--------cookie----------");
            System.out.println(result.toString());
            System.out.println("-------------------------");
        }
        return cookieMap;
    }

    /**
     * 构造header
     *
     * @return
     */
    private Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        header.put("Cookie", this.cookie);
        return header;
    }

    private void quit() {
        driver.quit();
    }

    public void delCookie() {
        driver.manage().deleteAllCookies();
    }
}
