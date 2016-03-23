package io.mark4carter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>{

  public List<Invoice> findByDateOfServiceBetween(Long start, Long end);
  
  public List<Invoice> findByDateOfServiceBetweenOrderByTechnicianAsc(Long start, Long end);
  
  public List<Invoice> findByDateOfServiceBetweenOrderByCustomerAsc(Long start, Long end);
  
  public List<Invoice> findByDateOfServiceBetweenAndTechnician(Long start, Long end, Technician tech);
  
  public List<Invoice> findByTechnician(Technician tech);
  
  public void deleteAll();
}
