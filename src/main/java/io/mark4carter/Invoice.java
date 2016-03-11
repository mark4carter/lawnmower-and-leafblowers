package io.mark4carter;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Invoice {
  
  @Id
  @GeneratedValue
  private Long idInvoice;
  
  private Long dateOfService;
  
  @ManyToOne
  @JoinColumn(name = "idCustomer")
  private Customer customer;
  
  @ManyToOne
  @JoinColumn(name = "idTechnician")
  private Technician technician;
  
  Invoice () {
    
  }
  
  Invoice (Customer customer, Technician technician, Long dateOfService) {
    this.customer = customer;
    this.technician = technician;
    this.dateOfService = dateOfService;
  }

  public Long getIdInvoice() {
    return idInvoice;
  }

  public void setIdInvoice(Long idInvoice) {
    this.idInvoice = idInvoice;
  }

  public Long getDateOfService() {
    return dateOfService;
  }

  public void setDateOfService(Long dateOfService) {
    this.dateOfService = dateOfService;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Technician getTechnician() {
    return technician;
  }

  public void setTechnician(Technician technician) {
    this.technician = technician;
  }
  
  

}
