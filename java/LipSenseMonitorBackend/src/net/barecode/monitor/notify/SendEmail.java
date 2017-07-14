package net.barecode.monitor.notify;

import java.util.Properties;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendEmail {
	// Inject the javax.mail.Session created in the server.xml
	@Resource(lookup="gmailSMTPSession")
	Session session;

	public void notifyInStock(String notifyEmail, String itemName) {

		final String username = System.getenv("AUTOMATION_EMAIL_ID");
		final String password = System.getenv("AUTOMATION_EMAIL_PASSWORD");
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		
		try {
			System.out.println("Session="+session);
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(notifyEmail));
			message.setSubject(itemName + " is in stock!");
			message.setText(itemName + " is in stock!");

			Transport.send(message);

			// If message is sent and no exceptions are thrown 
			// the servlet will print this message
			System.out.println("Message sent!");

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (javax.mail.MessagingException e) {
			e.printStackTrace();
		}
	}
}
