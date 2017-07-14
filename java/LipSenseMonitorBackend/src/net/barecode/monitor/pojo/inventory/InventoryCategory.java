package net.barecode.monitor.pojo.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the an inventory category.
 * <p>
 * A category is a named collection of InventoryItems.
 * <p>
 * <ul>
 * <li>name - the name of the category</li>
 * <li>items - the collection of InventoryItem</li>
 * </ul>
 * 
 * @author barecode
 */
public class InventoryCategory {
	public String name;
	public List<InventoryItem> items;

	public InventoryCategory(String name) {
		this.name = name;
		items = new ArrayList<InventoryItem>();
	}

	public void addItem(InventoryItem inventoryItem) {
		items.add(inventoryItem);
	}
}
