package io.mark4carter;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Customer {
  
  @Id
  @GeneratedValue
  private Long idCustomer;
  
  private String name;
  
  @ManyToOne
  @JoinColumn(name = "idTechnician")
  private Technician technician;
  
  private Date signUpDate;
  
  Customer() {
    
  }
  
  Customer(String name, Technician technician) {
    this.name = name;
    this.technician = technician;
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

  public Date getSignUpDate() {
    return signUpDate;
  }

  public void setSignUpDate(Date signUpDate) {
    this.signUpDate = signUpDate;
  }
  
  
  

}
