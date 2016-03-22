package io.mark4carter;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class LawnMowerAndLeafBlowersApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(LawnMowerAndLeafBlowersApplication.class, args);
	}
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private TechnicianRepository technicianRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
  @Override
  @Transactional
  public void run(String ...strings) throws Exception {
    Technician techOne = new Technician("TechBilly");
    technicianRepository.save(techOne);
    
    Customer customerOne = new Customer("CustomerJohn", techOne, new Date().getTime());
    technicianRepository.saveAndFlush(techOne);    
    customerRepository.save(customerOne);
    
    Invoice invoiceOne = new Invoice(customerOne, techOne, new Date().getTime());
    invoiceRepository.save(invoiceOne);
   
  }
}

@Controller
class HomeController {
  @RequestMapping("/")
  public String index() {
    return "index.html";
  }
}