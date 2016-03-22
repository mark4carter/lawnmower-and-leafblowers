package io.mark4carter;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Technician {
  
  @Id
  @GeneratedValue
  private Long idTechnician;
  
  private String name; 

  @OneToMany(mappedBy="technician")
  private Set<Customer> customers = new HashSet<Customer>();
  
  @OneToMany(mappedBy="technician")
  private Set<Invoice> invoices = new HashSet<Invoice>();
  
  Technician() {
    
  }
  
  Technician(String name) {
    this.name = name;
  }

  public Long getIdTechnician() {
    return idTechnician;
  }

  public void setIdTechnician(Long idCustomer) {
    this.idTechnician = idCustomer;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
