/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {

    private ExceptionUtil() {
        throw new IllegalAccessError();
    }

    @Nonnull
    public static String getStackTraceAsString(@Nonnull final Throwable throwable) {
        final StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
