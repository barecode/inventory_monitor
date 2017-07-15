package net.barecode.monitor.pojo.wishlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a Wishlist for a given distributor.
 * <p>
 * Each wishlist contains:
 * <ul>
 * <li>distributorID - the distributor ID of the wishlist owner</li>
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

	/**
	 * @param distributorID
	 *            SeneGence distributor ID
	 * @param notificationEmail
	 *            E-mail to send notifications
	 */
	public Wishlist(String distributorID, String notificationEmail) {
		this.distributorID = distributorID;
		this.notificationEmail = notificationEmail;
		this.list = new ArrayList<WishlistItem>();
	}

	/**
	 * Return the WishlistItem for the given itemNumer
	 * 
	 * @param itemNumber
	 *            SeneGence item number
	 * @return The WishlistItem for the given itemNumer, or null if not watched
	 */
	public synchronized WishlistItem getItem(int itemNumber) {
		WishlistItem foundItem = null;
		for (WishlistItem item : list) {
			if (item.itemNumber == itemNumber) {
				foundItem = item;
				break;
			}
		}
		return foundItem;
	}

	/**
	 * Adds a WishlistItem to the watched list.
	 * <p>
	 * An item can only be watched once, so another add for the same number will
	 * not change the contents of the Wishlist.
	 * 
	 * @param itemNumber
	 *            SeneGence item number to add
	 * @return The added (or already existing) WishlistItem
	 */
	public synchronized WishlistItem addItem(int itemNumber) {
		WishlistItem item = getItem(itemNumber);
		if (item == null) {
			item = new WishlistItem(itemNumber);
			list.add(item);
		}
		return item;
	}

	/**
	 * Removes a WishlistItem from the watched list.
	 * 
	 * @param itemNumber
	 *            SeneGence item number
	 * @return The removed WishlistItem, or null if not in the list.
	 */
	public synchronized WishlistItem removeItem(int itemNumber) {
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

	public synchronized Collection<WishlistItem> readonlyList() {
		return Collections.unmodifiableList(list);
	}

}
