package net.barecode.monitor.query;

import net.barecode.monitor.notify.Notifier;
import net.barecode.monitor.pojo.inventory.Inventory;
import net.barecode.monitor.pojo.inventory.InventoryCategory;
import net.barecode.monitor.pojo.inventory.InventoryItem;
import net.barecode.monitor.pojo.wishlist.Wishlist;
import net.barecode.monitor.pojo.wishlist.WishlistItem;
import net.barecode.monitor.pojo.wishlist.Wishlists;

/**
 * Wishlist to inventory comparison logic.
 * <p>
 * If items in the wishlist are in stock,
 * 
 * @author barecode
 */
public class WishlistCompare {
	private final Notifier notifier;

	/**
	 * @param notifier
	 *            Notifier object
	 */
	public WishlistCompare(Notifier notifier) {
		this.notifier = notifier;
	}

	/**
	 * Step through each Wishlist and compare against the current Inventory. If
	 * a WishlistItem is in stock, send a notification.
	 * <p>
	 * This implementation is hilariously inefficient... maybe? The lists are
	 * not big.
	 * 
	 * @param inv
	 *            Inventory
	 * @param wishlists
	 *            Wishlists
	 * @return The number of notifications sent
	 */
	public int compare(Inventory inv, Wishlists wishlists) {
		int notifications = 0;

		for (Wishlist wishlist : wishlists.getLists()) {
			for (WishlistItem watched : wishlist.list) {
				notifications += compareAndNotify(inv, wishlist, watched);
			}
		}

		return notifications;
	}

	/**
	 * Step through the inventory looking for the watched item. If the watched
	 * item is found and is in stock, send a notification.
	 * 
	 * @param inv
	 *            Inventory
	 * @param wishlists
	 *            Wishlists
	 * @param watched
	 *            WishlistItem to find
	 * @return 0 if the item was not matched, is not in stock, or was already
	 *         notified otherwise 1
	 */
	private int compareAndNotify(Inventory inv, Wishlist wishlist, WishlistItem watched) {
		for (InventoryCategory category : inv.categories) {
			for (InventoryItem invItem : category.items) {
				if (invItem.itemNumber == watched.itemNumber) {
					if (invItem.isInStock) {
						return notify(wishlist, watched, invItem);
					}
				}
			}
		}
		return 0;
	}

	/**
	 * Send a notification for the watched item, if not already sent.
	 * 
	 * @param wishlist
	 *            The context Wishlist
	 * @param watched
	 *            The watched item
	 * @param invItem
	 *            The inventory item in stock
	 * @return 0 if the item was already notified, 1 if the notification was
	 *         attempted. If the notification was sent but failed, the next pass
	 *         will still notify.
	 */
	private int notify(Wishlist wishlist, WishlistItem watched, InventoryItem invItem) {
		if (watched.isNotified) {
			return 0;
		}

		System.out.println(invItem.name + " is in stock. Notify " + wishlist.distributorID);
		if (notifier.notifyInStock(wishlist.notificationEmail, invItem.name)) {
			watched.setNotified();
		}
		return 1;
	}

}
