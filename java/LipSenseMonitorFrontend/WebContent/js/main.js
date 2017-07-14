/**
 * The index.html DOM has the following defined IDs:
 * - distributorIDSpan - <span> to contain the selected distributor ID
 * - distributorIDSelect - <select> to populate and select distributor ID
 * - updatedTimeSpan - <span> to contain how long ago the inventory was updated
 * - inventoryDiv - <div> to contain the generated inventory and wishlist view
 * - errorDiv - <div> to contain and errors worth reporting
 * 
 * There are no classes defined in index.html.
 * 
 * All other IDs or classes are defined within this file.
 * - class: inStock & outOfStock - used within the inventory table <td> for the availability column
 * - id: watchedSpan-itemNumber - used within the inventory table watched column, used to identify the image span
 * - class: notWatched & isWatched - used within the inventory table watched column, used to style the image span
 * - id: notifiedSpan-itemNumber - used within the inventory table notified column, used to identify the image span
 * - class: wasNotified - used within the inventory table notified column, used to style the image span
 */

/**
 * Define some useful global variables. I could is use namepsace, but I don't
 * care enough to do so.
 */
var activeDistributorID = '';
var inventoryRendered = false;
var wishlistOverlayed = false;

/**
 * Display an error in the designated error div. Error reports are intended to
 * be used in debugging / development.
 * 
 * @param message
 *            The message to display
 */
function displayError(message) {
	document.getElementById('errorDiv').innerHTML += message;
}

/**
 * Return the number of elapsed minutes represented by the elapsed milliseconds.
 * 
 * @returns A human friendly string containing the number of elapsed minutes
 */
function calculateMinutesFromMillis(milliseconds) {
	var seconds = Math.floor(milliseconds / 1000);
	var minutes = Math.floor(seconds / 60);
	if (minutes < 1) {
		return '<1 minute';
	} else if (minutes == 1) {
		return '1 minute';
	} else {
		return minutes + ' minutes';
	}
}

/**
 * Given the epoch (in millis) at the last inventory update, update the
 * updatedTimeSpan to show the number of elapsed minutes since it was updated.
 * 
 * @param epochAtLastUpdate
 * @returns
 */
function updatedTimeSpan(epochAtLastUpdate) {
	var e = document.getElementById('updatedTimeSpan');
	var nowEpoch = (new Date).getTime();
	e.innerHTML = calculateMinutesFromMillis(nowEpoch - epochAtLastUpdate);
}

/**
 * Overlay the wishlist for the active distributor onto the rendered inventory.
 * 
 * In order for this to work, both the populateDistributors AND the
 * populateInventory operations must have previously completed. This method is
 * therefore called from tryToOverlayWishlist, which will be called by both
 * operations and respond to some globals to ensure pre-mature execution does
 * not occur.
 */
function overlayWishlist() {
	if (!!!(activeDistributorID && inventoryRendered)) {
		console
				.log("Overlaying wishlist called before ready, waiting for next call");
		return;
	}

	if (wishlistOverlayed) {
		console.log("Wishlist already overlaid");
		return;
	}

	wishlistOverlayed = true;
	console.log("Overlaying wishlist for distributor " + activeDistributorID);

	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			var wishlist = JSON.parse(xhr.responseText);
			var list = wishlist.list;
			for (var i = 0; i < list.length; i++) {
				var item = list[i];
				setIsWatched(item.itemNumber);
				if (item.isNotified) {
					setNotified(item.itemNumber);
				}
			}
		}
	};
	xhr.open('GET', '/rest/wishlists/' + activeDistributorID);
	xhr.send();
}

/**
 * Populates the distributorIDSelect and distributorIDSpan elements. Sets the
 * activeDistributorID and distributorIDSpan to the first distributor in the
 * list.
 */
function populateDistributors() {
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			var distributors = JSON.parse(xhr.responseText);
			if (distributors.length == 0) {
				displayError("The returned distributor list was empty. XHR response: "
						+ xhr.responseText);
			}

			// Default to the first distributor in the list
			activeDistributorID = distributors[0];
			document.getElementById('distributorIDSpan').innerHTML = distributors[0];

			// Populate the selector
			var distSelect = document.getElementById('distributorIDSelect');
			for (var i = 0; i < distributors.length; i++) {
				var distID = distributors[i];
				distSelect.innerHTML += '<option value="' + distID + '">'
						+ distID + '</option>';
			}

			overlayWishlist();
		}
	};
	xhr.open('GET', '/rest/wishlists/distributors');
	xhr.send();
}

