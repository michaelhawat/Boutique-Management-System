package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.api.CustomerApi;
import com.example.demo.api.OrderApi;
import com.example.demo.api.OrderItemApi;
import com.example.demo.api.ProductApi;
import com.example.demo.model.CustomerDto;
import com.example.demo.model.OrderDto;
import com.example.demo.model.OrderItemDto;
import com.example.demo.model.ProductDto;
import com.example.demo.util.AlertUtils;
import com.example.demo.util.InvoiceGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class OrderStaffController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @FXML private TableView<OrderDto> ordersTable;
    @FXML private TableColumn<OrderDto, Long> orderIdColumn;
    @FXML private TableColumn<OrderDto, String> orderDescriptionColumn;
    @FXML private TableColumn<OrderDto, String> customerNameColumn;
    @FXML private TableColumn<OrderDto, String> statusColumn;
    @FXML private TableColumn<OrderDto, LocalDateTime> placedAtColumn;
    @FXML private TableColumn<OrderDto, String> totalColumn;

    @FXML private ComboBox<CustomerDto> customerCombo;
    @FXML private TextField descriptionField;
    @FXML private Button createOrderButton;
    @FXML private Button fulfillOrderButton;
    @FXML private Button deleteOrderButton;

    @FXML private TableView<OrderItemDto> orderItemsTable;
    @FXML private TableColumn<OrderItemDto, Long> productIdColumn;
    @FXML private TableColumn<OrderItemDto, String> productNameColumn;
    @FXML private TableColumn<OrderItemDto, BigDecimal> unitPriceColumn;
    @FXML private TableColumn<OrderItemDto, Integer> quantityColumn;
    @FXML private TableColumn<OrderItemDto, BigDecimal> subtotalColumn;

    @FXML private ComboBox<ProductDto> productCombo;
    @FXML private TextField quantityField;
    @FXML private Button addItemButton;
    @FXML private Button removeItemButton;

    private final ObservableList<OrderDto> orders = FXCollections.observableArrayList();
    private final ObservableList<CustomerDto> customers = FXCollections.observableArrayList();
    private final ObservableList<ProductDto> products = FXCollections.observableArrayList();
    private final ObservableList<OrderItemDto> orderItems = FXCollections.observableArrayList();

    private OrderDto selectedOrder;
    private OrderItemDto selectedOrderItem;

    @FXML
    public void initialize() {
        setupTables();
        setupComboBoxes();
        loadData();
        setupEventHandlers();
    }

    private void setupTables() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        placedAtColumn.setCellValueFactory(new PropertyValueFactory<>("placedAt"));
        placedAtColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DATE_FORMATTER.format(item));
                }
            }
        });
        totalColumn.setCellValueFactory(cellData -> {
            BigDecimal total = cellData.getValue().getTotal();
            return new SimpleStringProperty("$" + total.setScale(2, RoundingMode.HALF_UP));
        });
        ordersTable.setItems(orders);

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        unitPriceColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("$" + item.setScale(2, RoundingMode.HALF_UP));
                }
            }
        });
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        subtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("$" + item.setScale(2, RoundingMode.HALF_UP));
                }
            }
        });
        orderItemsTable.setItems(orderItems);

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, neu) -> {
            selectedOrder = neu;
            if (neu == null) {
                orderItems.clear();
            } else {
                loadOrderItems(neu.getOrderId());
            }
            updateActionButtons();
        });

        orderItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, neu) -> {
            selectedOrderItem = neu;
            removeItemButton.setDisable(neu == null);
        });
    }

    private void setupComboBoxes() {
        customerCombo.setItems(customers);
        customerCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CustomerDto item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName() + " (" + item.getContact() + ")");
            }
        });
        customerCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CustomerDto item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName() + " (" + item.getContact() + ")");
            }
        });

        productCombo.setItems(products);
        productCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ProductDto item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName() + " - $" + item.getPrice());
            }
        });
        productCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProductDto item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName() + " - $" + item.getPrice());
            }
        });
    }

    private void setupEventHandlers() {
        fulfillOrderButton.setDisable(true);
        deleteOrderButton.setDisable(true);
        removeItemButton.setDisable(true);
    }

    private void updateActionButtons() {
        if (selectedOrder == null) {
            fulfillOrderButton.setDisable(true);
            deleteOrderButton.setDisable(true);
            removeItemButton.setDisable(true);
            return;
        }
        fulfillOrderButton.setDisable(!isFulfillable(selectedOrder));
        deleteOrderButton.setDisable(!isCancelable(selectedOrder));
    }

    private boolean isFulfillable(OrderDto order) {
        if (order.getStatus() == null) {
            return false;
        }
        return switch (order.getStatus()) {
            case "PENDING", "PAID" -> true;
            default -> false;
        };
    }

    private boolean isCancelable(OrderDto order) {
        return "PENDING".equals(order.getStatus());
    }

    private void loadData() {
        try {
            List<OrderDto> orderList = OrderApi.getAllOrders();
            orderList.sort(Comparator.comparing(OrderDto::getPlacedAt, Comparator.nullsLast(Comparator.reverseOrder())));
            orders.setAll(orderList);
            selectedOrder = null;
            ordersTable.getSelectionModel().clearSelection();
            orderItems.clear();
            updateActionButtons();

            List<CustomerDto> customerList = CustomerApi.getAllCustomers();
            customers.setAll(customerList);

            List<ProductDto> productList = ProductApi.getAvailableProducts();
            products.setAll(productList);
        } catch (Exception e) {
            AlertUtils.error("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadOrderItems(Long orderId) {
        try {
            List<OrderItemDto> items = OrderItemApi.getOrderItems(orderId);
            orderItems.setAll(items);
        } catch (Exception e) {
            AlertUtils.error("Error loading order items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void createOrder() {
        try {
            CustomerDto customer = customerCombo.getSelectionModel().getSelectedItem();
            if (customer == null) {
                AlertUtils.warn("Please select a customer");
                return;
            }

            String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();

            OrderDto newOrder = new OrderDto(
                    null,
                    customer.getCustomerId(),
                    customer.getName(),
                    description.isEmpty() ? " " : description,
                    null,
                    "PENDING",
                    null,
                    BigDecimal.ZERO
            );

            OrderDto createdOrder = OrderApi.createOrder(newOrder);
            orders.add(0, createdOrder);

            customerCombo.getSelectionModel().clearSelection();
            descriptionField.clear();

            AlertUtils.info("Order created successfully");
        } catch (Exception e) {
            AlertUtils.error("Error creating order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void fulfillOrder() {
        if (selectedOrder == null) {
            AlertUtils.warn("Please select an order to fulfill");
            return;
        }
        if (!isFulfillable(selectedOrder)) {
            AlertUtils.warn("Only pending or paid orders can be fulfilled");
            return;
        }

        try {
            OrderDto updated = OrderApi.closeOrder(selectedOrder.getOrderId());
            selectedOrder.setStatus(updated.getStatus());
            selectedOrder.setPlacedAt(updated.getPlacedAt());
            selectedOrder.setTotal(updated.getTotal());
            ordersTable.refresh();
            AlertUtils.info("Order marked as fulfilled");
        } catch (Exception e) {
            AlertUtils.error("Error fulfilling order: " + e.getMessage());
            e.printStackTrace();
        }
        updateActionButtons();
    }

    @FXML
    public void deleteOrder() {
        if (selectedOrder == null) {
            AlertUtils.warn("Please select an order to delete");
            return;
        }
        if (!isCancelable(selectedOrder)) {
            AlertUtils.warn("Only pending orders can be deleted");
            return;
        }

        if (AlertUtils.confirm("Are you sure you want to delete this order?")) {
            try {
                OrderApi.deleteOrder(selectedOrder.getOrderId());
                orders.remove(selectedOrder);
                orderItems.clear();
                selectedOrder = null;
                AlertUtils.info("Order deleted successfully");
            } catch (Exception e) {
                AlertUtils.error("Error deleting order: " + e.getMessage());
                e.printStackTrace();
            }
        }
        updateActionButtons();
    }

    @FXML
    public void addOrderItem() {
        if (selectedOrder == null) {
            AlertUtils.warn("Please select an order first");
            return;
        }
        if (!"PENDING".equals(selectedOrder.getStatus())) {
            AlertUtils.warn("Items can only be added to pending orders");
            return;
        }

        try {
            ProductDto product = productCombo.getSelectionModel().getSelectedItem();
            String quantityText = quantityField.getText() == null ? "" : quantityField.getText().trim();

            if (product == null || quantityText.isEmpty()) {
                AlertUtils.warn("Please select a product and enter quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                AlertUtils.warn("Quantity must be greater than 0");
                return;
            }

            OrderItemDto request = new OrderItemDto(null,
                    selectedOrder.getOrderId(),
                    product.getProductId(),
                    product.getName(),
                    null,
                    quantity,
                    null);

            OrderItemDto createdItem = OrderItemApi.addOrderItem(selectedOrder.getOrderId(), request);
            orderItems.add(createdItem);
            refreshSelectedOrderSummary();

            productCombo.getSelectionModel().clearSelection();
            quantityField.clear();

            AlertUtils.info("Item added to order");
        } catch (NumberFormatException ex) {
            AlertUtils.warn("Please enter a valid quantity");
        } catch (Exception e) {
            AlertUtils.error("Error adding item to order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void removeOrderItem() {
        if (selectedOrder == null || selectedOrderItem == null) {
            AlertUtils.warn("Please select an item to remove");
            return;
        }
        if (!"PENDING".equals(selectedOrder.getStatus())) {
            AlertUtils.warn("Items can only be removed from pending orders");
            return;
        }

        try {
            OrderItemApi.removeOrderItem(selectedOrder.getOrderId(), selectedOrderItem.getId());
            orderItems.remove(selectedOrderItem);
            selectedOrderItem = null;
            refreshSelectedOrderSummary();
            AlertUtils.info("Item removed from order");
        } catch (Exception e) {
            AlertUtils.error("Error removing item from order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void addNewCustomer() {
        Dialog<CustomerDto> dialog = new Dialog<>();
        dialog.setTitle("Add New Customer");
        dialog.setHeaderText("Enter customer information");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField contactField = new TextField();
        contactField.setPromptText("Phone/Email");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Contact:"), 0, 1);
        grid.add(contactField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                if (name.isEmpty() || contact.isEmpty()) {
                    return null;
                }
                return new CustomerDto(null, name, contact);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(customerData -> {
            try {
                CustomerDto createdCustomer = CustomerApi.createCustomer(customerData);
                customers.add(createdCustomer);
                customerCombo.getSelectionModel().select(createdCustomer);
                AlertUtils.info("Customer added successfully");
            } catch (Exception e) {
                AlertUtils.error("Error adding customer: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void refreshOrders() {
        loadData();
    }

    @FXML
    public void goToDashboard() {
        Launcher.go("staff-dashboard.fxml", "Dashboard");
    }

    @FXML
    public void printInvoice() {
        if (selectedOrder == null) {
            AlertUtils.warn("Please select an order to print.");
            return;
        }
        try {
            List<OrderItemDto> items = OrderItemApi.getOrderItems(selectedOrder.getOrderId());
            InvoiceGenerator.generate(selectedOrder, items);
        } catch (Exception e) {
            AlertUtils.error("Error printing invoice: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshSelectedOrderSummary() {
        if (selectedOrder == null) {
            return;
        }
        try {
            OrderDto updated = OrderApi.getOrderById(selectedOrder.getOrderId());
            selectedOrder.setStatus(updated.getStatus());
            selectedOrder.setPlacedAt(updated.getPlacedAt());
            selectedOrder.setTotal(updated.getTotal());
            ordersTable.refresh();
        } catch (Exception e) {
            AlertUtils.error("Unable to refresh order summary: " + e.getMessage());
        }
    }
}
