package net.barecode.monitor.wishlist;

/**
 * Representation of a WishlistItem.
 * <p>
 * Each item in the wishlist contains:
 * <ul>
 * <li>itemNumber - the product number of the item</li>
 * <li>isWatched - indicates if the item is being actively watched</li>
 * <li>isNotified - indicates if a notification was sent for the item</li>
 * </ul>
 * 
 * @author barecode
 */
public class WishlistItem {
	public final int itemNumber;
	public boolean isWatched = true;
	public boolean isNotified = false;

	public WishlistItem(int itemNumber) {
		this.itemNumber = itemNumber;
	}

}
