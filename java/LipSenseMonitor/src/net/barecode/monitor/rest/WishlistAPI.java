package net.barecode.monitor.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.barecode.monitor.wishlist.Wishlist;
import net.barecode.monitor.wishlist.WishlistItem;

@Path("wishlists")
public class WishlistAPI {

	private Map<String, Wishlist> wishlists = new HashMap<String, Wishlist>();

	public WishlistAPI() {
		// Populate fake wishlist
		Wishlist wl = new Wishlist("12345", "abc@123.com");
		wl.list.add(new WishlistItem(1090));
		wl.list.add(new WishlistItem(1510));
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
}
