import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import model.Device;
import model.dao.DBConnector;
import model.dao.DeviceDAO;

public class DeviceDAOTest {

    private DeviceDAO deviceDAO;
    private DBConnector dbConnector;

    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        // Initialize DBConnector and DeviceDAO
        dbConnector = new DBConnector();
        deviceDAO = new DeviceDAO(dbConnector.openConnection());
    }

    @Test
    public void testAddDevice() throws SQLException {
        // Arrange
        Device device = new Device(0, "Test Device", "Sensor", 19.99, 100);

        // Act
        deviceDAO.addDevice(device);

        // Assert
        assertNotEquals(0, device.getId()); // Device should have an ID after insertion
    }

    @Test
    public void testGetDeviceById() throws SQLException {
        // Arrange
        Device device = new Device(0, "Sample Device", "Camera", 99.99, 50);
        deviceDAO.addDevice(device);

        // Act
        Device fetchedDevice = deviceDAO.getDeviceById(device.getId());

        // Assert
        assertNotNull(fetchedDevice);
        assertEquals(device.getId(), fetchedDevice.getId());
        assertEquals(device.getName(), fetchedDevice.getName());
        assertEquals(device.getType(), fetchedDevice.getType());
        assertEquals(device.getPrice(), fetchedDevice.getPrice(), 0.001);
        assertEquals(device.getStock(), fetchedDevice.getStock());
    }

    @Test
    public void testUpdateDevice() throws SQLException {
        // Arrange
        Device device = new Device(0, "Old Device", "Sensor", 25.99, 10);
        deviceDAO.addDevice(device);

        device.setName("Updated Device");
        device.setPrice(29.99);
        device.setStock(20);

        // Act
        deviceDAO.updateDevice(device);

        // Assert
        Device updatedDevice = deviceDAO.getDeviceById(device.getId());
        assertEquals("Updated Device", updatedDevice.getName());
        assertEquals(29.99, updatedDevice.getPrice(), 0.01);
        assertEquals(20, updatedDevice.getStock());
    }

    @Test
    public void testDeleteDevice() throws SQLException {
        // Arrange
        Device device = new Device(0, "Device to Delete", "Speaker", 199.99, 5);
        deviceDAO.addDevice(device);

        // Act
        deviceDAO.deleteDevice(device.getId());

        // Assert
        Device deletedDevice = deviceDAO.getDeviceById(device.getId());
        assertNull(deletedDevice); // Device should be null after deletion
    }

    @Test
    public void testGetAllDevices() throws SQLException {
        // Arrange
        Device device1 = new Device(0, "Device 1", "Phone", 699.99, 50);
        Device device2 = new Device(0, "Device 2", "Laptop", 999.99, 30);
        deviceDAO.addDevice(device1);
        deviceDAO.addDevice(device2);

        // Act
        List<Device> devices = deviceDAO.getAllDevices();

        // Assert
        assertEquals(2, devices.size()); // Ensure both devices are returned
        assertTrue(devices.stream().anyMatch(d -> d.getName().equals("Device 1")));
        assertTrue(devices.stream().anyMatch(d -> d.getName().equals("Device 2")));
    }

    @Test
    public void testSearchDevices() throws SQLException {
        // Arrange
        Device device1 = new Device(0, "Smartphone", "Phone", 299.99, 50);
        Device device2 = new Device(0, "Smartwatch", "Wearable", 149.99, 75);
        deviceDAO.addDevice(device1);
        deviceDAO.addDevice(device2);

        // Act
        List<Device> phones = deviceDAO.searchDevices("Smartphone", "");
        List<Device> wearables = deviceDAO.searchDevices("", "Wearable");

        // Assert
        assertEquals(1, phones.size()); // Only one "Smartphone" device should be returned
        assertEquals(1, wearables.size()); // Only one "Smartwatch" device should be returned
    }

    @Test
    public void testGetStock() throws SQLException {
        // Arrange
        Device device = new Device(0, "Test Device", "Sensor", 50.99, 100);
        deviceDAO.addDevice(device);

        // Act
        int stock = deviceDAO.getStock(device.getId());

        // Assert
        assertEquals(100, stock); // Ensure stock matches the added device's stock
    }

    @Test
    public void testUpdateStock() throws SQLException {
        // Arrange
        Device device = new Device(0, "Test Device", "Sensor", 50.99, 100);
        deviceDAO.addDevice(device);

        // Act
        deviceDAO.updateStock(device.getId(), 120);
        int updatedStock = deviceDAO.getStock(device.getId());

        // Assert
        assertEquals(120, updatedStock); // Ensure stock is updated correctly
    }

    @Test(expected = SQLException.class)
    public void testAddDeviceWithNegativePrice() throws SQLException {
        Device invalidDevice = new Device(0, "Invalid Device", "Sensor", -10.0, 5);
        deviceDAO.addDevice(invalidDevice); // Should throw SQLException or fail validation
    }

}
