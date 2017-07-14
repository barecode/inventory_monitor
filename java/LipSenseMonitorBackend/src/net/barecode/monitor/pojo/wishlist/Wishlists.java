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

	public void addWishlist(String distributorID, Wishlist wishlist) {
		wishlistsByDistributorID.put(distributorID, wishlist);
	}

	public Collection<Wishlist> lists() {
		return wishlistsByDistributorID.values();
	}

	public Collection<String> getDistributors() {
		return wishlistsByDistributorID.keySet();
	}

	public Wishlist get(String distributorID) {
		return wishlistsByDistributorID.get(distributorID);
	}
	
	public void put(String distributorID, Wishlist list) {
		wishlistsByDistributorID.put(distributorID, list);
	}
}
