package net.barecode.monitor.rest;

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
		wl.list.add(new WishlistItem("Plum LipSense"));
		wl.list.add(new WishlistItem("Glossy Gloss"));
		wishlists.put(wl.distributorID, wl);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Wishlist> getWishlists() {
		return wishlists;
	}

	@GET
	@Path("wishlists/{distributorID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Wishlist getWishlist(@PathParam("name") String distributorID) {
		return wishlists.get(distributorID);
	}

	@POST
	@Path("wishlists/{distributorID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem addWishlistItem(@PathParam("name") String distributorID, String inputJson) {
		return wishlists.get(distributorID).addItem(inputJson);
	}

	@GET
	@Path("wishlists/{distributorID}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem getItem(@PathParam("name") String distributorID, @PathParam("name") String name) {
		return wishlists.get(distributorID).getItem(name);
	}
}
