package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendEmail {

	@Autowired
	private JavaMailSender sender;
	
	public void send(String toEmailAdd, String subject, String message ) {
		SimpleMailMessage smm = new SimpleMailMessage();
		smm.setFrom("daihuygingas@gmail.com");
		smm.setTo(toEmailAdd);
		smm.setSubject(subject);
		smm.setText(message);
		sender.send(smm);
	}
	
}
