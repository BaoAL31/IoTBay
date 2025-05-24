import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import model.User;
import model.dao.DBConnector;
import model.dao.UserDAO;

public class UserDAOTest {
    private UserDAO userDAO;
    private DBConnector dbConnector;

    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        // Initialize DBConnector and UserDAO
        dbConnector = new DBConnector();
        userDAO = new UserDAO();
    }

    @Test
    public void testCreateUser() throws SQLException {
        // Arrange
        User user = new User("Test User", "testuser@example.com", "password123", "1234567890", "123 Main St", "user");
        user.setStatus("activated");

        // Act
        boolean result = userDAO.createUser(user);

        // Assert
        assertTrue(result);
        assertNotNull(user.getUserID());  // User should have an ID after creation
    }

    @Test
    public void testGetUserById() throws SQLException {
        // Arrange
        User user = new User("Test User", "testuser2@example.com", "password123", "0987654321", "456 Elm St", "user");
        user.setStatus("activated");
        userDAO.createUser(user);

        // Act
        User fetchedUser = userDAO.getUserById(user.getUserID());

        // Assert
        assertNotNull(fetchedUser);
        assertEquals(user.getUserID(), fetchedUser.getUserID());
        assertEquals(user.getEmail(), fetchedUser.getEmail());
    }

    @Test
    public void testDeleteUser() throws SQLException {
        // Arrange
        User user = new User("Test User", "testuser4@example.com", "password123", "4445556666", "321 Oak St", "user");
        user.setStatus("activated");
        userDAO.createUser(user);

        // Act
        boolean result = userDAO.deleteUser(user.getUserID());

        // Assert
        assertTrue(result);
        User deletedUser = userDAO.getUserById(user.getUserID());
        assertNull(deletedUser);  // User should be null after deletion
    }

    @Test
    public void testGetUsersByType() throws SQLException {
        // Arrange
        User user1 = new User("Admin User", "admin@example.com", "password123", "1234567890", "123 Admin St", "admin");
        user1.setStatus("activated");
        userDAO.createUser(user1);
        
        User user2 = new User("Regular User", "user@example.com", "password123", "0987654321", "456 User St", "user");
        user2.setStatus("activated");
        userDAO.createUser(user2);

        // Act
        List<User> admins = userDAO.getUsersByType("admin");
        List<User> users = userDAO.getUsersByType("user");

        // Assert
        assertEquals(2, admins.size());
        assertEquals("Admin User", admins.get(1).getFullName());

        assertEquals("Regular User", users.get(users.size() - 1).getFullName());
    }

    @Test
    public void testSearchUsersByName() throws SQLException {
        // Arrange
        User user1 = new User("Mike Testee", "miketestee@example.com", "password123", "5555455555", "123 Main St", "user");
        user1.setStatus("activated");
        userDAO.createUser(user1);
        
        User user2 = new User("Mike Tester", "miketester@example.com", "password123", "5555555545", "456 Oak St", "user");
        user2.setStatus("activated");
        userDAO.createUser(user2);

        // Act
        List<User> foundUsers = userDAO.searchUsersByName("Mike");

        // Assert
        assertEquals(2, foundUsers.size());  // Both "John Doe" and "Jane Doe" should be found
    }

}
