package org.fii.buildingevacuationsimulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class BuildingApplication extends Application {
    BuildingController buildingController = new BuildingController();

    @Override
    public void start(Stage primaryStage) {
        buildingController.addRoom(new Room(300, 100, 600, 600));

        Canvas canvas = new Canvas(1200, 800);
        buildingController.setCanvas(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);

        buildingController.draw(gc);

        canvas.setOnMousePressed(buildingController.canvasClickResize());

        canvas.setOnMouseReleased(buildingController.canvasClickRelease());

        canvas.setOnMouseDragged(buildingController.canvasDragResize(gc));

        ToolBar toolBar = new ToolBar();
        Button verticalButton = new Button("Vertical wall");
        verticalButton.setOnAction(buildingController.verticalButtonHandle(gc));
        toolBar.getItems().add(verticalButton);

        Button horizontalButton = new Button("Horizontal wall");
        horizontalButton.setOnAction(buildingController.horizontalButtonHandle(gc));
        toolBar.getItems().add(horizontalButton);

        Button doorButton = new Button("Door");
        doorButton.setOnAction(buildingController.doorButtonHandle(gc));
        toolBar.getItems().add(doorButton);

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setTop(toolBar);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
