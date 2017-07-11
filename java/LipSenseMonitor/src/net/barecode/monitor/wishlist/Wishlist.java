package net.barecode.monitor.wishlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a Wishlist for a given distributor.
 * <p>
 * Each wishlist contains:
 * <ul>
 * <li>distributorID - the distributor ID owner of the wishlist</li>
 * <li>notificationEmail - the e-mail address to send the notification</li>
 * <li>list - the list of wishlist items</li>
 * </ul>
 * 
 * @author barecode
 */
public class Wishlist {
	public final String distributorID;
	public String notificationEmail;
	public List<WishlistItem> list;

	public Wishlist(String distributorID, String notificationEmail) {
		this.distributorID = distributorID;
		this.notificationEmail = notificationEmail;
		this.list = new ArrayList<WishlistItem>();
	}

	public WishlistItem getItem(String name) {
		WishlistItem foundItem = null;
		for (WishlistItem item : list) {
			if (item.name.equals(name)) {
				foundItem = item;
				break;
			}
		}
		return foundItem;
	}

	public WishlistItem addItem(String name) {
		WishlistItem item = new WishlistItem(name);
		list.add(item);
		return item;
	}

}
