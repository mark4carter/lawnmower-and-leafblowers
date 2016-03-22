package io.mark4carter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Customer {
  
  @Id
  @GeneratedValue
  private Long idCustomer;
  
  private String name;
  
  private String address;
  
  @ManyToOne
  @JoinColumn(name = "idTechnician")
  private Technician technician;
  
  @OneToMany(mappedBy = "customer")
  private Set<Invoice> invoice = new HashSet<Invoice>();
  
  private Long signUpDate;
  
  private Long nextDayOfService;
  
  Customer() {
    
  }
  
  Customer(String name, Technician technician, Long signUpDate) {
    this.name = name;
    this.technician = technician;
    this.signUpDate = signUpDate;
    this.nextDayOfService = createSignUpDate(signUpDate);
    
  }
  

  public Long getIdCustomer() {
    return idCustomer;
  }

  public void setIdCustomer(Long idCustomer) {
    this.idCustomer = idCustomer;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Technician getTechnician() {
    return technician;
  }

  public void setTechnician(Technician technician) {
    this.technician = technician;
  }

  public Long getSignUpDate() {
    return signUpDate;
  }

  public void setSignUpDate(Long signUpDate) {
    this.signUpDate = signUpDate;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Long getNextDayOfService() {
    return nextDayOfService;
  }

  public void setNextDayOfService(Long nextDayOfService) {
    this.nextDayOfService = nextDayOfService;
  }
  
  public Long createSignUpDate(Long date) {
    
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(date);
    calendar.add(Calendar.DATE, 14);
    
    if (serviceIsWithinServiceMonths(calendar.getTimeInMillis())) {
      return calendar.getTimeInMillis();
    } else {
      return startNewYear(calendar.getTimeInMillis());
    }
      
      
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
  

  
  public Long startNewYear(Long date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(date);
    calendar.add(Calendar.YEAR, 1);
    calendar.set(Calendar.MONTH, 2);
    calendar.set(Calendar.DATE, 1);
    System.out.println("New Year = " + calendar);
    return calendar.getTime().getTime();
  }
}
