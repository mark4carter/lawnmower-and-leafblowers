package io.mark4carter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
  private final InvoiceRepository invoiceRepository;
  
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
  @RequestMapping(value="/listInvoices")
  List<Invoice> listAllInvoices() {
    return invoiceRepository.findAll();
  }
  
  @CrossOrigin
  @RequestMapping(value = "/{customerName}/addCustomer")
  List<Customer> addCustomerByName(@PathVariable String customerName) {
    List<Technician> techs = technicianRepository.findAllByOrderByNumberOfCustomersAsc();    
    Technician assignedTech = techs.get(0);
    assignedTech.setNumberOfCustomers(assignedTech.getNumberOfCustomers() + 1);
    technicianRepository.saveAndFlush(assignedTech);
    Customer newCustomer = new Customer(customerName, assignedTech, new Date());
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
  
  @CrossOrigin
  @RequestMapping(value = "/forceNextWeek")
  List<Invoice> forceNextWeek() {
    return invoiceRepository.findAll();
  }
  
  public Date createSignUpDate(Date date, int numberOfDays) {
    Date newDate = new Date(date.getTime());
    
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(newDate);
    calendar.add(Calendar.DATE, numberOfDays);
    newDate.setTime(calendar.getTime().getTime());
    
    return newDate;
  }
  
  @Autowired
  public LawnController( 
      CustomerRepository customerRepository,
      TechnicianRepository technicianRepository,
      InvoiceRepository invoiceRepository) {
   this.customerRepository = customerRepository;
   this.technicianRepository = technicianRepository;
   this.invoiceRepository = invoiceRepository;
  }
}
