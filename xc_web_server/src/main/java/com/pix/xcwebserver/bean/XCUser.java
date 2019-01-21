package com.pix.xcwebserver.bean;

import javax.persistence.*;

@Entity
@Table(name = "t_xc_user")
public class XCUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;
    private String name;
    private String email;
    private String password;
    private Integer level = 0;
    private String token = "";
    private Integer sex = 0;

    public XCUser() {
    }

    public XCUser(Integer uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
        if(null == level) {
            level = 0;
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        if(null == token) {
            this.token = "";
        }
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
        if(null == sex) {
            this.sex = 0;
        }
    }
}
