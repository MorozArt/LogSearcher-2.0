package com.moroz.logsearcher.view;

import com.moroz.logsearcher.controller.Controller;
import com.moroz.logsearcher.model.FileTree;
import javafx.util.Pair;

import java.util.List;

public interface View {

    void setFileTree(FileTree fileTree);

    void setOpenFile(String fileText, int filePartCount, List<Pair<Integer, Integer> > foundTextIndexes);

    void show();

    void setController(Controller controller);
}
