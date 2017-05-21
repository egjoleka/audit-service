package com.izettle.assignment.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.izettle.assignment.exception.IzettleException;
import com.izettle.assignment.utils.BearerRandomGenerator;

@RunWith(JUnit4.class)
public class TokenBearerGeneratorTest {

    @Test
    public void generateTokenSuccess() {
        final BearerRandomGenerator tokenBearerGenerator = new BearerRandomGenerator();
        final String tokenBearer1 = tokenBearerGenerator.generateValue();
        final String tokenBearer2 = tokenBearerGenerator.generateValue();
        assertNotNull(tokenBearer1);
        assertNotNull(tokenBearer2);
        assertNotSame(tokenBearer1, tokenBearer2);
    }

    @Test(expected = IzettleException.class)
    public void generateToken_FAILURE() throws NoSuchAlgorithmException {
        BearerRandomGenerator tokenBearerGenerator = Mockito.spy(new BearerRandomGenerator());
        Mockito.when(tokenBearerGenerator.generateValue(Matchers.anyString())).thenReturn(null);
        tokenBearerGenerator.generateValue();
    }
}
