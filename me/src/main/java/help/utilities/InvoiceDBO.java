package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class InvoiceDBO {
    public void saveInvoice(int ticketId, byte[] pdfData, String format) throws Exception
     {
        String sql = "INSERT INTO Invoices (ticket_id, invoice_format, invoice_file) VALUES (?, ?, ?)";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            pstmt.setString(2, format);
            pstmt.setBytes(3, pdfData);
            pstmt.executeUpdate();
        }
    }
}