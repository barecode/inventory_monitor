package net.barecode.monitor.pojo.wishlist;

/**
 * Representation of a WishlistItem being watched.
 * <p>
 * Each item in the wishlist contains:
 * <ul>
 * <li>itemNumber - the product number of the item</li>
 * <li>isNotified - indicates if a notification was sent for the item</li>
 * </ul>
 * 
 * @author barecode
 */
public class WishlistItem {
	public final int itemNumber;
	public boolean isNotified = false;

	/**
	 * @param itemNumber
	 *            SeneGence item number
	 */
	public WishlistItem(int itemNumber) {
		this.itemNumber = itemNumber;
	}

	/**
	 * Set the notification flag.
	 * 
	 * @return This WishlistItem instance
	 */
	public WishlistItem setNotified() {
		isNotified = true;
		return this;
	}

	/**
	 * Clear the notification flag.
	 * 
	 * @return This WishlistItem instance
	 */
	public WishlistItem clearNotified() {
		isNotified = false;
		return this;
	}

}
