package org.teamtators.common.harness;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An interface which specifies that a given higher level component may have its hardware inputs substituted with
 * Harness inputs. Components MUST implement a constructor which accepts only a HarnessContext.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HarnessTestable {
}
