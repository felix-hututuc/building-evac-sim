module org.fii.buildingevacuationsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.jgrapht.core;
    requires jakarta.json;
    requires org.eclipse.parsson;

    opens org.fii.buildingevacuationsimulator to javafx.fxml;
    exports org.fii.buildingevacuationsimulator;
}