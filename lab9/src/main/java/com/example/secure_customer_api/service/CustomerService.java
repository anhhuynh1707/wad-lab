package com.example.secure_customer_api.service;

import com.example.secure_customer_api.dto.CustomerRequestDTO;
import com.example.secure_customer_api.dto.CustomerResponseDTO;
import com.example.secure_customer_api.dto.CustomerUpdateDTO;

import org.springframework.data.domain.Page;


import java.util.List;

public interface CustomerService {
    
    List<CustomerResponseDTO> getAllCustomers();
    
    CustomerResponseDTO getCustomerById(Long id);
    
    CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO);
    
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO);
    
    void deleteCustomer(Long id);
    
    List<CustomerResponseDTO> searchCustomers(String keyword);
    
    List<CustomerResponseDTO> getCustomersByStatus(String status);

    List<CustomerResponseDTO> advancedSearch(String name, String email, String status);

    Page<CustomerResponseDTO> getAllCustomers(int page, int size);

    Page<CustomerResponseDTO> getAllCustomers(int page, int size, String sortBy, String sortDir);

    CustomerResponseDTO partialUpdateCustomer(Long id, CustomerUpdateDTO updateDTO);

}