/**
 * Starts an invetory category in the DOM.
 * 
 * @param name
 *            The category name
 * @returns The opening HTML for a category
 */
function startCategory(name) {
	return '<div><h2>'
			+ name
			+ '</h2><table><thead><tr><th class="itemNum">Item #</th><th align="left" class="description">Description</th><th>Availability</th><th class="watched">Watched?</th><th>Notified?</th></tr></thead><tbody>';
}

/**
 * Ends an inventory category in the DOM.
 * 
 * @returns The closing HTML for a category
 */
function endCategory() {
	return '</tbody></table></div>';
}

/**
 * Creates the HTML for the number column in the inventory table.
 * 
 * @param item
 * @returns
 */
function createNumberCol(item) {
	return '<td class="itemNum">' + item.itemNumber + '</td>';
}

/**
 * Creates the HTML for the description column in the inventory table.
 * 
 * @param item
 * @returns
 */
function createDescriptionCol(item) {
	return '<td align="left" class="description">' + item.name + '</td>';
}

/**
 * Creates the HTML for the availability column in the inventory table.
 * 
 * @param item
 * @returns
 */
function createAvailabilityCol(item) {
	var stockClass = (item.isInStock ? 'inStock' : 'outOfStock');
	var stockText = (item.isInStock ? 'In Stock' : 'Out of Stock');
	return '<td class="stock ' + stockClass + '">' + stockText + '</td>';
}

/**
 * Constructs the watchedSpan ID for the item number.
 * 
 * @param itemNumber
 * @returns
 */
function getWatchedSpanID(itemNumber) {
	return 'watchedSpan-' + itemNumber;
}

/**
 * Returns the watchedSpan element for the item number.
 * 
 * @param itemNumer
 * @returns
 */
function getWatchedSpanElement(itemNumer) {
	return document.getElementById(getWatchedSpanID(itemNumer));
}

/**
 * Updates the visual indication of itemNumber to be watched
 * 
 * @param itemNumber
 */
function setIsWatched(itemNumber) {
	var ele = getWatchedSpanElement(itemNumber);
	ele.classList.remove('notWatched');
	ele.classList.add('isWatched');
}

/**
 * Updates the visual indication of itemNumber to be unwatched
 * 
 * @param itemNumber
 */
function setNotWatched(itemNumber) {
	var ele = getWatchedSpanElement(itemNumber);
	ele.classList.remove('isWatched');
	ele.classList.add('notWatched');
}

/**
 * Determines if the itemNumber is considered to be watched (i.e. it has the isWatched
 * class).
 * 
 * @param itemNumber
 */
function isWatched(itemNumber) {
	return getWatchedSpanElement(itemNumber).classList.contains('isWatched');
}

/**
 * Toggles the given itemNumber to between isWatched and notWatched
 * classes.
 * 
 * @param itemNumber
 */
function toggleIsWatched(itemNumber) {
	if (isWatched(itemNumber)) {
		setNotWatched(itemNumber);
	} else {
		setIsWatched(itemNumber);
	}
}

/**
 * Drive the REST API to unwatch the item.
 * 
 * @param itemNumber
 */
function unwatch(itemNumber) {
	console.log("Unwatch " + itemNumber);

	var xhr = new XMLHttpRequest();
	xhr.open('DELETE', '/rest/wishlists/' + activeDistributorID + '/'
			+ itemNumber);
	xhr.send();
}

/**
 * Drive the REST API to watch the item
 * 
 * @param itemNumber
 */
function watch(itemNumber) {
	console.log("Watch " + itemNumber);

	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/rest/wishlists/' + activeDistributorID);
	xhr.setRequestHeader('Content-Type', 'application/json');
	xhr.send(itemNumber);
}

/**
 * Processes a click on the watchedSpan to change the visual indicator as well
 * as the change (via a REST API call) stored watched status.
 * 
 * @param itemNumber
 */
function processWatchedClick(itemNumber) {
	if (isWatched(itemNumber)) {
		clearNotified(itemNumber);
		unwatch(itemNumber);
	} else {
		watch(itemNumber);
	}
	toggleIsWatched(itemNumber);
}

