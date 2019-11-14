package com.moroz.logsearcher.view.fxview;

import com.moroz.logsearcher.AppProperties;
import com.moroz.logsearcher.controller.Controller;
import com.moroz.logsearcher.model.FileTree;
import com.moroz.logsearcher.view.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;

public class FXViewImpl extends Application implements View {

    private static Controller mvcController;
    private static FXViewController fxViewController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxview.fxml"));

        primaryStage.setTitle(AppProperties.getProperty("appTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void setFileTree(FileTree fileTree) {
        fxViewController.addFileTree(fileTree);
    }

    @Override
    public void setOpenFile(String fileText, int filePartCount, List<Pair<Integer, Integer> > foundTextIndexes) {
        fxViewController.openFile(fileText, filePartCount, foundTextIndexes);
    }

    @Override
    public void show() {
        launch();
    }

    @Override
    public void setController(Controller controller) {
        this.mvcController = controller;
    }

    protected Controller getMvcController() {
        return mvcController;
    }

    protected static void setFxViewController(FXViewController controller) {
        fxViewController = controller;
    }
}
