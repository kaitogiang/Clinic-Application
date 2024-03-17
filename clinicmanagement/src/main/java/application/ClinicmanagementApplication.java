package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import controllers.LoginController;
import services.SendEmail;

@SpringBootApplication
@ComponentScan(basePackages = {"services"}) // Include the 'services' package for component scanning
public class ClinicmanagementApplication {

	@Autowired
	private SendEmail senderservice;
	
	public static void main(String[] args) {
		SpringApplication.run(ClinicmanagementApplication.class, args);
	}
	@EventListener(ApplicationReadyEvent.class)
	public void sendEmail() {
		senderservice.send(LoginController.emailAdd, "Email Verify", Integer.toString(LoginController.otp)+"\nMã sẽ có hiệu lực trong vòng 2 phút");
	}

}
