package com.ccfit.zdarovamujiki.featuresfinder.controllers;

import com.ccfit.zdarovamujiki.featuresfinder.RequestManager;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.Feature;
import com.ccfit.zdarovamujiki.featuresfinder.deserialized.GeoPoint;
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
    @FXML private ListView<Feature> featureList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        placeLabel.setText("Enter place:");
        placeInputField.setPromptText("Place");
        findButton.setText("Find");
        featureList.setCellFactory(featureList -> new FeatureListCell());
        findButton.setOnAction(event -> RequestManager.getGeoPoints(placeInputField.getText()).thenAccept(list -> {
            ObservableList<Button> buttons = FXCollections.observableArrayList();
            for (GeoPoint geoPoint: list) {
                Button button = new Button(geoPoint.getName());
                button.setOnAction(event1 -> {
                    RequestManager.getFeatures(geoPoint.getLng(), geoPoint.getLat())
                            .thenAccept(features -> Platform.runLater(() -> featureList.setItems(features)));
                    RequestManager.getWeather(geoPoint.getLng(), geoPoint. getLat())
                            .thenAccept(weather -> Platform.runLater(() -> weatherLabel.setText(weather)));
                });
                buttons.add(button);
            }
            Platform.runLater(() -> placesList.setItems(buttons));
        }));
    }
}