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
  @RequestMapping(value = "/{customerName}/{generatedDate}/addCustomer")
  List<Customer> addCustomerByName(@PathVariable String customerName, @PathVariable Long generatedDate) {
    List<Technician> techs = technicianRepository.findAllByOrderByNumberOfCustomersAsc();    
    Technician assignedTech = techs.get(0);
    assignedTech.setNumberOfCustomers(assignedTech.getNumberOfCustomers() + 1);
    technicianRepository.saveAndFlush(assignedTech);
    Customer newCustomer = new Customer(customerName, assignedTech, generatedDate);
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
  @RequestMapping(value = "{newWeekDate}/forceNextWeek")
  List<Invoice> forceNextWeek(@PathVariable Long newWeekDate) {
    newWeekDate = removeHourAndMinutes(newWeekDate);
    Long lastWeekDate = addDays(newWeekDate, -7);
    List<Customer> customerList = customerRepository.findByNextDayOfServiceBetween(lastWeekDate, newWeekDate);
    
    for (Customer customer : customerList) {
      Invoice newInvoice = new Invoice(customer, customer.getTechnician(), customer.getNextDayOfService());
      invoiceRepository.save(newInvoice);
      
      Long nextDayOfService = addDays(customer.getNextDayOfService(), 14);
      if(serviceIsWithinServiceMonths(nextDayOfService)) {
        customer.setNextDayOfService(nextDayOfService);        
      } else {
        customer.setNextDayOfService(startNewYear(nextDayOfService));
      }
      
      customerRepository.saveAndFlush(customer);
    }
    
    return invoiceRepository.findByDateOfServiceBetweenOrderByTechnicianAsc(lastWeekDate, newWeekDate);
  }
  
  public Date createSignUpDate(Date date, int numberOfDays) {
    Date newDate = new Date(date.getTime());
    
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(newDate);
    calendar.add(Calendar.DATE, numberOfDays);
    newDate.setTime(calendar.getTime().getTime());
    
    return newDate;
  }
  
  public Boolean serviceIsWithinServiceMonths(Long nextDayOfService) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(nextDayOfService);
    int serviceMonth = calendar.get(Calendar.MONTH);
    if (serviceMonth >= 2 && serviceMonth <= 9 )  {
      return true;
    }
    return false;
    
  }
  
  public Long addDays(Long date, int numberOfDays) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(date);
    calendar.add(Calendar.DATE, numberOfDays);
    return calendar.getTime().getTime();
  }
  
  public Long startNewYear(Long date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(date);
    calendar.add(Calendar.YEAR, 1);
    calendar.set(Calendar.MONTH, 2);
    calendar.set(Calendar.DATE, 1);
    System.out.println("New Year = " + calendar);
    return calendar.getTime().getTime();
  }
  
  public Long removeHourAndMinutes(Long date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(date);
    GregorianCalendar newCalendar = new GregorianCalendar(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DATE));
    return newCalendar.getTime().getTime();
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
