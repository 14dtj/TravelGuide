package cn.edu.sjtu.travelguide.util;

public class Step {
    private String aName;

    private int aIcon;

    public Step() {
    }

    public Step(String aName,  int aIcon) {
        this.aName = aName;

        this.aIcon = aIcon;
    }

    public String getaName() {
        return aName;
    }



    public int getaIcon() {
        return aIcon;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }



    public void setaIcon(int aIcon) {
        this.aIcon = aIcon;
    }
}
