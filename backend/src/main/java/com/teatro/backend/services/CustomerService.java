package com.teatro.backend.services;

import com.teatro.backend.models.dtos.CustomerDTO;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerById(Long id);
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);
    void deleteCustomer(Long id);
    List<CustomerDTO> getCustomersWithFreePass();
    void incrementAttendance(Long customerId);
    void useFreePass(Long customerId);
}
