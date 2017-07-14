package net.barecode.monitor.query;

import java.util.Map;

import net.barecode.monitor.inventory.Inventory;
import net.barecode.monitor.inventory.InventoryCategory;
import net.barecode.monitor.inventory.InventoryHolder;
import net.barecode.monitor.inventory.InventoryItem;
import net.barecode.monitor.wishlist.Wishlist;
import net.barecode.monitor.wishlist.WishlistItem;

public class WishlistCompare {

	/**
	 * Hilariously inefficient implementation.
	 * 
	 * @param wishlists
	 * @return
	 */
	public int compare(Map<String, Wishlist> wishlists) {
		int matchedItems = 0;
		Inventory inv = InventoryHolder.getInstance().getInventory();
		for (Wishlist list : wishlists.values()) {
			for (WishlistItem item : list.list) {
				for (InventoryCategory category : inv.categories) {
					for (InventoryItem invItem : category.items) {
						if (invItem.itemNumber == item.itemNumber) {
							if (invItem.isInStock) {
								System.out.println(invItem.name + " is in stock. Notify " + list.distributorID);
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
