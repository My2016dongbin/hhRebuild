package com.haohai.platform.fireforestplatform.old.bean;

/**
 * Created by qc
 * on 2023/1/5.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Person {
    private String id;
    private String fullName;

    public Person() {
    }

    public Person(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }
}
