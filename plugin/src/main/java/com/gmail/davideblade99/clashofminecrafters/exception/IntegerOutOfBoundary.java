/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.exception;

import javax.annotation.Nullable;

/**
 * Exception indicating exceeding of integer boundaries (> Integer.MAX_VALUE && < Integer.MIN_VALUE).
 * If, for example, a too big number (not representable by an int) is given as a string, this exception is thrown.
 */
public final class IntegerOutOfBoundary extends IllegalArgumentException {

    public IntegerOutOfBoundary() {}

    public IntegerOutOfBoundary(@Nullable final String s) {
        super(s);
    }
}