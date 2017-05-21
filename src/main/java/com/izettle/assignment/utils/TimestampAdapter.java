package com.izettle.assignment.utils;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is needed to return the timestamp from the rest as the Timestamp does not have a non argument constructor,
 * therefore it does not fit to the JAX-RS specs.
 *
 * @author egjoleka
 *
 */
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
