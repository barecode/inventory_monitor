package net.barecode.monitor.controller;

import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import net.barecode.monitor.notify.SendEmail;
import net.barecode.monitor.pojo.inventory.Inventory;
import net.barecode.monitor.pojo.persistence.MongoPersistence;
import net.barecode.monitor.pojo.wishlist.Wishlist;
import net.barecode.monitor.pojo.wishlist.Wishlists;
import net.barecode.monitor.query.ParseInventory;
import net.barecode.monitor.query.QueryLiveInventory;
import net.barecode.monitor.query.WishlistCompare;

/**
 * The Controller
 */
@Singleton
@Startup
public class Controller {
	private static Controller holder = null;

	/**
	 * Encourage singleton model.
	 * 
	 * @return Singleton instance of InventoryHolder
	 */
	public static synchronized Controller getInstance() {
		return getInstance(true);
	}

	/**
	 * Return the singleton instance. Optionally drive an update if needed on
	 * creation.
	 * 
	 * @param shouldUpdate
	 *            {@code true} if {@link #doUpdateInventory()} should be called
	 *            if created, {@code false} otherwise
	 * @return Singleton instance of InventoryHolder
	 */
	private static synchronized Controller getInstance(boolean shouldUpdate) {
		if (holder == null) {
			holder = new Controller();
			holder.loadWishlistsFromPersistence();
			if (shouldUpdate) {
				holder.doUpdateInventory();
			}
		}
		return holder;
	}

	private Inventory inventory;
	private Wishlists wishlists;
	private final QueryLiveInventory query;
	private final ParseInventory parser;
	private final WishlistCompare compare;
	private final MongoPersistence persistence;

	private final String distributorID;
	private final String distributorPassword;
	private final String automationEmailID;
	private final String automationEmailPassword;
	private final String notificationEmail;

	public Controller() {
		distributorID = getenv("SENEGENCE_DIST_ID");
		distributorPassword = getenv("SENEGENCE_DIST_PASS");
		automationEmailID = getenv("AUTOMATION_EMAIL_ID");
		automationEmailPassword = getenv("AUTOMATION_EMAIL_PASSWORD");
		notificationEmail = getenv("NOTIFICATION_EMAIL");

		inventory = new Inventory();
		wishlists = initializeWithEmptyWishlists();
		query = new QueryLiveInventory(distributorID, distributorPassword);
		parser = new ParseInventory();
		compare = new WishlistCompare(new SendEmail(automationEmailID, automationEmailPassword));
		persistence = new MongoPersistence();
	}

	/**
	 * Populate initial wishlist with the primary distributorID
	 * 
	 * @return
	 */
	private Wishlists initializeWithEmptyWishlists() {
		Wishlists wishlists = new Wishlists();
		Wishlist wl = new Wishlist(distributorID, notificationEmail);
		wishlists.put(wl.distributorID, wl);
		return wishlists;
	}

	/**
	 * Return the environment variable value for the given key.
	 * <p>
	 * If no value is defined, a message is printed. These values are requred.
	 * 
	 * @param key
	 *            The environment variable name
	 * @return The environment variable value
	 */
	private String getenv(final String key) {
		String value = System.getenv(key);
		if (value == null || value.isEmpty()) {
			System.out.println("Required environment variable not set: " + key);
			value = "NULL";
		}
		return value;
	}

	/**
	 * cronjob-style invocation to update the singleton inventory.
	 */
	@Schedule(hour = "*", minute = "0,30", second = "0", persistent = false)
	public void updateInventory() {
		getInstance(false).doUpdateInventory();
	}

	/**
	 * Updates the inventory and drives compareAndNotify().
	 */
	private void doUpdateInventory() {
		System.out.println("Update requested at " + new Date());

		synchronized (this) {
			try {
				inventory = parser.parseInventory(query.query());
			} catch (Exception e) {
				System.out.println("Unable to query and parse live inventory");
				e.printStackTrace();
			}

			compareAndNotify();
		}
	}

	/**
	 * Return the Inventory.
	 * 
	 * @return
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Return the Wishlists.
	 * 
	 * @return
	 */
	public Wishlists getWishlists() {
		return wishlists;
	}

	public void saveWishlists() {
		persistence.store(wishlists);
	}

	private void loadWishlistsFromPersistence() {
		Wishlists wishlists = persistence.load();
		if (wishlists != null) {
			this.wishlists = wishlists;
		}
	}

	/**
	 * Drive the comparison logic.
	 * <p>
	 * This is done automatically from updateInventory().
	 * 
	 * @return The number of notifications attempted
	 */
	public int compareAndNotify() {
		return compare.compare(inventory, wishlists);
	}

}
