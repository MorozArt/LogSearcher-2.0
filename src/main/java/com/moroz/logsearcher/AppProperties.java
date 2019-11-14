package com.moroz.logsearcher;

import java.io.*;
import java.util.Properties;

public class AppProperties {

    private static Properties prop = new Properties();
    private static InputStreamReader reader;

    static {
        try {
            reader = new InputStreamReader(new FileInputStream(Thread.currentThread().getContextClassLoader()
                    .getResource("config.properties").getPath()), "windows-1251");
            prop.load(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String propertyName) {
        return prop.getProperty(propertyName, "Missing property!");
    }
}
