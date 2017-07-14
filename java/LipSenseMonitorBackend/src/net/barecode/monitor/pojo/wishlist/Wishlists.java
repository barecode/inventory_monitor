package net.barecode.monitor.pojo.wishlist;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for the persisted Wishlists.
 * 
 * @author barecode
 */
public class Wishlists {
	public Map<String, Wishlist> wishlistsByDistributorID = new HashMap<String, Wishlist>();

	/**
	 * Returns the list of Wishlist objects in this collection.
	 * 
	 * @return the list of Wishlist objects in this collection.
	 */
	public Collection<Wishlist> getLists() {
		return wishlistsByDistributorID.values();
	}

	/**
	 * Returns the collection of distributor IDs with existing watch lists.
	 * 
	 * @return the collection of distributor IDs with existing watch lists.
	 */
	public Collection<String> getDistributors() {
		return wishlistsByDistributorID.keySet();
	}

	/**
	 * Returns the Wishlist for the given distributor.
	 * 
	 * @param distributorID
	 *            the distributor ID of the wishlist owner
	 * @return the Wishlist owned by the distributor
	 */
	public Wishlist get(String distributorID) {
		return wishlistsByDistributorID.get(distributorID);
	}

	/**
	 * Adds a Wishlist to this collection.
	 * 
	 * @param distributorID
	 *            the distributor ID of the wishlist owner
	 * @param wishlist
	 *            The wishlist to add
	 */
	public void put(String distributorID, Wishlist list) {
		wishlistsByDistributorID.put(distributorID, list);
	}
}
