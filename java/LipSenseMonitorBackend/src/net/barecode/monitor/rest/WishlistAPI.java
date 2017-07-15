package net.barecode.monitor.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.barecode.monitor.controller.Controller;
import net.barecode.monitor.pojo.wishlist.Wishlist;
import net.barecode.monitor.pojo.wishlist.WishlistItem;
import net.barecode.monitor.pojo.wishlist.Wishlists;

/**
 * API to manage the multiple wishlists for distributors.
 * 
 * @author barecode
 */
@Path("wishlists")
public class WishlistAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Wishlists getWishlists() {
		return Controller.getInstance().getWishlists();
	}

	@GET
	@Path("distributors")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getDistributors() {
		return Controller.getInstance().getWishlists().getDistributors();
	}

	@GET
	@Path("{distributorID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Wishlist getWishlist(@PathParam("distributorID") String distributorID) {
		return Controller.getInstance().getWishlists().get(distributorID);
	}

	@POST
	@Path("{distributorID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem addWishlistItem(@PathParam("distributorID") String distributorID, int itemNumber) {
		Controller controller = Controller.getInstance();
		WishlistItem item = controller.getWishlists().get(distributorID).addItem(itemNumber);
		controller.saveWishlists();
		return item;
	}

	@GET
	@Path("{distributorID}/{itemNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem getItem(@PathParam("distributorID") String distributorID,
			@PathParam("itemNumber") int itemNumber) {
		return Controller.getInstance().getWishlists().get(distributorID).getItem(itemNumber);
	}

	@PUT
	@Path("{distributorID}/{itemNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem clearNotificationItem(@PathParam("distributorID") String distributorID,
			@PathParam("itemNumber") int itemNumber) {
		Controller controller = Controller.getInstance();
		WishlistItem item = null;
		Wishlist wishlist = controller.getWishlists().get(distributorID);
		if (wishlist != null) {
			item = wishlist.getItem(itemNumber);
			if (item != null) {
				item.clearNotified();
				controller.saveWishlists();
			}
		}
		return item;
	}

	@DELETE
	@Path("{distributorID}/{itemNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public WishlistItem removeWishlistItem(@PathParam("distributorID") String distributorID,
			@PathParam("itemNumber") int itemNumber) {
		Controller controller = Controller.getInstance();
		WishlistItem item = controller.getWishlists().get(distributorID).removeItem(itemNumber);
		controller.saveWishlists();
		return item;
	}

	@GET
	@Path("notify")
	@Produces(MediaType.APPLICATION_JSON)
	public int notifyDistributors() {
		return Controller.getInstance().compareAndNotify();
	}

}
