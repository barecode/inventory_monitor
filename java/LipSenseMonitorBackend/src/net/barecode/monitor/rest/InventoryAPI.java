package net.barecode.monitor.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.barecode.monitor.controller.Controller;
import net.barecode.monitor.pojo.inventory.Inventory;

/**
 * API to display the inventory of SeneGense at the time of the last update.
 * An update can be requested via ?update=true query param.
 * 
 * @author barecode
 */
@Path("inventory")
public class InventoryAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getInventory(@QueryParam("update") boolean update) {
		Controller c = Controller.getInstance();
		if (update) {
			c.updateInventory();
		}
		return c.getInventory();
	}

}
