package net.barecode.monitor.query;

import net.barecode.monitor.pojo.inventory.Inventory;
import net.barecode.monitor.pojo.inventory.InventoryCategory;
import net.barecode.monitor.pojo.inventory.InventoryItem;

public class ParseInventory {

	/**
	 * Parse the HTML of the SenGense inventory and construct an Inventory object.
	 * 
	 * @param inventoryHTML
	 * @return
	 */
	public Inventory parseInventory(String inventoryHTML) {
		Inventory inventory = new Inventory();

		parseCategory(inventoryHTML, inventory, "Liquid Lip Colors (0.25 oz)");
		parseCategory(inventoryHTML, inventory, "Glosses (0.25 oz)");

		return inventory;
	}

	/**
	 * @param inventoryHTML
	 * @param inventory
	 */
	private void parseCategory(String inventoryHTML, Inventory inventory, String categoryToFind) {
		int colorsIndex = inventoryHTML.indexOf(categoryToFind);
		int contianingElementEnd = inventoryHTML.lastIndexOf('>', colorsIndex);
		int nextElementStart = inventoryHTML.indexOf('<', colorsIndex);
		String categoryName = inventoryHTML.substring(contianingElementEnd+1, nextElementStart);
		InventoryCategory category = new InventoryCategory(categoryName);
		inventory.addCategory(category);

		int nextCategoryIndex = inventoryHTML.indexOf("ProductCategoryDescription.aspx", colorsIndex);

		int pos = colorsIndex;
		int nextRowStart = inventoryHTML.indexOf("<tr>", pos);
		int nextRowEnd = inventoryHTML.indexOf("</tr>", nextRowStart);
		while (nextRowEnd < nextCategoryIndex) {
			String itemHTML = inventoryHTML.substring(nextRowStart, nextRowEnd);
			InventoryItem item = parseItem(itemHTML);
			if (item != null) {
				category.addItem(item);
			}

			pos = nextRowEnd+5;
			nextRowStart = inventoryHTML.indexOf("<tr>", pos);
			nextRowEnd = inventoryHTML.indexOf("</tr>", nextRowStart);
		}
	}

	private InventoryItem parseItem(String itemHTML) {
		int iNumIdx = itemHTML.indexOf("<a name='");
		if (iNumIdx < 1) {
			return null;
		}
		String iNumStr = itemHTML.substring(iNumIdx+9, iNumIdx+9+4);
		System.out.println(iNumStr);
		int itemNumber = Integer.parseInt(iNumStr);

		int nameIdx = itemHTML.indexOf("#0000FF'>");
		int nameIdxEnd = itemHTML.indexOf('<', nameIdx);
		String name = itemHTML.substring(nameIdx+9, nameIdxEnd);
		System.out.println(name);
		
		int stockIdx = itemHTML.indexOf("color=\"Red\" size=\"2\">", nameIdxEnd);
		int stockIdxEnd = itemHTML.indexOf('<', stockIdx);
		String stockStr = itemHTML.substring(stockIdx+"color=\"Red\" size=\"2\">".length(), stockIdxEnd);
		System.out.println(stockStr);
		boolean isInStock = stockStr.isEmpty();
		// TODO Auto-generated method stub
		return new InventoryItem(itemNumber, name, isInStock);
	}
}
