package com.roostify;

/**
 * Enum representing different types of constraints that can be applied to employee scheduling.
 */
public enum ConstraintType {
    EARLYMANDATORY, // Employee must work early shift
    LATEMANDATORY, // Employee must work late shift
    NO, // Employee cannot work this shift
    EARLY, // Employee prefers early shift
    LATE // Employee prefers late shift
}
