package net.barecode.monitor.pojo.persistence;

import java.util.Collection;
import java.util.Iterator;

import javax.naming.InitialContext;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import net.barecode.monitor.pojo.wishlist.Wishlist;
import net.barecode.monitor.pojo.wishlist.WishlistItem;
import net.barecode.monitor.pojo.wishlist.Wishlists;

public class MongoPersistence {
	protected DB db;

	/**
	 * Load the Wishlists from Mongo.
	 * 
	 * @return Wishlists or {@code null} if an error occurred.
	 */
	public Wishlists load() {
		Wishlists loaded = null;
		try {
			Wishlists wishlists = new Wishlists();

			db = (DB) new InitialContext().lookup("java:comp/env/mongo/wishlist");
			DBCollection col = db.getCollection("wishlists");
			System.out.println("MongoDB has " + col.count() + " items in the wishlists.wishlists collection at load");

			DBCursor cursor = col.find();
			while (cursor.hasNext()) {
				Wishlist wishlist = deserializeWishlist((BasicDBObject)cursor.next());
				if (wishlist != null) {
					wishlists.put(wishlist.distributorID, wishlist);
				}
			}

			loaded = wishlists;
		} catch (Exception e) {
			System.out.println("Exception caught during load: " + e.getMessage());
			e.printStackTrace();
		}

		return loaded;
	}

	private Wishlist deserializeWishlist(BasicDBObject mWishlist) {
		if (!mWishlist.containsField("distributorID")) {
			System.out.println("Unknown object in the wishlists collection: " + mWishlist);
			return null;
		}
		
		Wishlist wishlist = new Wishlist(mWishlist.getString("distributorID"), mWishlist.getString("notificationEmail"));
		
		BasicDBList mList = (BasicDBList) mWishlist.get("list");
		Iterator itr = mList.iterator();
		while (itr.hasNext()) {
			BasicDBObject mItem = (BasicDBObject)itr.next();
			WishlistItem item = deserializeWishlistItem(mItem);
			if (item != null) {
				wishlist.list.add(item);
			}
		}
		
		return wishlist;
	}

	private WishlistItem deserializeWishlistItem(BasicDBObject mItem) {
		if (!mItem.containsField("itemNumber")) {
			System.out.println("Unknown object in the WishlistItem collection: " + mItem);
			return null;
		}
		WishlistItem item = new WishlistItem(mItem.getInt("itemNumber"));
		item.isNotified = mItem.getBoolean("isNotified");
		return item;
	}

	/**
	 * Store the Wishlists into mongo.
	 * 
	 * @param wishlists
	 */
	public void store(Wishlists wishlists) {
		try {
			db = (DB) new InitialContext().lookup("java:comp/env/mongo/wishlist");
			DBCollection col = db.getCollection("wishlists");
			for (Wishlist wishlist : wishlists.getLists()) {
				col.save(serialize(wishlist));
			}
		} catch (Exception e) {
			System.out.println("Exception caught during store: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Serialize a Wishlist to a Mongo DBObject.
	 * 
	 * @param wishlist
	 * @return
	 */
	private DBObject serialize(Wishlist wishlist) {
		DBObject obj = new BasicDBObject();
		obj.put("_id", wishlist.distributorID);
		obj.put("distributorID", wishlist.distributorID);
		obj.put("notificationEmail", wishlist.notificationEmail);
		obj.put("list", serialize(wishlist.readonlyList()));
		return obj;
	}

	/**
	 * Serialize a Collection of WishlistItem to a Mongo BasicDBList.
	 * 
	 * @param list
	 * @return
	 */
	private BasicDBList serialize(Collection<WishlistItem> list) {
		BasicDBList mList = new BasicDBList();
		for (WishlistItem item : list) {
			mList.add(serialize(item));
		}
		return mList;
	}

	/**
	 * Serialize a WishlistItem to a Mongo BasicDBObject.
	 * 
	 * @param item
	 * @return
	 */
	private BasicDBObject serialize(WishlistItem item) {
		BasicDBObject obj = new BasicDBObject();
		obj.put("itemNumber", item.itemNumber);
		obj.put("isNotified", item.isNotified);
		return obj;
	}

}
