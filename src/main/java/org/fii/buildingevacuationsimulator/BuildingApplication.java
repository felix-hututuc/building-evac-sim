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

        Button removeDoorButton = new Button("Remove Door / Stair");
        removeDoorButton.setOnAction(buildingController.removeDoorHandle());
        toolBar.getItems().add(removeDoorButton);

        MenuItem newFloorButton = new MenuItem("New Floor");
        newFloorButton.setOnAction(buildingController.newFloorHandle(root));

        MenuButton menuButton = new MenuButton("Change Floor");
        menuButton.setOnMouseClicked(buildingController.changeFloorHandle(root));
        menuButton.getItems().add(newFloorButton);
        toolBar.getItems().add(menuButton);

        Button sourceButton = new Button("Select Sources");
        sourceButton.setOnAction(buildingController.sourceButtonHandle());
        toolBar.getItems().add(sourceButton);

        Button clearSourceButton = new Button("Clear Sources");
        clearSourceButton.setOnAction(buildingController.clearSourceButtonHandle());
        toolBar.getItems().add(clearSourceButton);

        MenuItem disjointPathsButton = new MenuItem("Disjoint Paths Problem");
        disjointPathsButton.setOnAction(buildingController.selectDisjointPathsProblem());

        MenuItem minimumTimeProblem = new MenuItem("Min Evac Time Problem");
        minimumTimeProblem.setOnAction(buildingController.selectMinTimeProblem());

        MenuButton selectProblemButton = new MenuButton("Select Problem");
        selectProblemButton.getItems().add(disjointPathsButton);
        selectProblemButton.getItems().add(minimumTimeProblem);
        toolBar.getItems().add(selectProblemButton);

        Button maxFlowButton = new Button("Run Simulation");
        maxFlowButton.setOnAction(buildingController.runSimulationHandle());
        toolBar.getItems().add(maxFlowButton);

        Button showGraphButton = new Button("Show Graph Visualisation");
        showGraphButton.setOnAction(buildingController.showGraphHandle());
        toolBar.getItems().add(showGraphButton);

        Button exportButton = new Button("Export");
        exportButton.setOnAction(buildingController.exportHandle());
        toolBar.getItems().add(exportButton);

        Button importButton = new Button("Import");
        importButton.setOnAction(buildingController.importHandle(root));
        toolBar.getItems().add(importButton);

        MenuItem resetSimButton = new MenuItem("Reset Sim");
        resetSimButton.setOnAction(buildingController.resetSimulationHandle());

        MenuItem resetAllButton = new MenuItem("Full Reset");
        resetAllButton.setOnAction(buildingController.resetAll(root));

        MenuButton resetButton = new MenuButton("Reset");
        resetButton.getItems().add(resetSimButton);
        resetButton.getItems().add(resetAllButton);
        toolBar.getItems().add(resetButton);

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
