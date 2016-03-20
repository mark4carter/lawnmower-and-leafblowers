package io.mark4carter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

  List<Customer> findByNextDayOfServiceBetween(Long start, Long end);
  
  @Query("SELECT u.technician, COUNT(u) FROM Customer u GROUP BY u.technician")
  List findCountPerDay();
  
  public void deleteAll();
}
