/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.thread;

import java.lang.annotation.*;

/**
 * Indicates that the annotated method is executed asynchronously and thus its return does not correspond to the
 * end of the computation it is performing. A Callback is usually provided so that completion can be captured.
 *
 * @see NullableCallback
 * @see NonnullCallback
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface Async { }