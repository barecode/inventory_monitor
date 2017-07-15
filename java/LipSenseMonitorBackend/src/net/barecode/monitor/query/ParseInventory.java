package net.barecode.monitor.query;

import java.util.ArrayList;
import java.util.List;

import net.barecode.monitor.pojo.inventory.Inventory;
import net.barecode.monitor.pojo.inventory.InventoryCategory;
import net.barecode.monitor.pojo.inventory.InventoryItem;

/**
 * Creates a new Inventory object from the parsed inventory HTML.
 * 
 * @author barecode
 */
public class ParseInventory {

	/**
	 * Parse the HTML of the SenGense inventory and construct an Inventory
	 * object.
	 * 
	 * @param inventoryHTML
	 * @return
	 */
	public Inventory parseInventory(String inventoryHTML) {
		Inventory inventory = new Inventory();

		List<String> categories = findCategories(inventoryHTML);
		for (String category : categories) {
			parseCategory(inventoryHTML, inventory, category);
		}

		return inventory;
	}

	/**
	 * Find all of the categories within the inventoryHTML.
	 * 
	 * @param inventoryHTML
	 * @return A list of category titles
	 */
	private List<String> findCategories(String inventoryHTML) {
		List<String> categories = new ArrayList<String>();
		int categoryLineIdx = inventoryHTML.indexOf("ProductCategoryDescription.aspx", 0);
		while (categoryLineIdx > 0) {
			int colorsIdx = inventoryHTML.indexOf("#000000", categoryLineIdx);
			int startIdx = colorsIdx + 9;
			int endIdx = inventoryHTML.indexOf('<', startIdx);
			String category = inventoryHTML.substring(startIdx, endIdx);
			categories.add(category);

			categoryLineIdx = inventoryHTML.indexOf("ProductCategoryDescription.aspx", endIdx);
		}

		return categories;
	}

	/**
	 * @param inventoryHTML
	 * @param inventory
	 * @param categoryToFind
	 */
	private void parseCategory(String inventoryHTML, Inventory inventory, String categoryName) {
		InventoryCategory category = new InventoryCategory(categoryName);

		int categoryIndex = inventoryHTML.indexOf(categoryName);
		int nextCategoryIndex = inventoryHTML.indexOf("ProductCategoryDescription.aspx", categoryIndex);

		int pos = categoryIndex;
		int nextRowStart = inventoryHTML.indexOf("<tr>", pos);
		int nextRowEnd = inventoryHTML.indexOf("</tr>", nextRowStart);
		while (nextRowEnd < nextCategoryIndex) {
			String itemHTML = inventoryHTML.substring(nextRowStart, nextRowEnd);
			InventoryItem item = parseItem(itemHTML);
			if (item != null) {
				category.addItem(item);
			}

			pos = nextRowEnd + 5;
			nextRowStart = inventoryHTML.indexOf("<tr>", pos);
			nextRowEnd = inventoryHTML.indexOf("</tr>", nextRowStart);
		}

		// We only care about non-empty categories
		if (!category.items.isEmpty()) {
			inventory.addCategory(category);
		}
	}

	private InventoryItem parseItem(String itemHTML) {
		int iNumIdx = itemHTML.indexOf("<a name='");
		if (iNumIdx < 1) {
			return null;
		}
		try {
			String iNumStr = itemHTML.substring(iNumIdx + 9, iNumIdx + 9 + 4);
//			System.out.println(iNumStr);
			int itemNumber = Integer.parseInt(iNumStr);

			int nameIdx = itemHTML.indexOf("#0000FF'>");
			int nameIdxEnd = itemHTML.indexOf('<', nameIdx);
			String name = itemHTML.substring(nameIdx + 9, nameIdxEnd);
//			System.out.println(name);

			int stockIdx = itemHTML.indexOf("color=\"Red\" size=\"2\">", nameIdxEnd);
			int stockIdxEnd = itemHTML.indexOf('<', stockIdx);
			String stockStr = itemHTML.substring(stockIdx + "color=\"Red\" size=\"2\">".length(), stockIdxEnd);
//			System.out.println(stockStr);
			boolean isInStock = stockStr.isEmpty();

			return new InventoryItem(itemNumber, name, isInStock);
		} catch (java.lang.NumberFormatException e) {
			// There are some things with alphacharacters in the ID, ignore them
			return null;
		}
	}
}
