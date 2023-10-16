package org.fii.buildingevacuationsimulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class BuildingApplication extends Application {
    BuildingController buildingController = new BuildingController();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Building Evacuation Simulator");

        buildingController.draw();

        BorderPane root = new BorderPane();

        ToolBar toolBar = new ToolBar();
        Button verticalButton = new Button("Vertical wall");
        verticalButton.setOnAction(buildingController.verticalButtonHandle());
        toolBar.getItems().add(verticalButton);

        Button horizontalButton = new Button("Horizontal wall");
        horizontalButton.setOnAction(buildingController.horizontalButtonHandle());
        toolBar.getItems().add(horizontalButton);

        Button doorButton = new Button("Door");
        doorButton.setOnAction(buildingController.doorButtonHandle());
        toolBar.getItems().add(doorButton);

        Button sourceButton = new Button("Select Source");
        sourceButton.setOnAction(buildingController.sourceButtonHandle());
        toolBar.getItems().add(sourceButton);

        Button maxFlowButton = new Button("Max Flow");
        maxFlowButton.setOnAction(buildingController.maxFlowHandle());
        toolBar.getItems().add(maxFlowButton);

        Button showGraphButton = new Button("Show Graph");
        showGraphButton.setOnAction(buildingController.showGraphHandle());
        toolBar.getItems().add(showGraphButton);

        Button newFloorButton = new Button("New Floor");
        newFloorButton.setOnAction(buildingController.newFloorHandle(root));
        toolBar.getItems().add(newFloorButton);

        Button previousFloorButton = new Button("Previous Floor");
        previousFloorButton.setOnAction(buildingController.previousFloorHandle(root));
        toolBar.getItems().add(previousFloorButton);

        Button nextFloorButton = new Button("Next Floor");
        nextFloorButton.setOnAction(buildingController.nextFloorHandle(root));
        toolBar.getItems().add(nextFloorButton);

        root.setCenter(buildingController.getCanvas());
        root.setTop(toolBar);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
