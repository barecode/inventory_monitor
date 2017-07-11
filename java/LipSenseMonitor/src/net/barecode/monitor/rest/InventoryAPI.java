package net.barecode.monitor.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.barecode.monitor.inventory.Inventory;
import net.barecode.monitor.inventory.InventoryCategory;
import net.barecode.monitor.inventory.InventoryItem;
import net.barecode.monitor.query.ParseInventory;

/**
 * Read-only API to display the inventory of SeneGense at the time of the last
 * update.
 * 
 * @author mcthomps
 */
@Path("inventory")
public class InventoryAPI {

	private static Inventory inventory;

	static {
		// Populate fake inventory
		ParseInventory parser = new ParseInventory();
		File f = new File("inventory.html");
		System.out.println(f.getAbsolutePath());
		StringBuilder sb = new StringBuilder();
		BufferedReader buf = null;
		try {
			FileInputStream fis = new FileInputStream(f);
			buf = new BufferedReader(new InputStreamReader(fis));
			String line = buf.readLine();

			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		String fileAsString = sb.toString();
		inventory = parser.parseInventory(fileAsString);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Inventory getInventory() {
		return inventory;
	}

}
