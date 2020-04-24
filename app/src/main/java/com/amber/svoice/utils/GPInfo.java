package com.amber.svoice.utils;

public class GPInfo {

    private String code;

    private String name;

    private String price;

    public GPInfo(String code) {
        this.code = code;
    }

    public GPInfo(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public GPInfo(String code, String name, String price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "GPInfo{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
