package com.moroz.logsearcher.model;

import com.moroz.logsearcher.AppProperties;
import com.moroz.logsearcher.view.View;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class ModelImpl implements Model {

    private View view;
    private Path root;
    private List<FoundFile> foundFiles;

    @Override
    public void find(Path root, String searchText, String filesType) {
        this.root = root;

        try {
            ModelFileVisitor modelFileVisitor = new ModelFileVisitor(searchText, filesType, root);
            Files.walkFileTree(root, modelFileVisitor);

            foundFiles = modelFileVisitor.getFoundFiles();
            view.setFileTree(modelFileVisitor.getFileTree());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openFile(Path subPath) {
        FoundFile foundFile = getFoundFile(subPath);
        if (foundFile != null) {
            view.setOpenFile(readFile(foundFile, 0), foundFile.getPartIndexes().size(), foundFile.getFoundTextIndexes());
        }
    }

    @Override
    public String getFilePart(Path subPath, int part) {
        FoundFile foundFile = getFoundFile(subPath);
        if (foundFile != null) {
            return readFile(foundFile, part);
        }

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
        StringBuilder stringBuilder = new StringBuilder("");
        try(BufferedReader reader = new BufferedReader(new FileReader(foundFile.getFilePath().toFile()))) {

            reader.skip(foundFile.getPartIndexes().get(part));
            int stringCount = 0;
            int stringsInPartCount = Integer.parseInt(AppProperties.getProperty("stringsInPartCount"));
            while (reader.ready() && stringCount < stringsInPartCount) {
                stringBuilder.append(reader.readLine()+'\n');
                ++stringCount;
            }
        } catch (FileNotFoundException ignore) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }
}
