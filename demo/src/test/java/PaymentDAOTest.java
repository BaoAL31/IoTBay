
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

import model.dao.PaymentDAO;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class PaymentDAOTest {

    @Mock
    private Connection conn;

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;

    private PaymentDAO paymentDAO;

    @Before
    public void setUp() {
        paymentDAO = new PaymentDAO(conn);
    }

    /* Test Case 1: Filter Payments with All Criteria
     * - Verifies payment IDs retrieval using status, ID search, and date filters
     * - Mocks database response with sample payment ID
     * - Checks parameter binding for user ID, status, and search patterns */
    @Test
    public void getPaymentIdsByStatusAndSearchQuery_AllFilters() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("payment_id")).thenReturn(100);

        List<Integer> result = paymentDAO.getPaymentIdsByStatusAndSearchQuery(1, "completed", "100", "2023-10-01");

        assertEquals(Collections.singletonList(100), result);
        verify(ps).setInt(1, 1);
        verify(ps).setString(2, "completed");
        verify(ps).setString(3, "%100%");
        verify(ps).setString(4, "%2023-10-01%");
    }

    /* Test Case 2: Retrieve Payment Timestamp for Valid Order
     * - Validates paid_at timestamp retrieval for existing order ID
     * - Mocks database response with sample datetime
     * - Verifies correct column mapping for timestamp value */
    @Test
    public void getPaidDateByOrderId_Exists() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("paid_at")).thenReturn("2023-10-01 10:00:00");

        String paidDate = paymentDAO.getPaidDateByOrderId(1);

        assertEquals("2023-10-01 10:00:00", paidDate);
    }

    /* Test Case 3: Fetch Payment Items with Quantities
     * - Verifies item-quantity mapping for valid payment ID
     * - Mocks joined PaymentItem-Device table response
     * - Checks correct quantity mapping for multiple devices */
    @Test
    public void getPaymentItems_ValidPaymentId() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("device_id")).thenReturn(1, 2);
        when(rs.getInt("quantity")).thenReturn(3, 5);

        Map<Integer, Integer> items = paymentDAO.getPaymentItems(100);

        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(1, 3);
        expected.put(2, 5);
        assertEquals(expected, items);
    }

    /* Test Case 4: Create New Payment Record
     * - Validates insertion of payment with all required fields
     * - Mocks prepared statement parameter binding
     * - Verifies persistence of method, card number, and amount */
    @Test
    public void createPayment_Success() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);

        paymentDAO.createPayment(1, "Credit", "1234-5678", 99.99, "completed");

        verify(ps).setInt(1, 1);
        verify(ps).setString(2, "Credit");
        verify(ps).setString(3, "1234-5678");
        verify(ps).setDouble(4, 99.99);
        verify(ps).setString(5, "completed");
        verify(ps).executeUpdate();
    }

    /* Test Case 5: Delete Existing Payment
     * - Verifies payment deletion by ID
     * - Checks correct payment_id parameter binding
     * - Confirms database delete operation execution */
    @Test
    public void deletePayment_Success() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);

        paymentDAO.deletePayment(100);

        verify(ps).setInt(1, 100);
        verify(ps).executeUpdate();
    }

    /* Test Case 6: Get Associated Order ID
     * - Validates order ID retrieval for payment ID
     * - Mocks database response with linked order
     * - Verifies payment-order relationship integrity */
    @Test
    public void getOrderIdByPaymentId_Exists() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("order_id")).thenReturn(200);

        int orderId = paymentDAO.getOrderIdByPaymentId(100);

        assertEquals(200, orderId);
    }

    /* Test Case 7: Retrieve Payment Card Details
     * - Verifies card number retrieval for order
     * - Mocks encrypted card number storage
     * - Checks correct column mapping for sensitive data */
    @Test
    public void getCardNumberByOrderId_Exists() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("card_number")).thenReturn("1234-5678");

        String cardNumber = paymentDAO.getCardNumberByOrderId(1);

        assertEquals("1234-5678", cardNumber);
    }

    /* Test Case 8: Get Payment Method Type
     * - Validates payment method retrieval for order
     * - Mocks database response with payment type
     * - Verifies correct method enum mapping */
    @Test
    public void getPaymentMethodByOrderId_Exists() throws SQLException {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("method")).thenReturn("Debit");

        String method = paymentDAO.getPaymentMethodByOrderId(1);

        assertEquals("Debit", method);
    }
}