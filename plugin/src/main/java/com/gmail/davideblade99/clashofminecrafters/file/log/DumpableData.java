/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.file.log;

/**
 * A class that implements this interface must provide the {@link #getDataDump()} method, which returns a string
 * enclosing all the information the class wants to store in a dump. It is used with {@link ErrorLog} to log data
 * when an unexpected situation occurs (e.g., an exception during a database fetch). In this way, it is possible to
 * restore the data when needed by having it recorded on the log file.
 *
 * @see ErrorLog
 */
//TODO: inutilizzata
public interface DumpableData {

    /**
     * @return A string that contains all the information you want to store on the dump
     */
    String getDataDump();
}