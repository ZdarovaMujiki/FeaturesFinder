package com.ccfit.zdarovamujiki.featuresfinder.controllers;

import com.ccfit.zdarovamujiki.featuresfinder.RequestManager;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.FeatureInfo;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.GeoPointsList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    @FXML private Label placeLabel;
    @FXML private Label weatherLabel;
    @FXML private TextField placeInputField;
    @FXML private Button findButton;

    @FXML private ListView<Button> placesList;
    @FXML private ListView<FeatureInfo> featureList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        placeLabel.setText("Enter place:");
        placeInputField.setPromptText("Place");
        findButton.setText("Find");
        featureList.setCellFactory(FeatureListCell::new);
        findButton.setOnAction(event -> RequestManager.getGeoPoints(placeInputField.getText()).thenAccept(geoPointsList -> {
            ObservableList<Button> buttons = FXCollections.observableArrayList();
            for (GeoPointsList.Address address: geoPointsList.getHits()) {
                Button button = new Button(address.toString());
                button.setOnAction(event1 -> {
                    GeoPointsList.Address.Point point = address.getPoint();
                    double latitude = point.getLat();
                    double longitude = point.getLng();
                    RequestManager.getFeatures(longitude, latitude)
                            .thenAccept(features -> Platform.runLater(() -> featureList.setItems(features)));
                    RequestManager.getWeather(longitude, latitude)
                            .thenAccept(weather -> Platform.runLater(() ->
                                    weatherLabel.setText(weather.toString())));
                });
                buttons.add(button);
            }
            Platform.runLater(() -> placesList.setItems(buttons));
        }));
    }
}