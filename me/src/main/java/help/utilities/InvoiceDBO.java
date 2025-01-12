package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InvoiceDBO 
{
    /**
     * Saves the generated invoice PDF to the database for a specified ticket.
     * 
     * <p>This method inserts a new record into the Invoices table, associating the ticket ID with the PDF invoice data.
     * The invoice format (e.g., PDF, etc.) and the PDF content are stored in the database.</p>
     * 
     * @param ticketId The ID of the ticket for which the invoice is being saved.
     * @param pdfData The PDF content as a byte array representing the invoice.
     * @param format The format of the invoice (e.g., "PDF").
     * 
     * @throws IllegalArgumentException If the PDF data is null or empty.
     * @throws SQLException If there is an error while inserting the invoice data into the database.
     */
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