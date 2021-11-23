module com.ccfit.zdarovamujiki.featuresfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.datatransfer;
    requires lombok;
    requires java.desktop;
    requires java.net.http;
    requires java.logging;


    opens com.ccfit.zdarovamujiki.featuresfinder to com.fasterxml.jackson.databind, javafx.fxml;
    exports com.ccfit.zdarovamujiki.featuresfinder;
    exports com.ccfit.zdarovamujiki.featuresfinder.deserialized;
    opens com.ccfit.zdarovamujiki.featuresfinder.deserialized to com.fasterxml.jackson.databind, javafx.fxml;
    exports com.ccfit.zdarovamujiki.featuresfinder.controllers;
    opens com.ccfit.zdarovamujiki.featuresfinder.controllers to com.fasterxml.jackson.databind, javafx.fxml;
}