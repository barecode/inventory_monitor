package net.barecode.monitor.wishlist;

/**
 * Representation of a WishlistItem.
 * <p>
 * Each item in the wishlist contains:
 * <ul>
 * <li>name - the full name of the watched item</li>
 * <li>isWatched - indicates if the item is being actively watched</li>
 * <li>isNotified - indicates if a notification was sent for the item</li>
 * </ul>
 * 
 * @author barecode
 */
public class WishlistItem {
	public final String name;
	public boolean isWatched = true;
	public boolean isNotified = false;

	public WishlistItem(String name) {
		this.name = name;
	}

}
