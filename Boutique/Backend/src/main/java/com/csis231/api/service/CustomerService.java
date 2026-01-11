package com.csis231.api.service;

import com.csis231.api.dto.CustomerDto;
import com.csis231.api.entity.Customer;
import com.csis231.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return convertToDto(customer);
    }
    
    public CustomerDto createCustomer(CustomerDto customerDto) {
        if (customerRepository.findByContact(customerDto.getContact()).isPresent()) {
            throw new RuntimeException("Customer with contact " + customerDto.getContact() + " already exists");
        }
        
        Customer customer = convertToEntity(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDto(savedCustomer);
    }
    
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        // Check if contact is being changed and if new contact already exists
        if (!customer.getContact().equals(customerDto.getContact()) && 
            customerRepository.findByContact(customerDto.getContact()).isPresent()) {
            throw new RuntimeException("Customer with contact " + customerDto.getContact() + " already exists");
        }
        
        customer.setName(customerDto.getName());
        customer.setContact(customerDto.getContact());
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDto(savedCustomer);
    }
    
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    private CustomerDto convertToDto(Customer customer) {
        return CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .contact(customer.getContact())
                .build();
    }
    
    private Customer convertToEntity(CustomerDto customerDto) {
        return Customer.builder()
                .name(customerDto.getName())
                .contact(customerDto.getContact())
                .build();
    }
}
