package com.jianke.entity;


import com.jianke.demo.constant.SeasonEnum;

public class Test{

    public static void main(String[] args){
        SeasonEnum e = getEnum();
        System.out.println(e);
    }

    private static SeasonEnum getEnum() {
        return SeasonEnum.SPRING;
    }
}
