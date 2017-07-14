package net.barecode.monitor.notify;

public interface Notifier {
	
	/**
	 * Send an "in stock" notification email.
	 * 
	 * @param notifyEmail The TO email address
	 * @param itemName The item name that is in stock
	 * @return {@code true} if the notification was successfully sent, {@code false} otherwise
	 */
	public boolean notifyInStock(String notifyEmail, String itemName);
	
}
