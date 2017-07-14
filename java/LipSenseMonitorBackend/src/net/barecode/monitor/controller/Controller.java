package net.barecode.monitor.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import net.barecode.monitor.notify.SendEmail;
import net.barecode.monitor.pojo.inventory.Inventory;
import net.barecode.monitor.pojo.wishlist.Wishlist;
import net.barecode.monitor.pojo.wishlist.WishlistItem;
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
		if (holder == null) {
			holder = new Controller();
			holder.doUpdateInventory();
		}
		return holder;
	}

	private Inventory inventory;
	private final Wishlists wishlists;
	private final QueryLiveInventory query;
	private final ParseInventory parser;
	private final WishlistCompare compare;

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
		wishlists = new Wishlists();
		query = new QueryLiveInventory(distributorID, distributorPassword);
		parser = new ParseInventory();
		compare = new WishlistCompare(new SendEmail(automationEmailID, automationEmailPassword));
		

		// Populate fake wishlist
		Wishlist wl = new Wishlist("12345", notificationEmail);
		wl.list.add(new WishlistItem(1090));
		wl.list.add(new WishlistItem(1510));
		wishlists.put(wl.distributorID, wl);
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
		if (value == null) {
			System.out.println("Required environment variable not set: " + key);
			value = "NULL";
		}
		return value;
	}

	/**
	 * cronjob-style invocation to update the singleton inventory.
	 */
	@Schedule(hour = "*", minute = "*", second = "*/5", persistent = false)
	// @Schedule(hour = "*", minute = "0,30", second = "0", persistent = false)
	public void updateInventory() {
		System.out.println("Update requested at " + new Date());
//		getInstance().doUpdateInventory();
	}

	/**
	 * Updates the inventory and drives compareAndNotify().
	 */
	private void doUpdateInventory() {
		synchronized (inventory) {
			boolean useFake = true;
			if (useFake) {
				doFakeUpdate();
			} else {
				String inventoryHTML = null;
				try {
					inventoryHTML = query.query();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

				if (inventoryHTML != null) {
					inventory = parser.parseInventory(inventoryHTML);
				} else {
					doFakeUpdate();
				}
			}
			
			compareAndNotify();
		}
	}

	private void doFakeUpdate() {
		// Populate fake inventory
		File f = new File("inventory.html");
		System.out.println(f.getAbsolutePath());
		StringBuilder sb = new StringBuilder();
		BufferedReader buf = null;
		try {
			FileInputStream fis = new FileInputStream(f);
			buf = new BufferedReader(new InputStreamReader(fis));
			String line = buf.readLine();

			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		String fileAsString = sb.toString();
		inventory = parser.parseInventory(fileAsString);
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
