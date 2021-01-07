package com.alanchen.testproject.WebService;

public class WebServiceCallBackObject {
    public String action;
    public String data;
    public int errorCode;

    public WebServiceCallBackObject(String action, int errorCode, String data) {
        this.action = action;
        this.data = data;
        this.errorCode = errorCode;
    }
}
