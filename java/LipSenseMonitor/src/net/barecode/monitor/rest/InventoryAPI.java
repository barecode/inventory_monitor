package net.barecode.monitor.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.barecode.monitor.inventory.Inventory;
import net.barecode.monitor.inventory.InventoryCategory;
import net.barecode.monitor.inventory.InventoryItem;

/**
 * Read-only API to display the inventory of SeneGense at the time of the last
 * update.
 * 
 * @author mcthomps
 */
@Path("inventory")
public class InventoryAPI {

	private Inventory inventory = new Inventory();

	public InventoryAPI() {
		// Populate fake inventory
		InventoryCategory c1 = new InventoryCategory("INDIVIDUAL - LipSense® Liquid Lip Colors (0.25 oz)");
		c1.addItem(new InventoryItem(1360, "Apple Cider LipSense", true));
		c1.addItem(new InventoryItem(1203, "Aussie Rose LipSense", false));
		c1.addItem(new InventoryItem(1090, "Plum LipSense", false));
		inventory.addCategory(c1);

		InventoryCategory c2 = new InventoryCategory("Glosses (0.25 oz)");
		c2.addItem(new InventoryItem(1566, "Bougainvillea Gloss", false));
		c2.addItem(new InventoryItem(1510, "Glossy Gloss", true));
		inventory.addCategory(c2);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getInventory() {
		return inventory;
	}
}
