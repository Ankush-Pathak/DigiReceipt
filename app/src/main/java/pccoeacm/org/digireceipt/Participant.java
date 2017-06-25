package pccoeacm.org.digireceipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ankush on 1/14/2017.
 */

public class Participant implements Serializable {
    private String name1,name2,name3;
    private String phoneNo;
    private String email;
    private String insitute;
    private String dept;
    private List<String> events;
    private long timeStamp;
    private String username;
    private int totalPayment;
    private String paymentMode;
    private String year;
    private String txnId;
    private boolean attdnc;
    private boolean emailSent;



    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getName1() {
        return name1;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInsitute() {
        return insitute;
    }

    public void setInsitute(String insitute) {
        this.insitute = insitute;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }



    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public boolean getAttdnc() {
        return attdnc;
    }

    public void setAttdnc(boolean attdnc) {
        this.attdnc = attdnc;
    }

    public Participant(String name1, String name2, String name3, String phoneNo, String email, String insitute, String dept, List<String> events, long timeStamp, String username, int totalPayment, String paymentMode, String year, String txnId, boolean attdnc, boolean emailSent) {
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.phoneNo = phoneNo;
        this.email = email;
        this.insitute = insitute;
        this.dept = dept;
        this.events = events;


        this.timeStamp = timeStamp;
        this.username = username;
        this.totalPayment = totalPayment;
        this.paymentMode = paymentMode;

        this.year = year;
        this.txnId = txnId;
        this.attdnc = attdnc;

        this.emailSent = emailSent;
    }
    public Participant()
    {

    }
}
