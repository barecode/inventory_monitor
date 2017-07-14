package net.barecode.monitor.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

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
	private static Inventory inventory = new Inventory();
	private static Wishlists wishlists = new Wishlists();
	private static final QueryLiveInventory query = new QueryLiveInventory();
	private static final ParseInventory parser = new ParseInventory();
	private static final WishlistCompare compare = new WishlistCompare();
	
	/**
	 * cronjob 
	 * Update the singleton inventory.
	 */
	@Schedule(hour = "*", minute = "*", second = "*/5", persistent = false)
	//@Schedule(hour = "*", minute = "0,30", second = "0", persistent = false)
	public void updateInventory() {
		System.out.println("Update requested at " + new Date());
//		doUpdateInventory();
	}

	/**
	 * Encourage singleton model.
	 * 
	 * @return Singleton instance of InventoryHolder
	 */
	public static synchronized Controller getInstance() {
		if (holder == null) {
			holder = new Controller();
			doUpdateInventory();
		}
		return holder;
	}

	private static void doUpdateInventory() {
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
			
			// Populate fake wishlist
			Wishlist wl = new Wishlist("12345", "abc@123.com");
			wl.list.add(new WishlistItem(1090));
			WishlistItem item = new WishlistItem(1510);
			item.isNotified = true;
			wl.list.add(item);
			wishlists.put(wl.distributorID, wl);
		}
	}
	
	private static void doFakeUpdate() {
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

	public Wishlists getWishlists() {
		return wishlists;
	}
	
	public int compareAndNotify() {
		return compare.compare(inventory, wishlists);
	}
	
}
