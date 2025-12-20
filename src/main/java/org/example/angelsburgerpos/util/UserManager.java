package org.example.angelsburgerpos.util;

import org.example.angelsburgerpos.model.User;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static List<User> users = new ArrayList<>();

    public static List<User> getAllUsers() {
        if (users.isEmpty()) loadUsers();
        return users;
    }

    public static void loadUsers() {
        users.clear();
        List<String> lines = FileUtil.readLines(AppConfig.USERS_FILE);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                users.add(new User(parts[0], parts[1], parts[2], parts[3]));
            }
        }
    }

    public static void addUser(User newUser) {
        users.add(newUser);
        saveUsers();
    }

    public static void archiveUser(User user, String reason) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("%s,USER,%s,%s,%s,-,%s",
                timestamp, user.getUsername(), user.getFullname(), user.getRole(), reason);

        FileUtil.appendLine(AppConfig.ARCHIVES_FILE, logEntry);

        users.remove(user);
        saveUsers();
    }

    private static void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new File(AppConfig.USERS_FILE))) {
            for (User u : users) {
                writer.printf("%s,%s,%s,%s%n", u.getUsername(), u.getPassword(), u.getRole(), u.getFullname());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}