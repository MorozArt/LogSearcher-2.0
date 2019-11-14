package com.moroz.logsearcher.model;

import com.moroz.logsearcher.view.View;

import java.nio.file.Path;

public interface Model {

    void find(Path root, String searchText, String filesType);

    void openFile(Path file);

    String getFilePart(Path file, int part);

    void setView(View view);
}
