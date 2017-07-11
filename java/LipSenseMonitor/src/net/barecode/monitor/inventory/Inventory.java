package net.barecode.monitor.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the current SeneGense inventory.
 * <p>
 * The structure of the Inventory is a collection of categories, and the
 * categories is a collection of items.
 * <p>
 * <ul>
 * <li>epochAtLastUpdate - the epoch at the time of the last update of the
 * inventory</li>
 * <li>categories - the collection of categories in the inventory</li>
 * </ul>
 * 
 * @author barecode
 */
public class Inventory {
	public long epochAtLastUpdate;
	public List<InventoryCategory> categories;

	public Inventory() {
		this.epochAtLastUpdate = System.currentTimeMillis();
		this.categories = new ArrayList<InventoryCategory>();
	}

	public void addCategory(InventoryCategory category) {
		categories.add(category);
	}
}
