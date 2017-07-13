package net.barecode.monitor.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.barecode.monitor.query.WishlistCompare;
import net.barecode.monitor.wishlist.Wishlist;
import net.barecode.monitor.wishlist.WishlistItem;

@Path("wishlists")
public class WishlistAPI {

	private static Map<String, Wishlist> wishlists = new HashMap<String, Wishlist>();
	private static WishlistCompare compare = new WishlistCompare();

	static {
		// Populate fake wishlist
		Wishlist wl = new Wishlist("12345", "abc@123.com");
		wl.list.add(new WishlistItem(1090));
		WishlistItem item = new WishlistItem(1510);
		item.isNotified = true;
		wl.list.add(item);
		wishlists.put(wl.distributorID, wl);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Wishlist> getWishlists() {
		return wishlists;
	}

	@GET
	@Path("distributors")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getDistributors() {
		return wishlists.keySet();
	}

	@GET
	@Path("{distributorID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Wishlist getWishlist(@PathParam("distributorID") String distributorID) {
		return wishlists.get(distributorID);
	}

	@POST
	@Path("{distributorID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem addWishlistItem(@PathParam("distributorID") String distributorID, int itemNumber) {
		return wishlists.get(distributorID).addItem(itemNumber);
	}

	@GET
	@Path("{distributorID}/{itemNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem getItem(@PathParam("distributorID") String distributorID,
			@PathParam("itemNumber") int itemNumber) {
		return wishlists.get(distributorID).getItem(itemNumber);
	}

	@PUT
	@Path("{distributorID}/{itemNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem clearNotificationItem(@PathParam("distributorID") String distributorID,
			@PathParam("itemNumber") int itemNumber) {
		return wishlists.get(distributorID).getItem(itemNumber).clearNotification();
	}

	@DELETE
	@Path("{distributorID}/{itemNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem removeWishlistItem(@PathParam("distributorID") String distributorID,
			@PathParam("itemNumber") int itemNumber) {
		return wishlists.get(distributorID).removeItem(itemNumber);
	}

	@GET
	@Path("notify")
	@Produces(MediaType.APPLICATION_JSON)
	public int notifyDistributors() {
		return compare.compare(wishlists);
	}

}
