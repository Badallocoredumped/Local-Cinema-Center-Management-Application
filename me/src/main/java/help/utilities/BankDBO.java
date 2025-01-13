package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Database Handler class for managing bank-related operations.
 * Provides methods for adding, updating, deleting, and fetching bank account data from the database.
 */
public class BankDBO 
{
    /**
     * Updates the bank's total revenue and tax to be paid in the database.
     * 
     * This method adds the provided revenue and tax values to the existing totals in the "bank" table.
     * It assumes that there is a record with an ID of 1, and it updates the `total_revenue` and `tax_to_be_paid` fields.
     * 
     * @param revenue The amount of revenue to add to the total revenue.
     * @param tax The amount of tax to add to the tax to be paid.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     * @throws Exception If there is an error executing the update query or obtaining the database connection.
     */
    public boolean updateBankTotals(double revenue, double tax) throws Exception 
    {
        String sql = "UPDATE bank SET total_revenue = total_revenue + ?, tax_to_be_paid = tax_to_be_paid + ? WHERE id = 1";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, revenue);
            pstmt.setDouble(2, tax);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates the bank's total revenue and tax to be paid based on the totals from the "tickets" table.
     * 
     * This method retrieves the sum of `total_cost` and `total_tax` from the "tickets" table,
     * and then updates the `total_revenue` and `tax_to_be_paid` fields in the "bank" table for the record with ID = 1.
     * 
     * @return {@code true} if the update was successful, {@code false} otherwise.
     * @throws Exception If there is an error retrieving the totals or executing the update query.
     */
    public boolean updateBankTotalsFromTickets() throws Exception 
    {
        String getTotalsSQL = "SELECT SUM(total_cost) as revenue, SUM(total_tax) as tax FROM tickets";
        String updateBankSQL = "UPDATE bank SET total_revenue = ?, tax_to_be_paid = ? WHERE id = 1";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement getTotals = conn.prepareStatement(getTotalsSQL);
             PreparedStatement updateBank = conn.prepareStatement(updateBankSQL)) {
            
            // Get totals from tickets
            ResultSet rs = getTotals.executeQuery();
            if (rs.next()) {
                double totalRevenue = rs.getDouble("revenue");
                double totalTax = rs.getDouble("tax");
                
                // Update bank table
                updateBank.setDouble(1, totalRevenue);
                updateBank.setDouble(2, totalTax);
                
                return updateBank.executeUpdate() > 0;
            }
            return false;
        }
    }

    /**
     * Processes a full refund by adjusting the bank's total revenue and tax to be paid.
     * 
     * This method calculates the tax for tickets (20%) and products (10%) based on the provided amounts.
     * It then subtracts the total refund amount (ticket and product amounts) and the total tax (ticket and product taxes)
     * from the corresponding fields in the "bank" table for the record with ID = 1.
     * 
     * @param ticketAmount The amount paid for tickets.
     * @param productAmount The amount paid for products.
     * @return {@code true} if the refund was processed successfully, {@code false} otherwise.
     * @throws Exception If there is an error executing the update query.
     */
    public boolean processFullRefund(double ticketAmount, double productAmount) throws Exception 
    {
        double ticketTax = ticketAmount * 0.20; // 20% tax for tickets
        double productTax = productAmount * 0.10; // 10% tax for products
        double totalRefund = ticketAmount + productAmount;
        double totalTax = ticketTax + productTax;
        
        String sql = "UPDATE bank SET total_revenue = total_revenue - ?, tax_to_be_paid = tax_to_be_paid - ? WHERE id = 1";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, totalRefund);
            pstmt.setDouble(2, totalTax);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Processes a refund for a product by adjusting the bank's total revenue and tax to be paid.
     * 
     * This method calculates the tax for the product (10%) based on the provided amount.
     * It then subtracts the product amount and the calculated tax from the corresponding fields 
     * in the "bank" table for the record with ID = 1.
     * 
     * @param productAmount The amount paid for the product.
     * @return {@code true} if the refund was processed successfully, {@code false} otherwise.
     * @throws Exception If there is an error executing the update query.
     */
    public boolean processProductRefund(double productAmount) throws Exception {
        double productTax = productAmount * 0.10; // 10% tax for products
        
        String sql = "UPDATE bank SET total_revenue = total_revenue - ?, tax_to_be_paid = tax_to_be_paid - ? WHERE id = 1";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, productAmount);
            pstmt.setDouble(2, productTax);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Processes a refund for a seat purchase by adjusting the bank's total revenue and tax to be paid.
     * 
     * This method calculates the tax for the seat (20%) based on the provided amount.
     * It then subtracts the seat amount and the calculated tax from the corresponding fields 
     * in the "bank" table for the record with ID = 1.
     * 
     * @param seatAmount The amount paid for the seat.
     * @return {@code true} if the refund was processed successfully, {@code false} otherwise.
     * @throws Exception If there is an error executing the update query.
     */
    public boolean processSeatRefund(double seatAmount) throws Exception {
        double ticketTax = seatAmount * 0.20; // 20% tax for tickets
        
        String sql = "UPDATE bank SET total_revenue = total_revenue - ?, tax_to_be_paid = tax_to_be_paid - ? WHERE id = 1";
        
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, seatAmount);
            pstmt.setDouble(2, ticketTax);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves the total revenue and tax to be paid from the "bank" table.
     * 
     * This method queries the "bank" table for the total revenue and tax to be paid for the record with ID = 1.
     * If the record is found, it returns an array containing the total revenue and tax to be paid.
     * If no record is found, it returns an array with both values set to 0.0.
     * 
     * @return A double array where the first value is total revenue and the second value is tax to be paid.
     * @throws Exception If there is an error executing the query.
     */
    public double[] getBankTotals() throws Exception {
        String query = "SELECT total_revenue, tax_to_be_paid FROM bank WHERE id = 1";
        try (Connection conn = DataBaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new double[] {
                    rs.getDouble("total_revenue"),
                    rs.getDouble("tax_to_be_paid")
                };
            }
            return new double[] {0.0, 0.0};
        }
    }
}