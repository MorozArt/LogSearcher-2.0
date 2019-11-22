package com.moroz.logsearcher.model;

import com.moroz.logsearcher.AppProperties;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SearchTextService {

    private static final Logger log = LogManager.getLogger(SearchTextService.class.getName());

    public FoundFile search(Path file, String searchText) {
        List<Long> partIndexes = new ArrayList<>();
        List<Pair<Integer, Integer> > foundTextIndexes = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            Long position = 0L;
            partIndexes.add(position);

            int stringsCount = 0;
            int currentPart = 0;
            int stringsInPartCount = Integer.parseInt(AppProperties.getProperty("stringsInPartCount"));
            while ((line = reader.readLine()) != null) {
                if(stringsCount == stringsInPartCount) {
                    partIndexes.add(position);
                    stringsCount = 0;
                    ++currentPart;
                }

                Matcher matcher = Pattern.compile(searchText).matcher(line);
                while (matcher.find())
                {
                    Long index = (position + matcher.start() - stringsCount) - partIndexes.get(partIndexes.size()-1);
                    foundTextIndexes.add(new Pair<>(currentPart, index.intValue()));
                }

                position += line.length()+2;
                ++stringsCount;
            }
        } catch (IOException e) {
            log.error("exception at searching in file: "+file, e);
        }

        if(!foundTextIndexes.isEmpty()) {
            return new FoundFile(file, partIndexes, foundTextIndexes);
        } else {
            return null;
        }
    }
}
