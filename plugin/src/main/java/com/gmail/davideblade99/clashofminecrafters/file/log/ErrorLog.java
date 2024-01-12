/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.file.log;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.file.AsyncFileWriter;
import com.gmail.davideblade99.clashofminecrafters.util.ExceptionUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Class representing an error to be recorded on a log file. File writing is performed asynchronously via the
 * {@link #writeLog()} method.
 *
 * @see DumpableData
 * @see #writeLog()
 * @since 3.1
 */
public final class ErrorLog {

    private final Plugin plugin;
    private final Exception exception;
    private final String target;
    private final String action;
    private final String dataDump;

    /**
     * Create a new exception log
     *
     * @param plugin    Plugin instance
     * @param exception Exception from which to retrieve the stack trace and error details to be logged
     * @param action    Action that you were trying to execute and that caused the exception (e.g. load user from database)
     */
    public ErrorLog(@Nonnull final Plugin plugin, @Nonnull final Exception exception, @Nonnull final String action) {
        this(plugin, exception, null, action, (String) null);
    }

    /**
     * Create a new exception log
     *
     * @param plugin    Plugin instance
     * @param exception Exception from which to retrieve the stack trace and error details to be logged
     * @param target    Target of the action or {@code null} if there is none
     * @param action    Action that you were trying to execute and that caused the exception (e.g. load user from database)
     */
    public ErrorLog(@Nonnull final Plugin plugin, @Nonnull final Exception exception, @Nullable final String target, @Nonnull final String action) {
        this(plugin, exception, target, action, (String) null);
    }

    /**
     * Create a new exception log
     *
     * @param plugin       Plugin instance
     * @param exception    Exception from which to retrieve the stack trace and error details to be logged
     * @param target       Target of the action or {@code null} if there is none
     * @param action       Action that you were trying to execute and that caused the exception (e.g. load user from
     *                     database)
     * @param dumpableData Class from which to extract a dump of the data to be recorded in the log file using
     *                     {@link DumpableData#getDataDump()} or {@code null} if there is no data to store
     *
     * @see DumpableData#getDataDump()
     */
    public ErrorLog(@Nonnull final Plugin plugin, @Nonnull final Exception exception, @Nullable final String target, @Nonnull final String action, @Nullable final DumpableData dumpableData) {
        this(plugin, exception, target, action, dumpableData == null ? null : dumpableData.getDataDump());
    }

    /**
     * Create a new exception log
     *
     * @param plugin    Plugin instance
     * @param exception Exception from which to retrieve the stack trace and error details to be logged
     * @param target    Target of the action or {@code null} if there is none
     * @param action    Action that you were trying to execute and that caused the exception (e.g. load user from database)
     * @param dataDump  Dump of the data to be recorded in the log file or {@code null} if there is no data to store
     */
    public ErrorLog(@Nonnull final Plugin plugin, @Nonnull final Exception exception, @Nullable final String target, @Nonnull final String action, @Nullable final String dataDump) {
        this.plugin = plugin;
        this.exception = exception;
        this.target = target;
        this.action = action;
        this.dataDump = dataDump;
    }

    /**
     * Asynchronously writes all error information to a file named {@code sqlerror.<timestamp>.txt}, where {@code
     * <timestamp>} is replaced with the value returned by {@link System#currentTimeMillis()} method.
     */
    public void writeLog() {
        final Path filePath = Paths.get(plugin.getDataFolder().getPath(), "sqlerror." + System.currentTimeMillis() + ".txt");

        final AsyncFileWriter fileWriter = new AsyncFileWriter(plugin, filePath);
        try {
            // Date and hour
            fileWriter.addLine(CoM.DATE_FORMAT.format(LocalDateTime.now()));

            // Diagnostic information message (e.g. containing the UUID of the player)
            if (target != null) {
                fileWriter.addLine("Target of the query - " + target);
                fileWriter.addEmptyLine();
            }
            fileWriter.addLine("Action: " + action);
            fileWriter.addEmptyLine();
            if (dataDump != null) {
                fileWriter.addLine("Bump: [" + dataDump + "]");
                fileWriter.addEmptyLine();
            }

            // Exception details
            fileWriter.addLine("Error details: " + exception.getMessage());
            fileWriter.addEmptyLine();

            // Exception stack trace
            fileWriter.addLine("Stack trace:");
            fileWriter.addLine(ExceptionUtil.getStackTraceAsString(exception));

            fileWriter.write((Exception result) -> {
                if (result == null) // Operation successfully completed
                    MessageUtil.sendWarning("Created the file \"" + filePath + "\" containing the SQL error information.");
                else // Operation failed
                {
                    result.printStackTrace();

                    MessageUtil.sendError("Failed to create SQL error log file: " + result.getMessage());
                }
            });
        } catch (final InterruptedException e) {
            e.printStackTrace();

            MessageUtil.sendError("Failed to create SQL error log file: " + e.getMessage());
        }
    }
}