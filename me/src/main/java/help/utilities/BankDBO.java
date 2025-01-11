package help.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BankDBO {
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