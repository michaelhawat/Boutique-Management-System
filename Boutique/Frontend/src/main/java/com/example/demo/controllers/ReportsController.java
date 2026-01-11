package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.api.OrderApi;
import com.example.demo.api.ProductApi;
import com.example.demo.model.OrderDto;
import com.example.demo.model.OrderItemDto;
import com.example.demo.model.ProductDto;
import com.example.demo.util.AlertUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button generateReportButton;

    @FXML private Label totalOrdersLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalItemsSoldLabel;

    @FXML private TableView<OrderDto> ordersTable;
    @FXML private TableColumn<OrderDto, Long> orderIdColumn;
    @FXML private TableColumn<OrderDto, String> customerNameColumn;
    @FXML private TableColumn<OrderDto, String> statusColumn;
    @FXML private TableColumn<OrderDto, LocalDateTime> placedAtColumn;
    @FXML private TableColumn<OrderDto, String> totalColumn;

    @FXML private TableView<ProductReportDto> productsTable;
    @FXML private TableColumn<ProductReportDto, String> productNameColumn;
    @FXML private TableColumn<ProductReportDto, String> categoryColumn;
    @FXML private TableColumn<ProductReportDto, Integer> quantitySoldColumn;
    @FXML private TableColumn<ProductReportDto, String> revenueColumn;

    private final ObservableList<OrderDto> orders = FXCollections.observableArrayList();
    private final ObservableList<ProductReportDto> productReports = FXCollections.observableArrayList();

    public static class ProductReportDto {
        private String productName;
        private String category;
        private Integer quantitySold;
        private BigDecimal revenue;

        public ProductReportDto(String productName, String category, Integer quantitySold, BigDecimal revenue) {
            this.productName = productName;
            this.category = category;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
        }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Integer getQuantitySold() { return quantitySold; }
        public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
        
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }

    @FXML
    public void initialize() {
        setupTables();
        // Set default dates (last 30 days)
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        // Initialize labels
        totalOrdersLabel.setText("0");
        totalRevenueLabel.setText("$0.00");
        totalItemsSoldLabel.setText("0");
    }

    private void setupTables() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        placedAtColumn.setCellValueFactory(new PropertyValueFactory<>("placedAt"));
        placedAtColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : DATE_FORMATTER.format(item));
            }
        });
        totalColumn.setCellValueFactory(cellData -> {
            BigDecimal total = cellData.getValue().getTotal();
            return new SimpleStringProperty("$" + total.setScale(2, RoundingMode.HALF_UP));
        });
        ordersTable.setItems(orders);

        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantitySoldColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        revenueColumn.setCellValueFactory(cellData -> {
            BigDecimal revenue = cellData.getValue().getRevenue();
            return new SimpleStringProperty("$" + revenue.setScale(2, RoundingMode.HALF_UP));
        });
        productsTable.setItems(productReports);
    }

    @FXML
    public void generateReport() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate == null || endDate == null) {
                AlertUtils.warn("Please select both start and end dates");
                return;
            }

            if (startDate.isAfter(endDate)) {
                AlertUtils.warn("Start date must be before or equal to end date");
                return;
            }

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            List<OrderDto> filteredOrders = OrderApi.getOrdersByDateRange(startDateTime, endDateTime);
            orders.setAll(filteredOrders);

            // Fetch products to get categories
            List<ProductDto> allProducts = ProductApi.getAllProducts();
            Map<Long, String> productCategoryMap = new HashMap<>();
            for (ProductDto product : allProducts) {
                productCategoryMap.put(product.getProductId(), product.getCategory().name().replace('_', ' '));
            }

            // Calculate product sales
            Map<String, ProductReportDto> productMap = new HashMap<>();
            int totalItemsSold = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (OrderDto order : filteredOrders) {
                if (order.getOrderItems() != null) {
                    for (OrderItemDto item : order.getOrderItems()) {
                        String key = item.getProductName();
                        String category = productCategoryMap.getOrDefault(item.getProductId(), "Unknown");
                        ProductReportDto report = productMap.getOrDefault(key,
                                new ProductReportDto(item.getProductName(), category, 0, BigDecimal.ZERO));
                        report.quantitySold += item.getQuantity();
                        report.revenue = report.revenue.add(item.getSubtotal());
                        productMap.put(key, report);
                        totalItemsSold += item.getQuantity();
                    }
                }
                totalRevenue = totalRevenue.add(order.getTotal());
            }

            productReports.setAll(productMap.values().stream()
                    .sorted(Comparator.comparing(ProductReportDto::getRevenue).reversed())
                    .collect(Collectors.toList()));

            // Update statistics
            totalOrdersLabel.setText(String.valueOf(filteredOrders.size()));
            totalRevenueLabel.setText("$" + totalRevenue.setScale(2, RoundingMode.HALF_UP));
            totalItemsSoldLabel.setText(String.valueOf(totalItemsSold));

        } catch (Exception e) {
            AlertUtils.error("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goToDashboard() {
        Launcher.go("dashboard.fxml", "Dashboard");
    }
}

