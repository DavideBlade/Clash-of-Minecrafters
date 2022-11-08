/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.configuration;

import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

abstract class CoMYamlConfiguration extends YamlConfiguration {

    private final static byte DEFAULT_NUMBER = 0;


    protected final File file;

    /**
     * True if, each time a value is changed in the configuration, the file must also be changed.
     *
     * In other words, every time a setter is called, the value on the file is also changed. False if you do not
     * want this to happen (the value will be modified only in memory and will not persist until the {@link
     * #save()} method is called).
     *
     * @see #set(String, Object)
     * @see #save()
     * @see #setAndSave(String, Object)
     */
    final boolean autoSave;

    /**
     * Creates a new configuration that does not automatically save changes in the file
     *
     * @param file File on which to read/write the configuration
     */
    protected CoMYamlConfiguration(@Nonnull final File file) {
        this(file, false);
    }

    protected CoMYamlConfiguration(@Nonnull final File file, final boolean autoSave) {
        super();

        this.file = file;
        this.autoSave = autoSave;

        try {
            this.load();
        } catch (final Exception e) {
            //TODO: togliere try-catch e far propagare l'errore all'esterno -> l'utente deve essere informato in caso ci siano errori con i file (così come con il MySQL) + va creato un file di errore (come con MySQL)
            e.printStackTrace();

            ChatUtil.sendMessage("&cIt wasn't possible to load " + file.getName() + " file.");
        }
    }

    @Override
    public final int getInt(@Nonnull final String path) {
        final Object defaultValue = super.getDefault(path);

        return this.getInt(path, defaultValue instanceof Integer ? (Integer) defaultValue : DEFAULT_NUMBER);
    }

    @Override
    public final int getInt(@Nonnull final String path, final int defaultValue) {
        final Object value = super.get(path, defaultValue);

        return value instanceof Integer ? (Integer) value : defaultValue;
    }

    @Override
    public final long getLong(@Nonnull final String path) {
        final Object defaultValue = super.getDefault(path);

        /*
         * If the number can be represented by an integer then the Object will be an Integer.
         * If the number cannot be represented by an integer but by a long, then it will be a Long.
         */
        if (defaultValue instanceof Integer)
            return this.getLong(path, (Integer) defaultValue);
        else if (defaultValue instanceof Long)
            return this.getLong(path, (Long) defaultValue);
        else // If the default value is not representable neither with a long (e.g. it has decimals, is too large a number or is not a number)
            return this.getLong(path, DEFAULT_NUMBER);
    }

    @Override
    public final long getLong(@Nonnull final String path, final long defaultValue) {
        final Object value = super.get(path, defaultValue);

        /*
         * If the number can be represented by an integer then the Object will be an Integer.
         * If the number cannot be represented by an integer but by a long, then it will be a Long.
         */
        if (value instanceof Integer)
            return (Integer) value;
        else if (value instanceof Long)
            return (Long) value;
        else // If the default value is not representable neither with a long (e.g. it has decimals, is too large a number or is not a number)
            return defaultValue;
    }

    @Override
    public final double getDouble(@Nonnull final String path) {
        final Object defaultValue = super.getDefault(path);

        /*
         * If the number does not have a decimal part, even if it can be represented with a double, is not considered a Double.
         * Examples:
         * 2147483647 -> Integer
         * 9223372036854775807 -> Long
         * 9223372036854775808 -> BigInt
         * 9223372036854775808.0 -> Double
         * 1.79769313486231570e+309 = Infinity -> Double
         * Ciao -> String
         */
        if (defaultValue instanceof Integer)
            return this.getDouble(path, (Integer) defaultValue);
        else if (defaultValue instanceof Double)
            return this.getDouble(path, (Double) defaultValue);
        else
            return this.getDouble(path, DEFAULT_NUMBER);
    }

    @Override
    public final double getDouble(@Nonnull final String path, final double defaultValue) {
        final Object value = super.get(path, defaultValue);

        /*
         * If the number does not have a decimal part, even if it can be represented with a double, is not considered a Double.
         * Examples:
         * 2147483647 -> Integer
         * 9223372036854775807 -> Long
         * 9223372036854775808 -> BigInt
         * 9223372036854775808.0 -> Double
         * 1.79769313486231570e+309 = Infinity -> Double
         * Ciao -> String
         */
        if (value instanceof Integer)
            return (Integer) value;
        else if (value instanceof Double)
            return (Double) value;
        else
            return defaultValue;
    }

    public final byte getByte(@Nonnull final String path) {
        final Object defaultValue = super.getDefault(path);

        /*
         * Even if the number is representable with a byte, the Object will be an Integer.
         * Examples:
         * 3 -> Integer
         * 2147483647 -> Integer
         * 9223372036854775807 -> Long
         * 9223372036854775808 -> BigInt
         * 9223372036854775808.0 -> Double
         * 1.79769313486231570e+309 = Infinity -> Double
         * Ciao -> String
         */
        if (!(defaultValue instanceof Integer))
            return this.getByte(path, DEFAULT_NUMBER);

        // Check whether the default number is representable with a byte
        final int defaultNumber = (Integer) defaultValue;
        if (defaultNumber <= Byte.MIN_VALUE || defaultNumber >= Byte.MAX_VALUE)
            return this.getByte(path, DEFAULT_NUMBER);

        return this.getByte(path, (byte) defaultNumber);
    }

