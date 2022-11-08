/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.file;

import com.gmail.davideblade99.clashofminecrafters.util.thread.Async;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;

/**
 * Class responsible for asynchronous writing to the file identified by the path passed to the constructor. The
 * class is completely thread-safe: to ensure this, all methods are blocking until the lock is acquired. Once
 * acquired, all other operations are non-blocking and are performed on a separate thread.
 *
 * An instance of {@link AsyncFileWriter} holds in memory the lines to be written to the file. When you want to
 * write the lines memorized so far you must invoke {@link #write(NullableCallback, StandardOpenOption...)} method.
 * The writing channel to the file is opened when the {@link #write(NullableCallback, StandardOpenOption...)}
 * method is called and closed when it is completed.
 *
 * @see AsynchronousFileChannel
 */
public final class AsyncFileWriter {

    /** Plugin instance */
    private final Plugin plugin;

    /** File path */
    private final Path filePath;

    /** Buffer containing the lines to be written to the file */
    private final Collection<String> lines;

    /** Used to ensure that the class is thread-safe */
    private final Semaphore lock;

    /**
     * Create a new object with an empty buffer. No writing will be done until the {@link #write(NullableCallback,
     * StandardOpenOption...)} method is called.
     *
     * @param plugin   Plugin instance
     * @param parent   Parent folder path where the file to be written is located
     * @param fileName Name of file to write to
     */
    public AsyncFileWriter(@Nonnull final Plugin plugin, @Nonnull final String parent, @Nonnull final String fileName) {
        this(plugin, Paths.get(parent, fileName));
    }

    /**
     * Create a new object with an empty buffer. No writing will be done until the {@link #write(NullableCallback,
     * StandardOpenOption...)} method is called.
     *
     * @param plugin Plugin instance
     * @param path   File path to write to
     */
    public AsyncFileWriter(@Nonnull final Plugin plugin, @Nonnull final Path path) {
        this(plugin, path, new ArrayList<>());
    }

    /**
     * Creates a new object putting the lines passed into the buffer. No writing will be done until the {@link
     * #write(NullableCallback, StandardOpenOption...)} method is called.
     *
     * @param plugin Plugin instance
     * @param path   File path to write to
     * @param lines  Lines to write on the file
     */
    public AsyncFileWriter(@Nonnull final Plugin plugin, @Nonnull final Path path, @Nonnull final Collection<String> lines) {
        this.plugin = plugin;
        this.filePath = path;
        this.lines = lines;
        this.lock = new Semaphore(1, true);
    }

    /**
     * Add a line to the buffer
     *
     * @param text Line to write to the file
     *
     * @throws InterruptedException if the thread is interrupted during lock acquisition
     */
    public void addLine(@Nonnull final String text) throws InterruptedException {
        try {
            lock.acquire();
            lines.add(text);
        } finally {
            lock.release();
        }
    }

    /**
     * Add an empty line to the buffer
     *
     * @throws InterruptedException if the thread is interrupted during lock acquisition
     */
    public void addEmptyLine() throws InterruptedException {
        addLine("");
    }

    /**
     * Write asynchronously lines into the buffer at the beginning of the file. If the file does not exist it is
     * created, otherwise it is overwritten.
     *
     * @param completionHandler Callback that will be invoked when the operations are completed. It will have
     *                          {@code null} as parameter if the operation was completed successfully, otherwise it
     *                          will receive the thrown exception.The exceptions that can be thrown are those
     *                          specified in the {@link #write(NullableCallback, StandardOpenOption...)} method.
     *
     * @throws SecurityException If the security manager denies permission to access the file
     * @see Async
     */
    @Async
    public void write(@Nonnull final NullableCallback<Exception> completionHandler) {
        write(completionHandler, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }


    /**
     * Asynchronously write to the file the lines in the buffer according to the specified passed options. If
     * exceptions are thrown, they are passed to the callback received as parameter.
     *
     * @param options           Option with which to open the channel
     * @param completionHandler Callback that will be invoked when the operations are completed. It will have
     *                          {@code null} as parameter if the operation was completed successfully, otherwise it
     *                          will receive the thrown exception. {@link InterruptedException} will be thrown if
     *                          the thread is interrupted during lock acquisition and {@link Exception} for all
     *                          other errors.
     *
     * @throws SecurityException            If the security manager denies permission to access the file
     * @throws OverlappingFileLockException If the lock has already been acquired by another thread (of the same
     *                                      JVM) or if an attempt to lock a region is already in progress
     * @see Async
     */
    @Async
    private void write(@Nonnull final NullableCallback<Exception> completionHandler, @Nonnull final StandardOpenOption... options) {
        try {
            lock.acquire();
        } catch (final InterruptedException e) {
            completionHandler.call(e);
            return;
        } finally {
            lock.release();
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    // Convert lines to a single string
                    final StringBuilder stringBuilder = new StringBuilder();
                    for (String line : lines)
                        stringBuilder.append(line).append(System.lineSeparator());

                    final AsynchronousFileChannel asyncChannel = AsynchronousFileChannel.open(filePath, options);
                    try {
                        asyncChannel.lock().get(); // Wait for lock acquisition

                        asyncChannel.write(ByteBuffer.wrap(stringBuilder.toString().getBytes()), 0, asyncChannel, new CompletionHandler<Integer, AsynchronousFileChannel>() {
                            @Override
                            public void completed(final Integer result, @Nonnull final AsynchronousFileChannel channel) {
                                try {
                                    channel.close(); // Close the channel only when the write operation is finished
                                    Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(null));
                                } catch (final IOException e) {
                                    Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(e));
                                } finally {
                                    lock.release(); // Release the lock
                                }
                            }

                            @Override
                            public void failed(final Throwable exception, @Nonnull final AsynchronousFileChannel channel) {
                                try {
                                    channel.close(); // Close the channel only when the write operation is finished
                                    Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(null));
                                } catch (final IOException e) {
                                    Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(e));
                                } finally {
                                    lock.release(); // Release the lock
                                }
                            }
                        });
                    } catch (final Exception e) {
                        Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(e));

                        asyncChannel.close(); // In case of error close the channel and release the lock on the file
                        lock.release(); // In case of error release the lock
                    }
                } catch (final Exception e) {
                    Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(e));

                    lock.release(); // In case of error release the lock
                }
            }
        });
    }
}
