package com.ehei;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    private UserManager userManager;

    @BeforeEach
    public void setUp() {
        userManager = new UserManager();
    }

    @Test
    public void testAddUserValid() {
        assertTrue(userManager.addUser("John"), "Adding a valid user should return true");
    }

    @Test
    public void testAddUserNullOrEmpty() {
        assertFalse(userManager.addUser(null), "Adding a null user should return false");
        assertFalse(userManager.addUser(" "), "Adding an empty user should return false");
    }

    @Test
    public void testRemoveUserExists() {
        userManager.addUser("Alice");
        assertTrue(userManager.removeUser("Alice"), "Removing an existing user should return true");
    }

    @Test
    public void testRemoveUserDoesNotExist() {
        assertFalse(userManager.removeUser("Bob"), "Removing a non-existing user should return false");
    }

    @Test
    public void testGetUsers() {
        userManager.addUser("John");
        userManager.addUser("Alice");

        java.util.List<String> users = userManager.getUsers();
        assertEquals(2, users.size(), "There should be 2 users in the list");
        assertTrue(((java.util.List<?>) users).contains("John"), "List should contain 'John'");
        assertTrue(users.contains("Alice"), "List should contain 'Alice'");
    }

    @Test
    public void testGetUsersImmutable() {
        userManager.addUser("John");
        java.util.List<String> users = userManager.getUsers();
        users.add("FakeUser"); // This should not modify the original list

        assertEquals(1, userManager.getUsers().size(), "Original list should not be affected by external modifications");
    }
}
