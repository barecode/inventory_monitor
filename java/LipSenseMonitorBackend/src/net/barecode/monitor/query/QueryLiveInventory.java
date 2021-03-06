package net.barecode.monitor.query;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Query the live product inventory page and return the resulting HTML as a
 * String.
 * <p>
 * Initial requests will perform a login, and cache the resulting auth tokens.
 * If subsequent queries require re-authentication, one attempt (at most) will
 * be done.
 * 
 * @author barecode
 */
public class QueryLiveInventory {
	private final String distributorID;
	private final String distributorPassword;
	private final CookieManager manager;

	/**
	 * @param distributorID
	 *            SeneGence distributor ID
	 * @param distributorPassword
	 *            SeneGence distributor password
	 */
	public QueryLiveInventory(String distributorID, String distributorPassword) {
		this.distributorID = distributorID;
		this.distributorPassword = distributorPassword;
		this.manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
	}

	/**
	 * Query the live product inventory page and return the resulting HTML as a
	 * String.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String query() throws Exception {
		String result = queryProductList();
		if (result == null) {
			login();
			result = queryProductList();
		}
		return result;
	}

	private void login() throws Exception {
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
		s.close();
		is.close();

		if (result.contains("isDistributorPasswordCorrect===1")) {
			System.out.println("Login attempt successful");
		} else {
			System.out.println("Login failed for " + distributorID);
		}

		// debugCookies();
	}

	/**
	 * 
	 */
	private void debugCookies() {
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
		URL inventoryURL = new URL(
				"https://www.senegence.com/SeneGenceWeb/WebOrdering/ProductList.aspx?d=332764&c=1&ot=1");
		HttpURLConnection conn = (HttpURLConnection) inventoryURL.openConnection();
		InputStream is = conn.getInputStream();
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		s.close();
		is.close();

		// DistributorIdMismatchError.aspx indicates bad login
		if (result.contains("DistributorIdMismatchError.aspx")) {
			System.out.println("Looks like the login has expired");
			result = null;
		}

		return result;
	}

}
