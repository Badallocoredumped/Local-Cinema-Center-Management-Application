package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InvoiceDBO 
{
    public void saveInvoice(int ticketId, byte[] pdfData, String format) throws Exception 
    {
        if (pdfData == null || pdfData.length == 0) 
        {
            throw new IllegalArgumentException("PDF data cannot be null or empty");
        }

        String sql = "INSERT INTO Invoices (ticket_id, invoice_format, invoice_file) VALUES (?, ?, ?)";
        
        try (Connection conn = DataBaseHandler.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            pstmt.setString(2, format);
            pstmt.setBytes(3, pdfData);
            
            int result = pstmt.executeUpdate();
            if (result != 1) {
                throw new SQLException("Failed to save invoice to database");
            }
            System.out.println("Successfully saved PDF to database for ticket: " + ticketId);
        }
}
}