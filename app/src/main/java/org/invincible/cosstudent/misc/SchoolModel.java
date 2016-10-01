package org.invincible.cosstudent.misc;

import java.io.Serializable;

/**
 * Created by rt12148 on 25/9/16.
 **/
public class SchoolModel implements Serializable {

    private String name;
    private String image;
    private String phone;
    private String principal_phone;
    private String principal;
    private String email;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrincipalPhone() {
        return principal_phone;
    }

    public void setPrincipalPhone(String principal_phone) {
        this.principal_phone = principal_phone;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}
