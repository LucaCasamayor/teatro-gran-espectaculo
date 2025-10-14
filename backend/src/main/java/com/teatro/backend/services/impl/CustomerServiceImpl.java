package com.teatro.backend.services.impl;

import com.teatro.backend.exceptions.ResourceNotFoundException;
import com.teatro.backend.models.dtos.CustomerDTO;
import com.teatro.backend.models.entities.Customer;
import com.teatro.backend.repositories.CustomerRepository;
import com.teatro.backend.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findByActiveTrue().stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found or inactive with id: " + id));
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer c = modelMapper.map(dto, Customer.class);
        c.setLoyaltyFree(false);
        c.setActive(true);
        Customer saved = customerRepository.save(c);
        return modelMapper.map(saved, CustomerDTO.class);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found or inactive with id: " + id));

        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());

        Customer updatedCustomer = customerRepository.save(customer);
        return modelMapper.map(updatedCustomer, CustomerDTO.class);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found or already inactive with id: " + id));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    @Override
    public List<CustomerDTO> getCustomersWithFreePass() {
        return customerRepository.findByLoyaltyFreeTrue().stream()
                .filter(Customer::getActive)
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

}
