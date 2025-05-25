
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

import model.Device;
import model.dao.OrderDAO;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
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
        orderDAO = new OrderDAO(conn);
    }

    /* Test Case 1: Successful Order Creation
     * - Verifies that order creation returns generated order ID
     * - Mocks database key generation process
     * - Checks parameter binding and query execution */
    @Test
    public void testCreateOrder_Success() throws SQLException {
        // Mock order creation with generated keys
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(testOrderId);

        int result = orderDAO.createOrder(testUserId);
        
        assertEquals("Should return mock-generated order ID", testOrderId, result);
        verify(ps).setInt(1, testUserId);
        verify(ps).executeUpdate();
    }

    /* Test Case 2: Order Creation Failure
     * - Verifies exception handling when no ID is generated
     * - Simulates empty generated keys result
     * - Ensures proper error propagation */
    @Test(expected = SQLException.class)
    public void testCreateOrder_Failure() throws SQLException {
        // Mock failed key generation
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);

        orderDAO.createOrder(testUserId);
    }

    /* Test Case 3: Find Existing Active Order
     * - Verifies retrieval of active order ID
     * - Mocks database response with valid order ID
     * - Checks parameter binding and result parsing */
    @Test
    public void testFindActiveOrderId_Exists() throws SQLException {
        // Mock active order query response
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("order_id")).thenReturn(testOrderId);

        int result = orderDAO.findActiveOrderId(testUserId);
        
        assertEquals("Should return mock order ID", testOrderId, result);
        verify(ps).setInt(1, testUserId);
    }

    /* Test Case 4: Active Order Not Found
     * - Verifies handling of missing active orders
     * - Mocks empty result set
     * - Ensures proper default return value (-1) */
    @Test
    public void testFindActiveOrderId_NotFound() throws SQLException {
        // Mock empty result set
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        int result = orderDAO.findActiveOrderId(testUserId);
        
        assertEquals("Should return -1 for missing order", -1, result);
    }

    /* Test Case 5: Add New Order Item
     * - Verifies new item insertion workflow
     * - Mocks absence of existing items (SELECT returns empty)
     * - Validates INSERT parameter binding and values */
    @Test
    public void testAddOrderItem_NewItem() throws SQLException {
        // Mock SELECT (no existing items)
        PreparedStatement checkStmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("SELECT quantity FROM OrderItem WHERE order_id = ? AND device_id = ?"))
            .thenReturn(checkStmt);
        when(checkStmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        // Mock INSERT statement
        PreparedStatement insertStmt = mock(PreparedStatement.class);
        when(conn.prepareStatement("INSERT INTO OrderItem (order_id, device_id, quantity, unit_price) VALUES (?, ?, ?, ?)"))
            .thenReturn(insertStmt);

        Device device = new Device(testDeviceId, "Test Sensor", "Sensor", 49.99, 10);
        orderDAO.addOrderItem(testOrderId, device, 2);

        // Verify SELECT parameters
        verify(checkStmt).setInt(1, testOrderId);
        verify(checkStmt).setInt(2, testDeviceId);
        
        // Verify INSERT parameters
        verify(insertStmt).setInt(1, testOrderId);
        verify(insertStmt).setInt(2, testDeviceId);
        verify(insertStmt).setInt(3, 2);
        verify(insertStmt).setDouble(4, 49.99);
        verify(insertStmt).executeUpdate();
    }

    /* Test Case 6: Update Order Status
     * - Verifies status update functionality
     * - Mocks UPDATE query execution
     * - Checks parameter binding and query execution */
    @Test
    public void testUpdateOrderStatus_Success() throws SQLException {
        // Mock status update query
        when(conn.prepareStatement("UPDATE `order` SET status = ? WHERE order_id = ?"))
            .thenReturn(ps);

        orderDAO.updateOrderStatus(testOrderId, "submitted");
        
        verify(ps).setString(1, "submitted");
        verify(ps).setInt(2, testOrderId);
        verify(ps).executeUpdate();
    }

    /* Test Case 7: Retrieve Order Items
     * - Verifies item quantity parsing from result set
     * - Mocks multiple items in response
     * - Validates quantity mapping correctness */
    @Test
    public void testGetOrderItems_WithItems() throws SQLException {
        // Mock multi-item response
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("device_id")).thenReturn(testDeviceId, 501);
        when(rs.getInt("quantity")).thenReturn(3, 1);

        Map<Integer, Integer> result = orderDAO.getOrderItems(testOrderId);
        
        assertEquals("Should contain 2 items", 2, result.size());
        assertEquals("Should have correct quantity for first item", 
            Integer.valueOf(3), result.get(testDeviceId));
    }

    /* Test Case 8: Retrieve Draft Order Status
     * - Verifies status retrieval workflow
     * - Mocks database response with 'DRAFT' status
     * - Checks parameter binding and value parsing */
    @Test
    public void testGetOrderStatus_Draft() throws SQLException {
        // Mock status query response
        when(conn.prepareStatement("SELECT status FROM `order` WHERE order_id = ?"))
            .thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("status")).thenReturn("DRAFT");

        String status = orderDAO.getOrderStatus(testOrderId);
        
        assertEquals("Should return mock status", "DRAFT", status);
        verify(ps).setInt(1, testOrderId);
    }
}