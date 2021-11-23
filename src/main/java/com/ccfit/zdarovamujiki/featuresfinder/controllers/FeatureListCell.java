package com.ccfit.zdarovamujiki.featuresfinder.controllers;

import com.ccfit.zdarovamujiki.featuresfinder.deserialized.FeatureInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FeatureListCell extends ListCell<FeatureInfo> {
    @FXML
    public VBox vBox;
    @FXML
    public Hyperlink name;
    @FXML
    public Label description;

    private ListView<FeatureInfo> featureList;
    FeatureListCell(ListView<FeatureInfo> featureList) {
        this.featureList = featureList;
    }

    @Override
    protected void updateItem(FeatureInfo featureInfo, boolean empty) {
        super.updateItem(featureInfo, empty);

        setMinWidth(featureList.getWidth());
        setMaxWidth(featureList.getWidth());
        setPrefWidth(featureList.getWidth());

        setWrapText(true);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FeatureListCell.fxml"));
        loader.setController(this);
        try {
            vBox = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (featureInfo != null) {
            String kinds = featureInfo.getKinds()
                    .replace("_", " ")
                    .replace(",", ", ");
            description.setText(kinds);
            name.setText(featureInfo.getName());
            name.setOnAction(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(featureInfo.getOtm()));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            });
        }
        Platform.runLater(() -> setGraphic(vBox));
    }
}
