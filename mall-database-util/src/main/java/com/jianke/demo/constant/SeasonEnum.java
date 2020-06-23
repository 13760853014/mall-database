package com.jianke.demo.constant;

public enum SeasonEnum {

    SPRING(1, "春天"),
    SUMMER(2,"夏天"),
    AUTUMN(3, "秋天");

    private Integer num;
    private String desc;

    SeasonEnum(Integer num, String desc) {
        this.num = num;
        this.desc = desc;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
