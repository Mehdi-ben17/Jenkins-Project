package com.ehei;

public class App {
    public static void main(String[] args) {
        UserManager userManager = new UserManager();

        // Ajout de quelques utilisateurs
        System.out.println("Ajout de John : " + userManager.addUser("John"));
        System.out.println("Ajout de Alice : " + userManager.addUser("Alice"));
        System.out.println("Ajout d'un nom vide : " + userManager.addUser(" "));

        // Afficher les utilisateurs
        System.out.println("Utilisateurs : " + userManager.getUsers());

        // Suppression d'un utilisateur
        System.out.println("Suppression de John : " + userManager.removeUser("John"));
        System.out.println("Suppression d'un utilisateur inexistant : " + userManager.removeUser("Bob"));

        // Afficher les utilisateurs restants
        System.out.println("Utilisateurs restants : " + userManager.getUsers());
    }
}
