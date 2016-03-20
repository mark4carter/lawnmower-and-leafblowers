package io.mark4carter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
  
  @CrossOrigin
  @RequestMapping(value = "{newWeekDate}/listMonthlyInvoice")
  List<Invoice> listMonthlyInvoice(@PathVariable Long newWeekDate) {
    
    Long BeginningOfLastMonth = getBeginningOfLastMonth(newWeekDate);
    Long EndOfLastMonth = getEndOfLastMonth(newWeekDate);
    
    return invoiceRepository.findByDateOfServiceBetweenOrderByCustomerAsc(BeginningOfLastMonth, EndOfLastMonth);
  }
  
  @CrossOrigin
  @RequestMapping(value = "/resetAll")
  void resetAll() {
    invoiceRepository.deleteAll();
    customerRepository.deleteAll();
    technicianRepository.deleteAll();
    Technician CEO = new Technician("THE CEO!");
    technicianRepository.save(CEO);
  }
  
  @CrossOrigin
  @RequestMapping(value = "/monthWithPost", method = RequestMethod.POST)  
  List<Invoice> generateMonthlyWithPost(@RequestBody ManualReport reportRequest) {
    
    Long startMonth = this.getBeginningOfLastMonth
        (this.addMonths
            (this.getCalendarFromMonthAndYear
                (Integer.parseInt(reportRequest.getMonthOne()), 
                    Integer.parseInt(reportRequest.getYearOne())), 1));
    
    Long endMonth = this.getEndOfLastMonth
        (this.addMonths
            (this.getCalendarFromMonthAndYear
                (Integer.parseInt(reportRequest.getMonthTwo()), 
                    Integer.parseInt(reportRequest.getYearTwo())), 1));
    
    return invoiceRepository
        .findByDateOfServiceBetweenOrderByCustomerAsc(startMonth, endMonth);
  }
  
  
  /*
   * UTILITY METHODS (to be split later)
   */
  
  public Date createSignUpDate(Date date, int numberOfDays) {
    Date newDate = new Date(date.getTime());
    
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(newDate);
    calendar.add(Calendar.DATE, numberOfDays);
    newDate.setTime(calendar.getTime().getTime());
    
    return newDate;
  }
  
  public Long getCalendarFromMonthAndYear(int month, int year) {
    GregorianCalendar calendar = new GregorianCalendar(year, month, 1);
    return calendar.getTime().getTime();
  }
  
  public Long getBeginningOfLastMonth(Long newWeekDate) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(newWeekDate);
    calendar.add(Calendar.MONTH, -1);
    calendar.set(Calendar.DAY_OF_MONTH,
            calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime().getTime();
  }
  
  public Long getEndOfLastMonth(Long newWeekDate) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(newWeekDate);
    calendar.add(Calendar.MONTH, -1);
    calendar.set(Calendar.DAY_OF_MONTH,
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime().getTime();
    
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
  
  public Long addMonths(Long date, int numberOfMonths) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(date);
    calendar.add(Calendar.MONTH, numberOfMonths);
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
