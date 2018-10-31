package org.teamtators.common.config.helpers;

import org.teamtators.common.harness.HarnessContext;

public interface ConfigHelper<T> {
    T create(HarnessContext ctx);
}
