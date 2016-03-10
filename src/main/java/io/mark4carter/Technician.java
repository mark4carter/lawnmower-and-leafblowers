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
  
  private Long numberOfCustomers;  

  @OneToMany(mappedBy="technician")
  private Set<Customer> customer = new HashSet<>();
  
  Technician() {
    
  }
  
  Technician(String name) {
    this.name = name;
    this.numberOfCustomers = (long) 0;
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

  public Long getNumberOfCustomers() {
    return numberOfCustomers;
  }

  public void setNumberOfCustomers(Long numberOfCustomers) {
    this.numberOfCustomers = numberOfCustomers;
  }  
}
