package com.example.demo.controllers;

import com.example.demo.Launcher;
import com.example.demo.api.CustomerApi;
import com.example.demo.model.CustomerDto;
import com.example.demo.util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CustomerController {
    
    @FXML private TableView<CustomerDto> customersTable;
    @FXML private TableColumn<CustomerDto, Long> customerIdColumn;
    @FXML private TableColumn<CustomerDto, String> nameColumn;
    @FXML private TableColumn<CustomerDto, String> contactColumn;
    
    @FXML private TextField nameField;
    @FXML private TextField contactField;
    
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    
    private ObservableList<CustomerDto> customers = FXCollections.observableArrayList();
    private CustomerDto selectedCustomer;
    
    @FXML
    public void initialize() {
        setupTable();
        loadCustomers();
        setupEventHandlers();
    }
    
    private void setupTable() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        customersTable.setItems(customers);

        
        customersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedCustomer = newSelection;
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
    
    private void loadCustomers() {
        try {
            List<CustomerDto> customerList = CustomerApi.getAllCustomers();
            customers.clear();
            customers.addAll(customerList);
        } catch (Exception e) {
            AlertUtils.error("Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void populateFields(CustomerDto customer) {
        nameField.setText(customer.getName());
        contactField.setText(customer.getContact());
    }
    
    private void clearFields() {
        nameField.clear();
        contactField.clear();
    }
    
    @FXML
    public void addCustomer() {
        try {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            
            if (name.isEmpty() || contact.isEmpty()) {
                AlertUtils.warn("Please fill in all fields");
                return;
            }
            
            CustomerDto newCustomer = new CustomerDto(null, name, contact);
            CustomerDto createdCustomer = CustomerApi.createCustomer(newCustomer);
            
            customers.add(createdCustomer);
            clearFields();
            AlertUtils.info("Customer added successfully");
            
        } catch (Exception e) {
            AlertUtils.error("Error adding customer: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void updateCustomer() {
        if (selectedCustomer == null) {
            AlertUtils.warn("Please select a customer to update");
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            
            if (name.isEmpty() || contact.isEmpty()) {
                AlertUtils.warn("Please fill in all fields");
                return;
            }
            
            selectedCustomer.setName(name);
            selectedCustomer.setContact(contact);
            
            CustomerApi.updateCustomer(selectedCustomer.getCustomerId(), selectedCustomer);
            
            customersTable.refresh();
            clearFields();
            AlertUtils.info("Customer updated successfully");
            
        } catch (Exception e) {
            AlertUtils.error("Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void deleteCustomer() {
        if (selectedCustomer == null) {
            AlertUtils.warn("Please select a customer to delete");
            return;
        }
        
        if (AlertUtils.confirm("Are you sure you want to delete this customer?")) {
            try {
                CustomerApi.deleteCustomer(selectedCustomer.getCustomerId());
                customers.remove(selectedCustomer);
                clearFields();
                AlertUtils.info("Customer deleted successfully");
            } catch (Exception e) {
                AlertUtils.error("Error deleting customer: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    public void refreshCustomers() {
        loadCustomers();
    }
    
    @FXML
    public void goToDashboard() {
        Launcher.go("dashboard.fxml", "Dashboard");
    }
    
}
