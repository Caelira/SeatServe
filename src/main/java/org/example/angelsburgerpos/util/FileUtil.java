package org.example.angelsburgerpos.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//handles file io
public class FileUtil {

    // reads a file, returns a string
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    // Reads a file from the resource folder. (inside project)_
    public static List<String> readResource(String resourcePath) {
        List<String> lines = new ArrayList<>();
        InputStream is = FileUtil.class.getResourceAsStream(resourcePath);
        if (is != null) {
            try (Scanner scanner = new Scanner(is)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                    }
                }
            }
        }
        return lines;
    }

    // creates the directory if missing
    public static void createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // appends a line
    public static void appendLine(String filePath, String line) {
        try (FileWriter fw = new FileWriter(filePath, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}