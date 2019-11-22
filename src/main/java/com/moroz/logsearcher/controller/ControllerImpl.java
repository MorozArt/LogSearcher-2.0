package com.moroz.logsearcher.controller;

import com.moroz.logsearcher.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class ControllerImpl implements Controller {

    private static final Logger log = LogManager.getLogger(ControllerImpl.class.getName());
    private Model model;

    @Override
    public void find(Path root, String searchText, String filesType) {
        log.trace("find with root: "+root+" searchText: "+searchText+" filesType: "+filesType);
        Thread thread = new Thread(() -> model.find(root, searchText, filesType));
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void openFile(Path file) {
        log.trace("call model method \"openFile\" with file: "+file);
        model.openFile(file);
    }

    @Override
    public String getOpenFilePart(Path file, int part) {
        log.trace("call model method \"getOpenFilePart\" with file: "+file+" part: "+part);
        return model.getFilePart(file, part);
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }
}
