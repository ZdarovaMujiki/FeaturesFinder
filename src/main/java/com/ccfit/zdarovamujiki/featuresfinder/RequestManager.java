package com.ccfit.zdarovamujiki.featuresfinder;

import com.ccfit.zdarovamujiki.featuresfinder.deserialized.FeatureInfo;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.FeatureList;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.GeoPointsList;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (InputStream stream = ClassLoader.getSystemResourceAsStream("apikeys.properties")) {
            properties.load(stream);
        }
        catch (IOException exception) {
            log.log(Level.WARNING, "Error loading properties");
        }
    }

    private static GeoPointsList readGeoPointsResponse(String response) {
        GeoPointsList geoPointsList = null;
        try {
            geoPointsList = mapper.readValue(response, GeoPointsList.class);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING,"Error reading geopoints response");
        }
        return geoPointsList;
    }

    public static CompletableFuture<String> getResponseBody(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String body = response.body();
                    if (response.statusCode() != 200) {
                        log.log(Level.WARNING, "Bad response: " + body);
                    }
                    return body;
                });
    }

    public static CompletableFuture<GeoPointsList> getGeoPoints(String placeName) {
        placeName = placeName.replace(" ", "%20");

        String key = properties.getProperty("GH_KEY");
        String uri = String.format("%s?limit=%d&q=%s&key=%s",
               Constants.GH_GEOCODES_URL, Constants.GH_GEOCODES_LIMIT, placeName, key);

        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
        return getResponseBody(request).thenApply(RequestManager::readGeoPointsResponse);
    }

    private static Weather readWeatherResponse(String response) {
        Weather weather = null;
        try {
            weather = mapper.readValue(response, Weather.class);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "Error reading weather response");
        }
        return weather;
    }

    public static CompletableFuture<Weather> getWeather(double lng, double lat) {
        String key = properties.getProperty("OW_KEY");
        String uri = String.format(Locale.US, "%s?units=%s&lon=%f&lat=%f&appid=%s",
                Constants.OW_WEATHER_URL, Constants.MEASURE_UNITS, lng, lat, key);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
        return getResponseBody(request).thenApply(RequestManager::readWeatherResponse);
    }

    private static FeatureInfo readFeatureInfoResponse(String response) {
        FeatureInfo featureInfo = null;
        try {
            featureInfo = mapper.readValue(response, FeatureInfo.class);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "Error reading feature info response");
        }
        return featureInfo;
    }

    private static ObservableList<FeatureInfo> readFeaturesResponse(String response) {
        FeatureList featureList = null;
        try {
            featureList = mapper.readValue(response, FeatureList.class);
        } catch (JsonProcessingException e) {
            log.log(Level.WARNING, "Error reading weather response");
        }

        String key = properties.getProperty("OTM_KEY");
        ObservableList<FeatureInfo> featureInfoList = FXCollections.observableArrayList();
        for (FeatureList.Feature feature: featureList.getFeatures()) {
            if (feature.getProperties().getName().equals("")) {
                continue;
            }
            String xid = feature.getProperties().getXid();
            String uri = String.format("%s%s?apikey=%s",
                    Constants.OTM_FEATURES_INFO_URL, xid, key);
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
            getResponseBody(request)
                    .thenApply(RequestManager::readFeatureInfoResponse)
                    .thenAccept(featureInfoList::add);
        }
        return featureInfoList;
    }

    public static CompletableFuture<ObservableList<FeatureInfo>> getFeatures(double lng, double lat) {
        String key = properties.getProperty("OTM_KEY");
        String uri = String.format(Locale.US, "%s?radius=%f&lon=%f&lat=%f&apikey=%s",
                Constants.OTM_FEATURES_LIST_URL, Constants.OTM_FEATURES_LIST_RADIUS, lng, lat, key);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri)).build();
        return getResponseBody(request).thenApply(RequestManager::readFeaturesResponse);
    }
}