    public final byte getByte(@Nonnull final String path, final byte defaultValue) {
        final int value = this.getInt(path, defaultValue);

        // Check whether the value is representable with a byte
        if (value <= Byte.MIN_VALUE || value >= Byte.MAX_VALUE)
            return DEFAULT_NUMBER;

        return (byte) value;
    }

    public final short getShort(@Nonnull final String path) {
        final Object defaultValue = super.getDefault(path);

        /*
         * Even if the number is representable with a short, the Object will be an Integer.
         * Examples:
         * 3 -> Integer
         * 30000 -> Integer
         * 2147483647 -> Integer
         * 9223372036854775807 -> Long
         * 9223372036854775808 -> BigInt
         * 9223372036854775808.0 -> Double
         * 1.79769313486231570e+309 = Infinity -> Double
         * Ciao -> String
         */
        if (!(defaultValue instanceof Integer))
            return this.getShort(path, DEFAULT_NUMBER);

        // Check whether the default number is representable with a short
        final int defaultNumber = (Integer) defaultValue;
        if (defaultNumber <= Short.MIN_VALUE || defaultNumber >= Short.MAX_VALUE)
            return this.getShort(path, DEFAULT_NUMBER);

        return this.getShort(path, (short) defaultNumber);
    }

    public final short getShort(@Nonnull final String path, final short defaultValue) {
        final int value = this.getInt(path, defaultValue);

        // Check whether the value is representable with a short
        if (value <= Short.MIN_VALUE || value >= Short.MAX_VALUE)
            return DEFAULT_NUMBER;

        return (short) value;
    }

    public final float getFloat(@Nonnull final String path) {
        final Object defaultValue = super.getDefault(path);

        /*
         * Even if the number is representable with a float, the Object will be a Double.
         * Plus, if the number does not have a decimal part, even if it can be represented
         * with a float, is not considered a Double.
         * Examples:
         * 2147483647 -> Integer
         * 9223372036854775807 -> Long
         * 9223372036854775808 -> BigInt
         * 3.40282346638528860e+38 -> Double
         * 9223372036854775808.0 -> Double
         * 1.79769313486231570e+309 = Infinity -> Double
         * Ciao -> String
         */
        if (!(defaultValue instanceof Double))
            return this.getFloat(path, DEFAULT_NUMBER);

        // Check whether the default number is representable with a float
        final double defaultNumber = (Double) defaultValue;
        if (Math.abs(defaultNumber) <= Float.MIN_VALUE || Math.abs(defaultNumber) >= Float.MAX_VALUE)
            return this.getFloat(path, DEFAULT_NUMBER);

        return this.getFloat(path, (float) defaultNumber);
    }

    public final float getFloat(@Nonnull final String path, final float defaultValue) {
        final double value = this.getDouble(path, defaultValue);

        // Check whether the value is representable with a float
        if (Math.abs(value) <= Float.MIN_VALUE || Math.abs(value) >= Float.MAX_VALUE)
            return DEFAULT_NUMBER;

        return (float) value;
    }

    /**
     * {@inheritDoc}
     *
     * In addition to executing the parent method, it also saves changes persistently (on the file) if {@link
     * #autoSave} is set
     *
     * @see MemorySection#set(String, Object)
     * @since v3.0.1
     */
    @Override
    public void set(@Nonnull final String path, @Nullable final Object value) {
        super.set(path, value);

        if (autoSave)
            this.save();
    }

    /**
     * Update the configuration and save it to file
     *
     * @param path  Path of the object to set
     * @param value New value to set the path to
     *
     * @see #save()
     * @see MemorySection#set(String, Object)
     */
    public final void setAndSave(@Nonnull final String path, @Nullable final Object value) {
        super.set(path, value);

        this.save();
    }

    /**
     * {@inheritDoc}
     *
     * Saves the configuration to the file. Until this method is called, the configuration is only in memory and is
     * not persistent. For example, if the server is shut down before saving to file, configuration changes will be
     * lost.
     *
     * @see FileConfiguration#save(File)
     */
    public final void save() {
        try {
            super.save(file);
        } catch (final Exception e) {
            //TODO: come su MySQL creare un file con le informazioni per la diagnostica
            //TODO: togliere il try-catch, far propagare l'errore all'esterno e informare l'utente del problema?
            e.printStackTrace();
            ChatUtil.sendMessage("&cIt wasn't possible to save " + file.getName() + " file. Some data may be lost.");
        }
    }

    void load() throws Exception {
        if (!file.exists())
            FileUtil.createFile(file);

        super.load(file);
    }
}