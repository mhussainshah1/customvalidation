package com.example.customvalidation.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ContactInfoExpression {

    @Id
    @Column(name = "expression_type")
    private String type;

    private String pattern;

    //standard constructor, getters, setters
    public ContactInfoExpression() {
    }

    public ContactInfoExpression(String type, String pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
