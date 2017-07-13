package net.barecode.monitor.inventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import net.barecode.monitor.query.ParseInventory;

/**
 * Cronjob-like task to update the inventory.
 * 
 * @author barecode
 */
@Singleton
@Startup
public class InventoryHolder {
	private static InventoryHolder holder = null;
	private static Inventory inventory;

	/**
	 * Encourage singleton model.
	 * 
	 * @return Singleton instance of InventoryHolder
	 */
	public static synchronized InventoryHolder getInstance() {
		if (holder == null) {
			holder = new InventoryHolder();
			inventory = new Inventory();
			doUpdateInventory();
		}
		return holder;
	}

	private static void doUpdateInventory() {
		synchronized (inventory) {
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
	}

	/**
	 * Update the singleton inventory.
	 */
	@Schedule(hour = "*", minute = "0,30", second = "0", persistent = false)
	public void updateInventory() {
		System.out.println("Update requested at " + new Date());
		doUpdateInventory();
	}

	/**
	 * Return the Inventory.
	 * 
	 * @return
	 */
	public Inventory getInventory() {
		return inventory;
	}

}
