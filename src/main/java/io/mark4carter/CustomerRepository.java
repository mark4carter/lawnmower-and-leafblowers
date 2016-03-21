package io.mark4carter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

  List<Customer> findByNextDayOfServiceBetween(Long start, Long end);
  
  @Query("SELECT u.technician.idTechnician, COUNT(u) FROM Customer u GROUP BY u.technician.idTechnician ORDER BY COUNT(u) ASC")
  List findCountPerDay();
  
  /*
  @Query("SELECT u.technician.idTechnician FROM Customer u INNER JOIN Technician v ON u.technician.idTechnician = v.idTechnician")
  List newFindCount();
  */
  
  //@Query("SELECT v, u FROM Customer u RIGHT JOIN u.technician v")
  @Query("SELECT v, COUNT(u) FROM Customer u RIGHT JOIN u.technician v GROUP BY u.technician.idTechnician ORDER BY COUNT(u) ASC")
  List newFindCount();
  
  public void deleteAll();
}
