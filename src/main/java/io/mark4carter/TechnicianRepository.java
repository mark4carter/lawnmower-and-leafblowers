package io.mark4carter;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long>{
  
  //List<Technician> findAllByOrderByNumberOfCustomersAsc();
  
  Technician findByIdTechnician(Long techId);
  
  public void deleteAll();

}
