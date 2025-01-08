package help;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.text.Normalizer;
import org.apache.pdfbox.pdmodel.font.PDType0Font;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import help.classes.ShoppingCart;
import help.classes.Tickets;
import help.utilities.DataBaseHandler;
import help.utilities.InvoiceDBO;
import help.utilities.ProductDBO;
import help.utilities.TicketsDBO;
import help.classes.Product;
import help.classes.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Step5Controller {

    @FXML private Button next_button_step5;
    @FXML private Button back_button_step5;
    @FXML private Button SaveTicketInvoice;
    
    // Movie Ticket Labels
    @FXML private Label MovieName;
    @FXML private Label SessionName;
    @FXML private Label HallName;
    @FXML private Label SeatsNumber;
    @FXML private Label CustomerName;

    @FXML private Label DiscNum;
    @FXML private Label NonDiscNum;

    @FXML private Label MovieInvolveProducts;
    @FXML private Label MovieInvolveSeatPrice;
    
    @FXML private Label MovieInvolveTotalPrice;
    @FXML private Label MovieInvolveTotalTax;

    


    
    // Side Panel Labels
    @FXML private Label selectedMovieLabel;
    @FXML private Label selectedSession;
    @FXML private Label selectedSeat;
    @FXML private Label selectedShoppingCart;
    @FXML private Label TotalPrice;
    @FXML private Label TotalTax;
    @FXML private Label SeatPrices;

    @FXML
    private void initialize() throws Exception {
        // Get existing instances
        ShoppingCart cart = ShoppingCart.getInstance();
        Tickets ticket = Tickets.getInstance();
        Session session = cart.getSelectedDaySessionAndHall();

        // Debug prints
        System.out.println("Step5 - Initialize:");
        System.out.println("Customer Name: " + ticket.getCustomerName());
        System.out.println("Seat Numbers: " + ticket.getSeatNumbers());

        // Set Movie Ticket Details
        MovieName.setText(cart.getSelectedMovie().getTitle());
        SessionName.setText(session.getSession());
        HallName.setText(session.getHall());
        SeatsNumber.setText(String.join(", ", ticket.getSeatNumbers()));
        CustomerName.setText(ticket.getCustomerName());
        DiscNum.setText("Discounted Seats = " + ticket.getDiscountedSeatNumber());
        NonDiscNum.setText("Non-Discounted Seats = " + (ticket.getSeatNumbers().size() - ticket.getDiscountedSeatNumber()));

        StringBuilder cartItems = new StringBuilder();
        for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) 
        {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            cartItems.append(product.getName())
                    .append(" - $")
                    .append(String.format("%.2f", product.getPrice()))
                    .append(" x ")
                    .append(quantity)
                    .append("\n");
        }
        selectedShoppingCart.setText(cartItems.length() > 0 ? cartItems.toString() : "No products in cart.");



        // Set Side Panel Details
        selectedMovieLabel.setText(cart.getSelectedMovie().getTitle());
        selectedSession.setText(session.getSession() + "\n" + session.getDay() + "\n" + session.getHall());
        selectedSeat.setText(String.join(", ", ticket.getSeatNumbers()));
        
        // Movie Involve
        
        SeatPrices.setText("Seat Prices:" + "$ " + String.valueOf(ticket.getTotalSeatCost()));

        MovieInvolveTotalPrice.setText("$" + String.format("%.2f", ticket.getTotalCost()));
        MovieInvolveTotalTax.setText("$" + String.format("%.2f", ticket.getTotalTax()));


    }

    @FXML
    private void handleNextButtonAction() throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/final.fxml"));
        Stage stage = (Stage) next_button_step5.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) throws Exception
    {
        
            // Get instances
            ShoppingCart cart = ShoppingCart.getInstance();
            Tickets ticket = Tickets.getInstance();
            ProductDBO productDBO = new ProductDBO();
            TicketsDBO ticketsDBO = new TicketsDBO();

            // Return products to stock
            for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) 
            {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();
                productDBO.returnProductStock(product.getName(), quantity);
            }

            // Delete ticket from database
            ticketsDBO.deleteTicket(ticket.getTicketId());

            // Clear shopping cart and ticket
            cart.clearItemsBought();
            ticket.clear();

            // Navigate back
            Parent step4Root = FXMLLoader.load(getClass().getResource("/help/fxml/step4.fxml"));
            Scene scene = back_button_step5.getScene();
            scene.setRoot(step4Root);
            Stage stage = (Stage) back_button_step5.getScene().getWindow();
            stage.setTitle("Step 4");

        
    }



        

    private void savePDFToDatabase(String filepath, int ticketId, String format) throws Exception 
    {
        File file = new File(filepath);
        if (!file.exists() || !file.canRead()) {
            throw new IOException("Cannot read PDF file: " + filepath);
        }
    
        byte[] pdfData = Files.readAllBytes(file.toPath());
        if (pdfData == null || pdfData.length == 0) {
            throw new IOException("PDF data is empty");
        }
    
        System.out.println("Uploading PDF: " + filepath);
        System.out.println("PDF size: " + pdfData.length + " bytes");
    
        InvoiceDBO invoiceDBO = new InvoiceDBO();
        invoiceDBO.saveInvoice(ticketId, pdfData, format);
    }





        @FXML
        private void handleSaveTicketInvoice(ActionEvent event) 
        {
            try {
                // Get user's Documents folder path
                String documentsPath = System.getProperty("user.home") + "\\Documents\\MovieTickets\\";
                File directory = new File(documentsPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
        
                ShoppingCart cart = ShoppingCart.getInstance();
                Tickets ticket = Tickets.getInstance();
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                
                // Create full file paths
                String ticketPath = documentsPath + "ticket_" + ticket.getTicketId() + "_" + timestamp + ".pdf";
                String invoicePath = documentsPath + "invoice_" + ticket.getTicketId() + "_" + timestamp + ".pdf";
                
                // Create PDFs
                createTicketPDF(ticketPath, ticket, cart);
                createInvoicePDF(invoicePath, ticket, cart);
        
                // Debug print
                System.out.println("PDFs created at: " + ticketPath);
                System.out.println("Attempting database upload...");
        
                // Save to database with verification
                savePDFToDatabase(ticketPath, ticket.getTicketId(), "PDF");
                savePDFToDatabase(invoicePath, ticket.getTicketId(), "PDF");
        
                showAlert("Success", "PDFs Created and Saved", 
                 "Files saved to:\n" + documentsPath + "\nand uploaded to database.",
                 Alert.AlertType.INFORMATION);

                 Parent root = FXMLLoader.load(getClass().getResource("/help/fxml/final.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.show();
                        
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
                showAlert("Error", null, "Error: " + e.getMessage(), 
                 Alert.AlertType.ERROR);
            }
        }

    private void createTicketPDF(String filepath, Tickets ticket, ShoppingCart cart) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            // Load Arial font which supports Turkish characters
            PDType0Font font;
            try {
                font = PDType0Font.load(document, new File("C:/Windows/Fonts/arial.ttf"));
            } catch (IOException e) {
                // Fallback to built-in font if Arial not found
                font = PDType0Font.load(document, new File("C:/Windows/Fonts/segoeui.ttf"));
            }
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Title
                contentStream.beginText();
                contentStream.setFont(font, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Movie Ticket");
                contentStream.endText();
                
                // Ticket Details with proper encoding
                float yPosition = 700;
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Ticket ID: " + ticket.getTicketId());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Customer: " + ticket.getCustomerName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Movie: " + cart.getSelectedMovie().getTitle());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Seats: " + String.join(", ", ticket.getSeatNumbers()));
                contentStream.endText();
            }
            
            document.save(filepath);
        } catch (IOException e) {
            throw new IOException("Failed to create PDF: " + e.getMessage(), e);
        }
    }

    private void createInvoicePDF(String filepath, Tickets ticket, ShoppingCart cart) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Invoice");
                contentStream.endText();
                
                // Invoice Details
                float yPosition = 700;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Invoice for Ticket ID: " + ticket.getTicketId());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Products:");
                contentStream.endText();
                
                // Product List
                yPosition -= 40;
                for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText(entry.getKey().getName() + " x " + entry.getValue() + 
                                        " = $" + String.format("%.2f", entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue()))));
                    contentStream.endText();
                    yPosition -= 20;
                }
                
                // Totals
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, yPosition - 20);
                contentStream.showText("Total: $" + String.format("%.2f", ticket.getTotalCost()));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Tax: $" + String.format("%.2f", ticket.getTotalTax()));
                contentStream.endText();
            }
            
            document.save(filepath);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) 
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Get current stage
        Stage stage = (Stage) MovieName.getScene().getWindow();
        
        // Make alert non-modal and set owner
        alert.initOwner(stage);
        alert.initModality(Modality.NONE);
        
        // Position alert in center of screen
        alert.setX(stage.getX() + stage.getWidth()/2 - alert.getWidth()/2);
        alert.setY(stage.getY() + stage.getHeight()/2 - alert.getHeight()/2);
        
        alert.show();
    }
}