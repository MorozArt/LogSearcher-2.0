package com.moroz.logsearcher.model;

import javafx.util.Pair;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

class FoundFile {

    private Path filePath;
    private List<Long> partIndexes;
    private List<Pair<Integer, Integer> > foundTextIndexes;

    public FoundFile(Path filePath, List<Long> partIndexes, List<Pair<Integer, Integer> > foundTextIndexes) {
        this.filePath = filePath;
        this.partIndexes = partIndexes;
        this.foundTextIndexes = foundTextIndexes;
    }

    public Path getFilePath() {
        return filePath;
    }

    public List<Long> getPartIndexes() {
        return partIndexes;
    }

    public List<Pair<Integer, Integer> > getFoundTextIndexes() {
        return foundTextIndexes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundFile foundFile = (FoundFile) o;
        return Objects.equals(filePath, foundFile.filePath);
    }

    @Override
    public int hashCode() {

        return Objects.hash(filePath);
    }
}
