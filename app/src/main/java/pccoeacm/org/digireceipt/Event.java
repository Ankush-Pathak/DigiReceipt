package pccoeacm.org.digireceipt;

/**
 * Created by Ankush on 1/14/2017.
 */

public class Event {
    private String name;
    private int maxPerTeam;
    private int regFee;
    private int code;
    private String info;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPerTeam() {
        return maxPerTeam;
    }

    public void setMaxPerTeam(int maxPerTeam) {
        this.maxPerTeam = maxPerTeam;
    }

    public int getRegFee() {
        return regFee;
    }

    public void setRegFee(int regFee) {
        this.regFee = regFee;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Event(String name, int maxPerTeam, int regFee, int code, String info) {
        this.name = name;
        this.maxPerTeam = maxPerTeam;
        this.regFee = regFee;
        this.code = code;
        this.info = info;

    }
    public Event()
    {

    }

}
