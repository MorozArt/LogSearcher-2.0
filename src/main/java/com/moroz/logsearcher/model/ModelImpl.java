package com.moroz.logsearcher.model;

import com.moroz.logsearcher.AppProperties;
import com.moroz.logsearcher.controller.ControllerImpl;
import com.moroz.logsearcher.view.View;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ModelImpl implements Model {

    private static final Logger log = LogManager.getLogger(ModelFileVisitor.class.getName());

    private View view;
    private Path root;
    private List<FoundFile> foundFiles;

    @Override
    public void find(Path root, String searchText, String filesType) {
        log.trace("find with root: "+root+" searchText: "+searchText+" filesType: "+filesType);
        this.root = root;

        try {
            ModelFileVisitor modelFileVisitor = new ModelFileVisitor(searchText, filesType, root);
            Files.walkFileTree(root, modelFileVisitor);

            foundFiles = modelFileVisitor.getFoundFiles();
            view.setFileTree(modelFileVisitor.getFileTree());
        } catch (IOException e) {
            log.error("exception in method \"find\"", e);
        }
    }

    @Override
    public void openFile(Path subPath) {
        FoundFile foundFile = getFoundFile(subPath);
        if (foundFile != null) {
            log.trace("open file: "+foundFile.getFilePath());
            view.setOpenFile(readFile(foundFile, 0), foundFile.getPartIndexes().size(), foundFile.getFoundTextIndexes());
        } else {
            log.warn("file "+subPath+" not find!");
        }
    }

    @Override
    public String getFilePart(Path subPath, int part) {
        FoundFile foundFile = getFoundFile(subPath);
        if (foundFile != null) {
            log.trace("return text for file: "+foundFile.getFilePath()+" for part: "+part);
            return readFile(foundFile, part);
        }

        log.warn("file "+subPath+" not find!");
        return "-->Read file error!<--";
    }

    private FoundFile getFoundFile(Path subPath) {
        Path file = root.getParent().resolve(subPath);

        for(FoundFile foundFile : foundFiles) {
            if(foundFile.getFilePath().equals(file)) {
                return foundFile;
            }
        }

        return null;
    }

    private String readFile(FoundFile foundFile, int part) {
        log.trace("read file: "+foundFile.getFilePath()+" part: "+part);
        StringBuilder stringBuilder = new StringBuilder("");
        try(BufferedReader reader = new BufferedReader(new FileReader(foundFile.getFilePath().toFile()))) {

            reader.skip(foundFile.getPartIndexes().get(part));
            int stringCount = 0;
            int stringsInPartCount = Integer.parseInt(AppProperties.getProperty("stringsInPartCount"));
            while (reader.ready() && stringCount < stringsInPartCount) {
                stringBuilder.append(reader.readLine()+'\n');
                ++stringCount;
            }
        } catch (IOException e) {
            log.error("exception at read file: "+foundFile.getFilePath(), e);
        }

        return stringBuilder.toString();
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }
}
