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

	public WishlistItem getItem(int itemNumber) {
		WishlistItem foundItem = null;
		for (WishlistItem item : list) {
			if (item.itemNumber == itemNumber) {
				foundItem = item;
				break;
			}
		}
		return foundItem;
	}

	public WishlistItem addItem(int itemNumber) {
		WishlistItem item = new WishlistItem(itemNumber);
		list.add(item);
		return item;
	}

	public WishlistItem removeItem(int itemNumber) {
		WishlistItem foundItem = null;
		for (int i = 0; i < list.size(); i++) {
			WishlistItem item = list.get(i);
			if (item.itemNumber == itemNumber) {
				foundItem = item;
				list.remove(i);
				break;
			}
		}
		return foundItem;
	}

}
