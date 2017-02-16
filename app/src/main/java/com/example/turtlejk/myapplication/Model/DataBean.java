package com.example.turtlejk.myapplication.Model;

import java.util.List;

public class DataBean {
    private String name;
    private String title;
    private String style;
    private String value;
    private String selected;
    private String orgtype;
    private int orgwidth;
    private String orghide;
    private String leipiplugins;
    private String type;
    private String content;
    private String orgmax;
    private String orgmin;
    private int orgheight;
    private String orgfontsize;
    private int size;
    private List<OptionBean> optionBeanList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getValue() {
        return value;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOrgtype() {
        return orgtype;
    }

    public void setOrgtype(String orgtype) {
        this.orgtype = orgtype;
    }

    public int getOrgwidth() {
        return orgwidth;
    }

    public void setOrgwidth(int orgwidth) {
        this.orgwidth = orgwidth;
    }

    public String getOrghide() {
        return orghide;
    }

    public void setOrghide(String orghide) {
        this.orghide = orghide;
    }

    public String getLeipiplugins() {
        return leipiplugins;
    }

    public void setLeipiplugins(String leipiplugins) {
        this.leipiplugins = leipiplugins;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrgmin() {
        return orgmin;
    }

    public void setOrgmin(String orgmin) {
        this.orgmin = orgmin;
    }

    public String getOrgmax() {
        return orgmax;
    }

    public void setOrgmax(String orgmax) {
        this.orgmax = orgmax;
    }

    public int getOrgheight() {
        return orgheight;
    }

    public void setOrgheight(int orgheight) {
        this.orgheight = orgheight;
    }

    public List<OptionBean> getOptionBeanList() {
        return optionBeanList;
    }

    public void setOptionBeanList(List<OptionBean> optionBeanList) {
        this.optionBeanList = optionBeanList;
    }

    public String getOrgfontsize() {
        return orgfontsize;
    }

    public void setOrgfontsize(String orgfontsize) {
        this.orgfontsize = orgfontsize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
