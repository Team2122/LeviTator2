package org.teamtators.common.harness.sources;

import org.teamtators.common.harness.HarnessAnalogDataSource;

import java.util.HashMap;
import java.util.Map;

public class AnalogSourceBank {
    private static Map<String, Class<? extends HarnessAnalogDataSource>> clazzMap;

    public static Map<String, Class<? extends HarnessAnalogDataSource>> getClazzMap() {
        if(clazzMap == null) {
            clazzMap = new HashMap<>();
            clazzMap.put("Constant", ConstantAnalogDataSource.class);
            clazzMap.put("Random", RandomAnalogDataSource.class);
        }
        return clazzMap;
    }
}
