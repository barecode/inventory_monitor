package net.barecode.monitor.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.barecode.monitor.inventory.Inventory;
import net.barecode.monitor.inventory.InventoryHolder;

/**
 * Read-only API to display the inventory of SeneGense at the time of the last
 * update.
 * 
 * @author barecode
 */
@Path("inventory")
public class InventoryAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getInventory(@QueryParam("update") boolean update) {
		InventoryHolder inventoryHolder = InventoryHolder.getInstance();
		if (update) {
			inventoryHolder.updateInventory();
		}
		return inventoryHolder.getInventory();
	}

}
