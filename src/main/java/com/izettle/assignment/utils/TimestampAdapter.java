package com.izettle.assignment.utils;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class TimestampAdapter extends XmlAdapter<String, Timestamp> {

    @Override
    public String marshal(final Timestamp timestamp) throws Exception {
        return timestamp.toString();
    }

    @Override
    public Timestamp unmarshal(String string) throws Exception {
        return new Timestamp(Long.valueOf(string));
    }

}
