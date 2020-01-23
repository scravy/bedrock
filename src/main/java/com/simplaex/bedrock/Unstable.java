package com.simplaex.bedrock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks an API that is not considered stable (yet).
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Unstable {
}
