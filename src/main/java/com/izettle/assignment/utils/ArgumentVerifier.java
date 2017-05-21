package com.izettle.assignment.utils;

/**
 * Utility that can validate a number of kind of method arguments (instead of writing code for this in each every
 * class).
 */
public class ArgumentVerifier {


    public static void verifyNotNull(Object argument, String argumentDescription) {
        if (argument == null) {
            throw new IllegalArgumentException(
                    String.format("The argument '%s' must not be null", argumentDescription));
        }
    }

  
    public static void verifyNotNull(Object argument) {
        verifyNotNull(argument, "The argument is null");
    }
}
