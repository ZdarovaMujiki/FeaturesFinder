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
@JsonDeserialize(using = GeoPointDeserializer.class)
public class GeoPoint {
    private String name;
    private double lat;
    private double lng;
}

class GeoPointDeserializer extends JsonDeserializer<GeoPoint> {
    @Override
    public GeoPoint deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode root = parser.readValueAs(JsonNode.class);
        JsonNode point = root.get("point");
        String name = root.get("name").asText();
        double lat = point.get("lat").asDouble();
        double lng = point.get("lng").asDouble();
        return new GeoPoint(name, lat, lng);
    }
}