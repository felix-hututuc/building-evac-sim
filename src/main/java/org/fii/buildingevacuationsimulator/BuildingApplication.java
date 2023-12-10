package org.fii.buildingevacuationsimulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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

        Button stairButton = new Button("Stair");
        stairButton.setOnAction(buildingController.stairButtonHandle());
        toolBar.getItems().add(stairButton);

        Button sourceButton = new Button("Select Source");
        sourceButton.setOnAction(buildingController.sourceButtonHandle());
        toolBar.getItems().add(sourceButton);

        Button maxFlowButton = new Button("Max Flow");
        maxFlowButton.setOnAction(buildingController.maxFlowHandle());
        toolBar.getItems().add(maxFlowButton);

        Button showGraphButton = new Button("Show Graph");
        showGraphButton.setOnAction(buildingController.showGraphHandle());
        toolBar.getItems().add(showGraphButton);

        MenuItem newFloorButton = new MenuItem("New Floor");
        newFloorButton.setOnAction(buildingController.newFloorHandle(root));

        MenuButton menuButton = new MenuButton("Change Floor");
        menuButton.setOnMouseClicked(buildingController.changeFloorHandle(root));
        menuButton.getItems().add(newFloorButton);
        toolBar.getItems().add(menuButton);

        Button exportButton = new Button("Export");
        exportButton.setOnAction(buildingController.exportHandle());
        toolBar.getItems().add(exportButton);

        Button importButton = new Button("Import");
        importButton.setOnAction(buildingController.importHandle());
        toolBar.getItems().add(importButton);

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
