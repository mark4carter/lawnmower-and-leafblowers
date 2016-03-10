package io.mark4carter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/controller/")
public class LawnController {
  
  private final CustomerRepository customerRepository;
  private final TechnicianRepository technicianRepository;
  
  @CrossOrigin
  @RequestMapping(value = "/listCustomers") 
  List<Customer> listAllCustomers(){
    return customerRepository.findAll();
  }
  
  @CrossOrigin
  @RequestMapping(value="/listTechnicians")
  List<Technician> listAllTechnicians(){
    return technicianRepository.findAll();
  }
  
  @CrossOrigin
  @RequestMapping(value = "/{customerName}/addCustomer")
  List<Customer> addCustomerByName(@PathVariable String customerName) {
    List<Technician> techs = technicianRepository.findAllByOrderByNumberOfCustomersAsc();    
    Technician assignedTech = techs.get(0);
    assignedTech.setNumberOfCustomers(assignedTech.getNumberOfCustomers() + 1);
    technicianRepository.saveAndFlush(assignedTech);
    Customer newCustomer = new Customer(customerName, assignedTech);
    customerRepository.save(newCustomer);
    
    return customerRepository.findAll();    
  }
  
  @CrossOrigin
  @RequestMapping(value = "/{technicianName}/addTechnician") 
  List<Technician> addTechnicianByName(@PathVariable String technicianName) {
    Technician tech = new Technician(technicianName);
    technicianRepository.save(tech);
    return technicianRepository.findAll();
  }
  
  @Autowired
  public LawnController( 
      CustomerRepository customerRepository,
      TechnicianRepository technicianRepository) {
   this.customerRepository = customerRepository;
   this.technicianRepository = technicianRepository;    
  }
}
