package net.barecode.monitor.query;

import net.barecode.monitor.notify.SendEmail;
import net.barecode.monitor.pojo.inventory.Inventory;
import net.barecode.monitor.pojo.inventory.InventoryCategory;
import net.barecode.monitor.pojo.inventory.InventoryItem;
import net.barecode.monitor.pojo.wishlist.Wishlist;
import net.barecode.monitor.pojo.wishlist.WishlistItem;
import net.barecode.monitor.pojo.wishlist.Wishlists;

public class WishlistCompare {
	private SendEmail email = new SendEmail();

	/**
	 * Hilariously inefficient implementation.
	 * 
	 * @param wishlists
	 * @return
	 */
	public int compare(Inventory inv, Wishlists wishlists) {
		int matchedItems = 0;

		for (Wishlist list : wishlists.lists()) {
			for (WishlistItem item : list.list) {
				for (InventoryCategory category : inv.categories) {
					for (InventoryItem invItem : category.items) {
						if (invItem.itemNumber == item.itemNumber) {
							if (invItem.isInStock) {
								System.out.println(invItem.name + " is in stock. Notify " + list.distributorID);
								email.notifyInStock(list.notificationEmail, invItem.name);
								matchedItems++;
							}
						}
					}
				}
			}
		}

		return matchedItems;
	}

}
