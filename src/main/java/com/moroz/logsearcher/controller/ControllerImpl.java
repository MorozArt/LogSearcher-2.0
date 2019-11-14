package com.moroz.logsearcher.controller;

import com.moroz.logsearcher.model.Model;

import java.nio.file.Path;

public class ControllerImpl implements Controller {

    private Model model;

    @Override
    public void find(Path root, String searchText, String filesType) {
        Thread thread = new Thread(() -> model.find(root, searchText, filesType));
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void openFile(Path file) {
        model.openFile(file);
    }

    @Override
    public String getOpenFilePart(Path file, int part) {
        return model.getFilePart(file, part);
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }
}
