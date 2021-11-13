package com.ccfit.zdarovamujiki.featuresfinder.controllers;

import com.ccfit.zdarovamujiki.featuresfinder.deserialized.Feature;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FeatureListCell extends ListCell<Feature> {
    @FXML
    public VBox vBox;
    @FXML
    public Hyperlink name;

    @Override
    protected void updateItem(Feature feature, boolean empty) {
        super.updateItem(feature, empty);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FeatureListCell.fxml"));
        loader.setController(this);
        try {
            vBox = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (feature != null) {
            name.setText(feature.getName());
            name.setOnAction(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(feature.getOtm()));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            });
        }
        Platform.runLater(() -> setGraphic(vBox));
    }
}
