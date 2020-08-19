package com.example.pramodgobburi.cheep.Models;

public class User {

    private static String username;
    private static int id;
    private static String firstname;
    private static String lastname;
    private static String email;

    private static String accessToken;
    private static String refreshToken;
    private static boolean isAuthenticated;

    public static User currentUser;

    public static User getInstance() {
        if(currentUser == null) {
            currentUser = new User();
        }
        return currentUser;
    }

    private User() {

    }

    public void setUserData(int id, String username, String firstname, String lastname, String email, boolean isAuthenticated) {
        currentUser.id = id;
        currentUser.username = username;
        currentUser.firstname = firstname;
        currentUser.lastname = lastname;
        currentUser.email = email;
        currentUser.isAuthenticated = isAuthenticated;
    }

    public void setUserData(String username, String firstname, String lastname, String email, boolean isAuthenticated) {
        currentUser.username = username;
        currentUser.firstname = firstname;
        currentUser.lastname = lastname;
        currentUser.email = email;
        currentUser.isAuthenticated = isAuthenticated;
    }


    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getAccessToken() {
        return currentUser.accessToken;
    }

    public static void setAccessToken(String accessToken) {
        currentUser.accessToken = accessToken;
    }

    public static String getRefreshToken() {
        return currentUser.refreshToken;
    }

    public static void setRefreshToken(String refreshToken) {
        currentUser.refreshToken = refreshToken;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        User.id = id;
    }

    public static String getFirstname() {
        return firstname;
    }

    public static void setFirstname(String firstname) {
        User.firstname = firstname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        User.lastname = lastname;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static boolean isIsAuthenticated() {
        return isAuthenticated;
    }

    public static void setIsAuthenticated(boolean isAuthenticated) {
        User.isAuthenticated = isAuthenticated;
    }
}
