package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.api.CustomerApi;
import com.example.demo.api.OrderApi;
import com.example.demo.api.ProductApi;
import com.example.demo.model.CustomerDto;
import com.example.demo.model.OrderDto;
import com.example.demo.model.ProductDto;
import com.example.demo.util.AlertUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StaffDashboardController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private static final int LOW_STOCK_THRESHOLD = 3;

    @FXML private Label totalOrdersLabel;
    @FXML private Label pendingOrdersLabel;
    @FXML private Label fulfilledOrdersLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label lowStockLabel;


    @FXML private TableView<OrderDto> recentOrdersTable;
    @FXML private TableColumn<OrderDto, Long> recentOrderIdColumn;
    @FXML private TableColumn<OrderDto, String> recentCustomerColumn;
    @FXML private TableColumn<OrderDto, String> recentStatusColumn;
    @FXML private TableColumn<OrderDto, LocalDateTime> recentPlacedAtColumn;
    @FXML private TableColumn<OrderDto, String> recentTotalColumn;

    @FXML private TableView<ProductDto> lowStockTable;
    @FXML private TableColumn<ProductDto, String> lowStockNameColumn;
    @FXML private TableColumn<ProductDto, String> lowStockCategoryColumn;
    @FXML private TableColumn<ProductDto, Integer> lowStockQtyColumn;

    private final ObservableList<OrderDto> recentOrders = FXCollections.observableArrayList();
    private final ObservableList<CustomerDto> recentCustomers = FXCollections.observableArrayList();
    private final ObservableList<ProductDto> lowStockProducts = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        setupTables();
        refreshData();
    }

    @FXML
    public void refreshData() {
        try {
            loadData();
        } catch (Exception e) {
            AlertUtils.error("Failed to load dashboard. Please contact an administrator.\n\nDetails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTables() {
        recentOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        recentCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        recentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        recentPlacedAtColumn.setCellValueFactory(new PropertyValueFactory<>("placedAt"));
        recentPlacedAtColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : DATE_FORMATTER.format(item));
            }
        });
        recentTotalColumn.setCellValueFactory(cellData -> {
            BigDecimal total = cellData.getValue().getTotal();
            return new SimpleStringProperty("$" + total.setScale(2, RoundingMode.HALF_UP));
        });
        recentOrdersTable.setItems(recentOrders);

        lowStockNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lowStockCategoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().name().replace('_', ' ')));
        lowStockQtyColumn.setCellValueFactory(new PropertyValueFactory<>("stockQty"));
        lowStockTable.setItems(lowStockProducts);
    }

    private void loadData() {
        try {
            List<OrderDto> orders = OrderApi.getAllOrders();
            orders.sort(Comparator.comparing(OrderDto::getPlacedAt, Comparator.nullsLast(Comparator.reverseOrder())));
            recentOrders.setAll(orders.stream().limit(8).collect(Collectors.toList()));

            List<CustomerDto> customers = CustomerApi.getAllCustomers();
            recentCustomers.setAll(customers.stream().limit(8).collect(Collectors.toList()));
            List<ProductDto> products = ProductApi.getAllProducts();
            lowStockProducts.setAll(products.stream()
                    .filter(p -> p.getStockQty() != null && p.getStockQty() <= LOW_STOCK_THRESHOLD)
                    .sorted(Comparator.comparing(ProductDto::getStockQty))
                    .collect(Collectors.toList()));

            updateStatistics(orders, customers , products);
        } catch (Exception e) {
            AlertUtils.error("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics(List<OrderDto> orders, List<CustomerDto> customers ,List<ProductDto> products) {
        long pending = orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long fulfilled = orders.stream().filter(o -> "FULFILLED".equals(o.getStatus())).count();
        BigDecimal inventoryValue = products.stream()
                .map(p -> {
                    int stock = p.getStockQty() == null ? 0 : p.getStockQty();
                    return p.getPrice().multiply(BigDecimal.valueOf(stock));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long lowStock = products.stream()
                .filter(p -> p.getStockQty() != null && p.getStockQty() <= LOW_STOCK_THRESHOLD)
                .count();
        totalOrdersLabel.setText(String.valueOf(orders.size()));
        pendingOrdersLabel.setText(String.valueOf(pending));
        fulfilledOrdersLabel.setText(String.valueOf(fulfilled));
        totalCustomersLabel.setText(String.valueOf(customers.size()));

    }

    @FXML
    public void goToOrders() {
        Launcher.go("orderStaff.fxml", "Order Management");
    }

    @FXML
    public void goToCustomers() {
        Launcher.go("customers.fxml", "Customer Management");
    }

    @FXML
    public void logout() {
        Launcher.go("login.fxml", "Login");
    }
}

