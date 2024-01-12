/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.thread;

import javax.annotation.Nullable;

/**
 * Interface that allows a value (possibly {@code null}) to be returned from an asynchronous task.
 * When the asynchronous task is ready it will call the {@link #call(V)} method.
 * Implementors will write the code to execute once the result is ready
 * inside the implementation of the method {@link #call(V)}.
 *
 * @param <V> The result type of the method {@link #call(V)}
 *
 * @since 3.0
 */
public interface NullableCallback<V> {

    /**
     * Computes a result
     *
     * @param result Computed result
     */
    void call(@Nullable final V result);
}
