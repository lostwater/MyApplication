package com.example.turtlejk.myapplication.Model;


import java.util.List;

public class ResultBean {
    private int fields;
    private String template;
    private String parse;
    private List<DataBean> add_fields;
    private List<DataBean> dataBeanList;

    public int getFields() {
        return fields;
    }

    public void setFields(int fields) {
        this.fields = fields;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public List<DataBean> getAdd_fields() {
        return add_fields;
    }

    public void setAdd_fields(List<DataBean> add_fields) {
        this.add_fields = add_fields;
    }

    public List<DataBean> getDataBeanList() {
        return dataBeanList;
    }

    public void setDataBeanList(List<DataBean> dataBeanList) {
        this.dataBeanList = dataBeanList;
    }
}
