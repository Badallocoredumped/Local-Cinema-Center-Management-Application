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
import help.utilities.BankDBO;
import help.utilities.DataBaseHandler;
import help.utilities.InvoiceDBO;
import help.utilities.ProductDBO;
import help.utilities.TicketProductsDBO;
import help.utilities.TicketsDBO;
import help.classes.Product;
import help.classes.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Step5Controller {

    @FXML private Button next_button_step5;
    @FXML private Button back_button_step5;
    @FXML private Button SaveTicketInvoice;

    @FXML private Button SignoutButton;
    @FXML private Button CloseButton;
    @FXML private Button MinimizeButton; 
     
    
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
    private void handleCloseButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) CloseButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimizeButtonAction(ActionEvent event) 
    {
        Stage stage = (Stage) MinimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleSignOutButtonAction(ActionEvent event) {
        try {
            // Get instances
            ShoppingCart cart = ShoppingCart.getInstance();
            Tickets ticket = Tickets.getInstance();
            
            // Return products to inventory
            ProductDBO productDBO = new ProductDBO();
            for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                productDBO.returnProductStock(product.getName(), quantity);
            }
            
            // Delete ticket if it exists
            if (ticket != null && ticket.getTicketId() > 0) {
                TicketsDBO ticketsDBO = new TicketsDBO();
                ticketsDBO.deleteTicket(ticket.getTicketId());
            }
            
            // Reset instances
            cart.clear();
            Tickets.resetInstance();
            
            // Navigate to login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/help/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setFullScreen(false);
            stage.show();
            
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Sign Out Failed");
            alert.setHeaderText("Error During Sign Out");
            alert.setContentText("Failed to properly sign out: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

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
            TicketProductsDBO ticketProductsDBO = new TicketProductsDBO();


            // Return products to stock
            for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) 
            {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();
                productDBO.returnProductStock(product.getName(), quantity);
            }

            ticketProductsDBO.deleteTicketProducts(ticket.getTicketId());

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
            try 
            {
                Tickets ticket = Tickets.getInstance();

                BankDBO bankDBO = new BankDBO();
                bankDBO.updateBankTotals(ticket.getTotalCost(), ticket.getTotalTax());
                bankDBO.updateBankTotalsFromTickets();
                // Get user's Documents folder path
                String documentsPath = System.getProperty("user.home") + "\\Documents\\MovieTickets\\";
                File directory = new File(documentsPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
        
                ShoppingCart cart = ShoppingCart.getInstance();
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
            
            // Try multiple font paths for better compatibility
            // Try multiple font paths for better compatibility
                PDType0Font font = null;
                String[] fontPaths = {
                    "C:/Windows/Fonts/ARIALUNI.TTF",  // Arial Unicode
                    "C:/Windows/Fonts/arial.ttf",      // Regular Arial
                    "C:/Windows/Fonts/segoeui.ttf"     // Segoe UI
                };

                for (String path : fontPaths) {
                    try {
                        font = PDType0Font.load(document, new File(path));
                        break;
                    } catch (IOException e) {
                        continue;
                    }
                }

                if (font == null) {
                    // Fallback to default embedded font
                    font = PDType0Font.load(document, getClass().getResourceAsStream("/fonts/DejaVuSans.ttf"));
                }

            
            // Continue with PDF creation using the loaded font
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(font, 12);
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
    
            PDType0Font font = PDType0Font.load(document, new File("C:/Windows/Fonts/arial.ttf"));
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float yPosition = yStart;
                
                // Header
                contentStream.beginText();
                contentStream.setFont(font, 16);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("INVOICE");
                contentStream.endText();
                
                yPosition -= 30;
                
                // Customer Details
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Customer: " + ticket.getCustomerName());
                contentStream.endText();
                
                yPosition -= 20;
                
                // Products Section
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Products Purchased:");
                contentStream.endText();
                
                yPosition -= 20;
                
                // Product List
                for (Map.Entry<Product, Integer> entry : cart.getItemsBought().entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();
                    double totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity)).doubleValue();
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(String.format("%s - $%.2f x %d = $%.2f", 
                        product.getName(), product.getPrice(), quantity, totalPrice));
                    contentStream.endText();
                    
                    yPosition -= 15;
                }
                
                yPosition -= 20;
                
                // Totals
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.format("Subtotal: $%.2f", ticket.getTotalCost()));
                contentStream.endText();
                
                yPosition -= 15;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.format("Tax: $%.2f", ticket.getTotalTax()));
                contentStream.endText();
                
                yPosition -= 15;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.format("Total: $%.2f", (ticket.getTotalCost() + ticket.getTotalTax())));
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