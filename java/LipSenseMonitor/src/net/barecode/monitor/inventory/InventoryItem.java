package net.barecode.monitor.inventory;

/**
 * A shallow representation of the an inventory item. Only critical information
 * necessary to monitor inventory and compare to the
 * {@link net.barecode.monitor.wishlist.Wishlist} is kept.
 * <p>
 * An InventoryItem is a collection of:
 * <p>
 * <ul>
 * <li>itemNumber - the product number of the item</li>
 * <li>name - the name of the item</li>
 * <li>isInStock - indicates if the item is in stock</li>
 * </ul>
 * 
 * @author barecode
 */
public class InventoryItem {
	public int itemNumber;
	public String name;
	public boolean isInStock;

	public InventoryItem(int itemNumber, String name, boolean isInStock) {
		this.itemNumber = itemNumber;
		this.name = name;
		this.isInStock = isInStock;
	}
}
