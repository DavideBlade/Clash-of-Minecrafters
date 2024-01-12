/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util;

import com.gmail.davideblade99.clashofminecrafters.CoM;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtil {

    private FileUtil() {
        throw new IllegalAccessError();
    }

    /**
     * Put {@code input} to specified file.
     *
     * @param inFile  - the name of embedded file to be copied
     * @param outFile - the file where specified embedded file should be copied
     * @since 2.0
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyFile(@Nonnull final String inFile, @Nonnull final File outFile) {
        // Create output folder
        outFile.getParentFile().mkdirs();

        // Copy contents of inFile to outFile
        try {
            final InputStream input = CoM.getInstance().getResource(inFile);
            final OutputStream output = new FileOutputStream(outFile);

            final byte[] buf = new byte[1024];
            int number;
            while ((number = input.read(buf)) > 0)
                output.write(buf, 0, number);

            output.close();
            input.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createFile(@Nonnull final File file) {
        if (file.exists())
            return;

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}