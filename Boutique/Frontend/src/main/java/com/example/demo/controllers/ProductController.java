package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.api.ProductApi;
import com.example.demo.model.ProductCategory;
import com.example.demo.model.ProductDto;
import com.example.demo.util.AlertUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;

public class ProductController {
    
    @FXML private TableView<ProductDto> productsTable;
    @FXML private TableColumn<ProductDto, Long> productIdColumn;
    @FXML private TableColumn<ProductDto, String> nameColumn;
    @FXML private ComboBox<ProductCategory> categoryCombo;
    @FXML private TableColumn<ProductDto, String> categoryColumn;
    @FXML private TableColumn<ProductDto, BigDecimal> priceColumn;
    @FXML private TableColumn<ProductDto, Integer> stockColumn;
    
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    
    private ObservableList<ProductDto> products = FXCollections.observableArrayList();
    private ProductDto selectedProduct;
    
    @FXML
    public void initialize() {
        setupTable();
        loadProducts();
        setupEventHandlers();
        categoryCombo.setItems(FXCollections.observableArrayList(ProductCategory.values()));

    }
    
    private void setupTable() {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().name())
        );        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQty"));
        productsTable.setItems(products);
        
        // Selection listener
        productsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedProduct = newSelection;
                    if (newSelection != null) {
                        populateFields(newSelection);
                        updateButton.setDisable(false);
                        deleteButton.setDisable(false);
                    } else {
                        clearFields();
                        updateButton.setDisable(true);
                        deleteButton.setDisable(true);
                    }
                });
    }
    
    private void setupEventHandlers() {
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }
    
    private void loadProducts() {
        try {
            List<ProductDto> productList = ProductApi.getAllProducts();
            products.clear();
            products.addAll(productList);
        } catch (Exception e) {
            AlertUtils.error("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void populateFields(ProductDto product) {
        nameField.setText(product.getName());
        categoryCombo.setValue(product.getCategory());
        priceField.setText(product.getPrice().toString());
        stockField.setText(product.getStockQty().toString());
    }
    
    private void clearFields() {
        nameField.clear();
        categoryCombo.setValue(null);
        priceField.clear();
        stockField.clear();
    }
    
    @FXML
    public void addProduct() {
        try {
            String name = nameField.getText().trim();
            ProductCategory category = categoryCombo.getValue();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();
            
            if (name.isEmpty() || category == null || priceText.isEmpty() || stockText.isEmpty()) {
                AlertUtils.warn("Please fill in all fields");
                return;
            }
            
            BigDecimal price = new BigDecimal(priceText);
            Integer stock = Integer.parseInt(stockText);
            
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtils.warn("Price must be greater than 0");
                return;
            }
            
            if (stock < 0) {
                AlertUtils.warn("Stock quantity must be non-negative");
                return;
            }
            
            ProductDto newProduct = new ProductDto(null, name, category, price, stock);
            ProductDto createdProduct = ProductApi.createProduct(newProduct);
            
            products.add(createdProduct);
            clearFields();
            AlertUtils.info("Product added successfully");
            
        } catch (NumberFormatException e) {
            AlertUtils.warn("Please enter valid numbers for price and stock");
        } catch (Exception e) {
            AlertUtils.error("Error adding product: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void updateProduct() {
        if (selectedProduct == null) {
            AlertUtils.warn("Please select a product to update");
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            ProductCategory category = categoryCombo.getValue();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();
            
            if (name.isEmpty() || category == null || priceText.isEmpty() || stockText.isEmpty()) {
                AlertUtils.warn("Please fill in all fields");
                return;
            }
            
            BigDecimal price = new BigDecimal(priceText);
            Integer stock = Integer.parseInt(stockText);
            
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                AlertUtils.warn("Price must be greater than 0");
                return;
            }
            
            if (stock < 0) {
                AlertUtils.warn("Stock quantity must be non-negative");
                return;
            }
            
            selectedProduct.setName(name);
            selectedProduct.setCategory(category);
            selectedProduct.setPrice(price);
            selectedProduct.setStockQty(stock);
            
            ProductApi.updateProduct(selectedProduct.getProductId(), selectedProduct);
            
            productsTable.refresh();
            clearFields();
            AlertUtils.info("Product updated successfully");
            
        } catch (NumberFormatException e) {
            AlertUtils.warn("Please enter valid numbers for price and stock");
        } catch (Exception e) {
            AlertUtils.error("Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void deleteProduct() {
        if (selectedProduct == null) {
            AlertUtils.warn("Please select a product to delete");
            return;
        }
        
        if (AlertUtils.confirm("Are you sure you want to delete this product?")) {
            try {
                ProductApi.deleteProduct(selectedProduct.getProductId());
                products.remove(selectedProduct);
                clearFields();
                AlertUtils.info("Product deleted successfully");
            } catch (Exception e) {
                AlertUtils.error("Error deleting product: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void refreshProducts() {
        loadProducts();
    }
    
    @FXML
    public void goToDashboard() {
        Launcher.go("dashboard.fxml", "Dashboard");
    }
}
