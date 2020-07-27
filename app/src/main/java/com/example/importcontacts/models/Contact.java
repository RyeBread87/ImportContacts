package com.example.importcontacts.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.importcontacts.AppDatabase;

import java.io.Serializable;

// model class for contacts with properties, Room annotations, and getters & setters
@Entity(tableName = AppDatabase.TABLE_NAME_CONTACTS)
public class Contact {

    @PrimaryKey(autoGenerate = true)
    public int contact_id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "country_code")
    public String countryCode;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "email")
    public String email;

    public Contact() {
    }

    // getters and setters
    public Integer getId() {
        return contact_id;
    }

    public void setId(int id) {
        this.contact_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setCountryCode(String countryCode) {this.countryCode = countryCode; }

    public String getCountryCode() { return this.countryCode; }

}