/**
 * Creates the HTML for the watched column in the inventory table.
 * 
 * @param item
 * @returns
 */
function createWatchedCol(item) {
	var spanID = 'watchedSpan-' + item.itemNumber;
	return '<td class="watched" align="center"><span id="' + spanID
			+ '" class="notWatched" onclick="processWatchedClick('
			+ item.itemNumber
			+ ')" title="Click to toggle watch state"></span></td>';
}

/**
 * Constructs the notifiedSpan ID for the item number.
 * 
 * @param itemNumber
 * @returns
 */
function getNotifiedSpanID(itemNumber) {
	return 'notifiedSpan-' + itemNumber;
}

/**
 * Returns the notifiedSpan element for the item number.
 * 
 * @param itemNumer
 * @returns
 */
function getNotifiedSpanElement(itemNumer) {
	return document.getElementById(getNotifiedSpanID(itemNumer));
}

/**
 * Determines if the id is considered to be notified (i.e. it has the
 * wasNotified class).
 * 
 * @param itemNumber
 */
function isNotified(itemNumber) {
	return getNotifiedSpanElement(itemNumber).classList.contains('wasNotified');
}

/**
 * Adds the wasNotified class to the element.
 * 
 * @param itemNumber
 */
function setNotified(itemNumber) {
	getNotifiedSpanElement(itemNumber).classList.add('wasNotified');
}

/**
 * Clear the notifiedSpan element of the visual indicator, and drive the REST
 * API to clear the flag.
 * 
 * @param itemNumber
 */
function clearNotified(itemNumber) {
	getNotifiedSpanElement(itemNumber).classList.remove('wasNotified');

	var xhr = new XMLHttpRequest();
	xhr.open('PUT', '/rest/wishlists/' + activeDistributorID + '/' + itemNumber);
	xhr.send();
}

/**
 * Processes a click on the notifiedSpan to change the visual indicator as well
 * as the change (via a REST API call) stored notified status.
 * 
 * @param itemNumber
 *            The itemNumber that was clicked
 */
function processNotifiedClick(itemNumber) {
	if (isNotified(itemNumber)) {
		clearNotified(itemNumber);
	}
}

/**
 * Creates the HTML for the notified column in the inventory table.
 * 
 * @param item
 * @returns
 */
function createNotifiedCol(item) {
	return '<td align="center"><span id="' + getNotifiedSpanID(item.itemNumber)
			+ '" onclick="processNotifiedClick(' + item.itemNumber
			+ ')" title="Click to be notified again"></span></td>';
}

/**
 * Creates the HTML to represent the inventory items.
 * 
 * @param items
 *            The array of inventory category items
 * @returns The HTML representing the inventory items to add to the DOM
 */
function addCategoryItems(items) {
	var itemsHTML = '';
	for (var j = 0; j < items.length; j++) {
		var item = items[j];

		var tableRow = '<tr>';
		tableRow += createNumberCol(item);
		tableRow += createDescriptionCol(item);
		tableRow += createAvailabilityCol(item);
		tableRow += createWatchedCol(item);
		tableRow += createNotifiedCol(item);
		tableRow += '</tr>';
		itemsHTML += tableRow;
	}
	return itemsHTML;
}

/**
 * Builds the inventory table in the DOM.
 * 
 * The following ID and class conventions are used: -
 */
function buildInventory(inventory) {
	var newHTML = '';
	for (var i = 0; i < inventory.categories.length; i++) {
		var category = inventory.categories[i];

		newHTML += startCategory(category.name);
		newHTML += addCategoryItems(category.items);
		newHTML += endCategory();
	}

	document.getElementById('inventoryDiv').innerHTML = newHTML;
	inventoryRendered = true;
}

/**
 * Populates the inventoryDiv.
 */
function populateInventory() {
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			var inv = JSON.parse(xhr.responseText);
			updatedTimeSpan(inv.epochAtLastUpdate);
			buildInventory(inv);
			overlayWishlist();
		}
	};
	xhr.open('GET', '/rest/inventory');
	xhr.send();
}

/**
 * Render the inventory (hooked into window.onload)
 */
function renderInventory() {
	console.log("Rendering inventory");
	populateDistributors();
	populateInventory();
}

window.onload = renderInventory;
