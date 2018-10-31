package org.teamtators.common.harness.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.common.config.ConfigException;
import org.teamtators.common.harness.HarnessAnalogDataSource;
import org.teamtators.common.harness.HarnessContext;
import org.teamtators.common.harness.HarnessHooks;
import org.teamtators.common.harness.sources.AnalogSourceBank;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HarnessConfigurator {
    private static final Logger logger = LoggerFactory.getLogger(HarnessConfigurator.class);
    private static final String CONFIG_FILE = "Harness.yaml";
    private static ObjectMapper mapper;

    public static HarnessContext createContext() throws Exception {
        mapper = new ObjectMapper(new YAMLFactory());
        String filePath = System.getProperty("configDir") + File.separator + CONFIG_FILE;
        ObjectNode node;
        try (InputStream fileStream = new FileInputStream(filePath)) {
            logger.trace("Loading config from path {}", filePath);
            node = (ObjectNode) mapper.reader().readTree(fileStream);
        } catch (IOException e) {
            throw new ConfigException(String.format("Error loading config %s", CONFIG_FILE), e);
        }
        if(node.get("enable").asBoolean()) {
            HarnessContext context = contextFromNode(node);
            HarnessHooks.setContext(context);
            return context;
        }
        return null;
    }

    private static HarnessContext contextFromNode(ObjectNode node) throws Exception {
        HarnessContext context = new HarnessContext();
        context.analogSources = new HashMap<>();

        ObjectNode analog = (ObjectNode) node.get("analogSources");
        Iterator<Map.Entry<String, JsonNode>> it = analog.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            String name = field.getKey();
            //select data source, configure
            String typeName = field.getValue().get("type").asText();
            //figure out just what type corresponds to the name
            Class<? extends HarnessAnalogDataSource> type = analogSourceCalled(typeName);
            //remove type so we can treat the submap as a source :)
            while (field.getValue().fields().hasNext()) {
                Map.Entry<String, JsonNode> subfield = field.getValue().fields().next();
                if (subfield.getKey().equals("type")) {
                    field.getValue().fields().remove();
                    break;
                }
            }
            //k, let's do that
            HarnessAnalogDataSource thing = mapper.treeToValue(field.getValue(), type);
            context.analogSources.put(name, thing);
        }
        return context;
    }

    private static Class<? extends HarnessAnalogDataSource> analogSourceCalled(String typeName) throws Exception {
        return AnalogSourceBank.getClazzMap().get(typeName);
    }
}
