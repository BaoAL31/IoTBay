
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.*;
import java.util.*;

import model.Device;
import model.dao.OrderDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderDAOTest {

    @Mock private Connection conn;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;
    @Mock private ResultSet generatedKeys;
    
    private OrderDAO orderDAO;
    private final int testUserId = 1;
    private final int testOrderId = 100;
    private final int testDeviceId = 500;

    @Before
    public void setUp() throws SQLException {
        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        orderDAO = new OrderDAO(conn);
    }

    @Test
    public void testCreateOrder_Success() throws SQLException {
        // Mock generated keys
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(testOrderId);

        int createdOrderId = orderDAO.createOrder(testUserId);
        
        assertEquals(testOrderId, createdOrderId);
        verify(ps).setInt(1, testUserId);
        verify(ps).executeUpdate();
    }

    @Test(expected = SQLException.class)
    public void testCreateOrder_Failure() throws SQLException {
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);
        
        orderDAO.createOrder(testUserId);
    }

    @Test
    public void testFindActiveOrderId_Exists() throws SQLException {
        mockResultSetWithOrderId(testOrderId);
        
        int foundOrderId = orderDAO.findActiveOrderId(testUserId);
        
        assertEquals(testOrderId, foundOrderId);
        verify(ps).setInt(1, testUserId);
    }

    @Test
    public void testFindActiveOrderId_NotFound() throws SQLException {
        when(rs.next()).thenReturn(false);
        
        int result = orderDAO.findActiveOrderId(testUserId);
        
        assertEquals(-1, result);
    }

    @Test
    public void testAddOrderItem_NewItem() throws SQLException {
        // Mock check query
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        Device testDevice = new Device(testDeviceId, "Test Sensor", "Sensor", 49.99, 10);
        orderDAO.addOrderItem(testOrderId, testDevice, 2);
        
        verify(conn, times(2)).prepareStatement(anyString()); // Check + insert
        verify(ps).setInt(1, testOrderId);
        verify(ps).setInt(2, testDeviceId);
        verify(ps).setInt(3, 2);
        verify(ps).setDouble(4, 49.99);
    }

    @Test
    public void testUpdateOrderStatus_Success() throws SQLException {
        orderDAO.updateOrderStatus(testOrderId, "submitted");
        
        verify(ps).setString(1, "submitted");
        verify(ps).setInt(2, testOrderId);
        verify(ps).executeUpdate();
    }

    @Test
    public void testGetOrderItems_WithItems() throws SQLException {
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(testDeviceId, 3);
        expected.put(501, 1);

        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("device_id")).thenReturn(testDeviceId, 501);
        when(rs.getInt("quantity")).thenReturn(3, 1);

        Map<Integer, Integer> result = orderDAO.getOrderItems(testOrderId);
        
        assertEquals(expected.size(), result.size());
        assertEquals(Integer.valueOf(3), result.get(testDeviceId));
    }

    @Test
    public void testGetOrderStatus_Draft() throws SQLException {
        mockSingleStringResult("DRAFT");
        
        String status = orderDAO.getOrderStatus(testOrderId);
        
        assertEquals("DRAFT", status);
    }

    // Helper methods
    private void mockResultSetWithOrderId(int orderId) throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("order_id")).thenReturn(orderId);
    }

    private void mockSingleStringResult(String value) throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString(1)).thenReturn(value);
    }
}