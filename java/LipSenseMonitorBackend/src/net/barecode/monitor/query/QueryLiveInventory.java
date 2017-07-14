package net.barecode.monitor.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.barecode.monitor.pojo.wishlist.WishlistItem;

public class QueryLiveInventory {
	private final String distributorID;
	private final String distributorPassword;
	private final String automationEmailID;
	private final String automationEmailPassword;
	private final String notificationEmail;

	public QueryLiveInventory() {
		distributorID = getenv("SENEGENCE_DIST_ID");
		distributorPassword = getenv("SENEGENCE_DIST_PASS");
		automationEmailID = getenv("AUTOMATION_EMAIL_ID");
		automationEmailPassword = getenv("AUTOMATION_EMAIL_PASSWORD");
		notificationEmail = getenv("NOTIFICATION_EMAIL");
	}

	private String getenv(final String key) {
		String value = System.getenv(key);
		if (value == null) {
			System.out.println("Required environment variable not set: " + key);
			value = "NULL";
		}
		return value;
	}

	public String query() throws Exception {
		String result = queryProductList();
		if (result == null) {
			login();
			result = queryProductList();
		}
		return result;
	}

	private void login() throws Exception {
		CookieManager manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);

		String rawData = "{Dist_ID:'" + distributorID + "',Dist_Pass:'" + distributorPassword + "'}";
		URL loginURL = new URL("https://www.senegence.com/senegence/default.aspx/DistributorLogin");
		HttpURLConnection conn = (HttpURLConnection) loginURL.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Content-Length", String.valueOf(rawData.length()));
		OutputStream os = conn.getOutputStream();
		os.write(rawData.getBytes());

		InputStream is = conn.getInputStream();
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		System.out.println("Content:");
		System.out.println(result);
		s.close();
		is.close();

		System.out.println("Cookies:");
		CookieStore cs = manager.getCookieStore();
		List<HttpCookie> cookies = cs.getCookies();
		if (cookies.isEmpty()) {
			System.out.println("No cookies");
		} else {
			Iterator<HttpCookie> itr = cookies.iterator();
			while (itr.hasNext()) {
				HttpCookie c = itr.next();
				System.out.println(c.toString());
			}
		}
	}

	/**
	 * Query the ProductList.aspx.
	 * 
	 * @return If the login is valid, return the page HTML. If the login is
	 *         expired, return {@code null}.
	 * @throws Exception
	 */
	private String queryProductList() throws Exception {
		URL inventoryURL = new URL("https://www.senegence.com/SeneGenceWeb/WebOrdering/ProductList.aspx?d=332764&c=1&ot=1");
		HttpURLConnection conn = (HttpURLConnection) inventoryURL.openConnection();
		InputStream is = conn.getInputStream();
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		System.out.println("Content:");
		System.out.println(result);
		s.close();
		is.close();

		// DistributorIdMismatchError.aspx indicates bad login
		if (result.contains("DistributorIdMismatchError.aspx")) {
			result = null;
		}

		return result;
	}

}
