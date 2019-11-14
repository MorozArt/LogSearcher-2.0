package com.moroz.logsearcher.controller;

import com.moroz.logsearcher.model.Model;

import java.nio.file.Path;

public interface Controller {

    void find(Path root, String searchText, String filesType);

    void openFile(Path file);

    String getOpenFilePart(Path file, int part);

    void setModel(Model model);
}
