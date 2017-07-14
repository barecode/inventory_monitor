package net.barecode.monitor.notify;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Embodiment of SMTP mail logic used to send email notifications.
 * 
 * @author barecode
 */
public class SendEmail implements Notifier {
	private final Session session;
	private final String automationEmailID;

	/**
	 * @param automationEmailID The gmail account
	 * @param automationEmailPassword The gmail account password
	 */
	public SendEmail(String automationEmailID, String automationEmailPassword) {
		this.automationEmailID = automationEmailID;

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(automationEmailID, automationEmailPassword);
			}
		});
	}

	/** {@inheritDoc} */
	public boolean notifyInStock(String notifyEmail, String itemName) {
		boolean result = false;
		try {
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(automationEmailID));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notifyEmail));
			message.setSubject(itemName + " is in stock!");
			message.setText(itemName + " is in stock!");

			Transport.send(message);
			result = true;
		} catch (AddressException e) {
			System.out.println("AddressException during notifyInStock");
			e.printStackTrace();
		} catch (MessagingException e) {
			System.out.println("MessagingException during notifyInStock");
			e.printStackTrace();
		}
		
		return result;
	}
}
