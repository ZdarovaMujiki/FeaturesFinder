package com.ccfit.zdarovamujiki.featuresfinder.deserialized;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = FeatureDeserializer.class)
public class Feature {
    private String name;
    private String otm;
}

class FeatureDeserializer extends JsonDeserializer<Feature> {
    @Override
    public Feature deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode root = parser.readValueAs(JsonNode.class);
        String otm = root.get("otm").asText();
        String name = root.get("name").asText();
        return new Feature(name, otm);
    }
}
