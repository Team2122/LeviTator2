package org.teamtators.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.common.TatorRobotBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public class Configurables {
    private static final Logger logger = LoggerFactory.getLogger(Configurables.class);

    public static boolean configureObject(Object toConfigure, JsonNode config) throws ConfigException {
        Preconditions.checkNotNull(toConfigure, "Cannot config null object");
        Preconditions.checkNotNull(config, "config for object must not be null");

        Class<?> clazz = toConfigure.getClass();
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type i : interfaces) {
            if (!(i instanceof ParameterizedType))
                continue;
            ParameterizedType type = (ParameterizedType) i;
            if (type.getRawType() == Configurable.class) {
                Class configClass = (Class) type.getActualTypeArguments()[0];
                try {
                    Object configObj = TatorRobotBase.configMapper.treeToValue(config, configClass);
                    Method configure = clazz.getMethod("configure", configClass);
                    configure.invoke(toConfigure, configObj);
                    return true;
                } catch (NoSuchMethodException e) {
                    logger.warn("Object of class " + clazz.getName() + " missing configure method");
                    return false;
                } catch (JsonProcessingException e) {
                    throw new ConfigException("Error reading config of " + configClass.toString() +
                            " for object of " + clazz.toString(), e);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new ConfigException("Error applying config on object of " + clazz.toString(), e);
                }
            }
        }
//        logger.trace("Attempted to configure object of " + clazz.toString() + ", which is not Configurable");
        return false;
    }
}
