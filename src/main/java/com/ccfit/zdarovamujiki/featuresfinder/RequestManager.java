package com.ccfit.zdarovamujiki.featuresfinder;

import com.ccfit.zdarovamujiki.featuresfinder.deserialized.Feature;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.GeoPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Log
public class RequestManager {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Properties properties = new Properties();
    static {
        try (InputStream stream = ClassLoader.getSystemResourceAsStream("apikeys.properties")) {
            properties.load(stream);
        }
        catch (IOException exception) {
            log.log(Level.WARNING, "Error loading properties");
        }
    }

    static private ArrayList<GeoPoint> readGeoPointsResponse(String response) {
        GeoPoint[] geoPoints = new GeoPoint[0];
        try {
            JsonNode jsonNode = mapper.readValue(response, JsonNode.class);
            JsonNode jsonArray = jsonNode.get("hits");
            geoPoints = mapper.readValue(jsonArray.toString(), GeoPoint[].class);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING,"Error reading geopoints response");
        }
        return new ArrayList<>(Arrays.asList(geoPoints));
    }

    static public CompletableFuture<ArrayList<GeoPoint>> getGeoPoints(String placeName) {
        placeName = placeName.replace(" ", "");
        String key = properties.getProperty("GH_KEY");
        String uri = String.format("%s?limit=%d&q=%s&key=%s",
               Constants.GH_GEOCODES_URL, Constants.GH_GEOCODES_LIMIT, placeName, key);

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(RequestManager::readGeoPointsResponse);
    }

    static private String readWeatherResponse(String response) {
        String weatherDescription = "";
        String temperature = "";
        try {
            JsonNode jsonNode = mapper.readValue(response, JsonNode.class);
            weatherDescription = jsonNode.get("weather").get(0).get("description").asText();
            temperature = jsonNode.get("main").get("temp").asText();
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "Error reading weather response");
        }
        return String.format("Weather: %s, %s C", weatherDescription, temperature);
    }
    static public CompletableFuture<String> getWeather(double lng, double lat) {
        String key = properties.getProperty("OW_KEY");
        String uri = String.format(Locale.US, "%s?units=%s&lon=%f&lat=%f&appid=%s",
                Constants.OW_WEATHER_URL, Constants.MEASURE_UNITS, lng, lat, key);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(RequestManager::readWeatherResponse);
    }
    static private Feature readFeatureInfoResponse(String response) {
        Feature feature = null;
        try {
            feature = mapper.readValue(response, Feature.class);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "Error reading feature info response");
        }
        return feature;
    }
    static private ObservableList<Feature> readFeaturesResponse(String response) {
        JsonNode[] nodes = new JsonNode[0];
        try {
            JsonNode jsonNode = mapper.readValue(response, JsonNode.class);
            JsonNode jsonArray = jsonNode.get("features");
            nodes = mapper.readValue(jsonArray.toString(), JsonNode[].class);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "Error reading weather response");
        }

        String key = properties.getProperty("OTM_KEY");
        ObservableList<Feature> list = FXCollections.observableArrayList();
        for (JsonNode node: nodes) {
            if (node.get("properties").get("name").asText().equals("")) {
                continue;
            }
            String xid = node.get("properties").get("xid").asText();
            String uri = String.format("%s%s?apikey=%s",
                    Constants.OTM_FEATURES_INFO_URL, xid, key);
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(RequestManager::readFeatureInfoResponse)
                    .thenAccept(list::add);
        }
        return list;
    }

    static public CompletableFuture<ObservableList<Feature>> getFeatures(double lng, double lat) {
        String key = properties.getProperty("OTM_KEY");
        String uri = String.format(Locale.US, "%s?radius=%f&lon=%f&lat=%f&apikey=%s",
                Constants.OTM_FEATURES_LIST_URL, Constants.OTM_FEATURES_LIST_RADIUS, lng, lat, key);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(RequestManager::readFeaturesResponse);
    }
}