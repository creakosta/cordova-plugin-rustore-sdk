var exec = require('cordova/exec');
var argscheck = require('cordova/argscheck');

(function (global) {
	var RuStore = function () {}
	
	/**
	 * Open the app review & rating form
	 * @param onSuccess A callback that will be called if all went ok
	 * @param onError A callback that will be called if there's some error
	 */
	RuStore.prototype.openReviewForm = function (onSuccess, onError) {
		exec(onSuccess, onError, 'RuStorePlugin', 'openReviewForm', [])
	}
	
	/**
	 * Initialize the billing client to access the purchases functionality
	 * @param options A set of options containing the app config values
	 * @param onSuccess A callback that will be called if all went ok
	 * @param onError A callback that will be called if there's some error
	 */
	RuStore.prototype.initPurchases = function (options, onSuccess, onError) {
		argscheck.checkArgs('O', 'RuStore.initPurchases', arguments)
		
		if(!options) {
			if(onError) {
				onError('No options provided!')
			}
		}
		else {
			if(options.consoleApplicationId !== undefined && typeof options.consoleApplicationId != 'string') {
				if(onError) {
					onError("Invalid console application ID provided!")
				}
			}
			else if(options.consoleApplicationId === undefined || options.consoleApplicationId === "") {
				if(onError) {
					onError("No console application ID provided!")
				}
			}
			else if(options.deeplinkScheme !== undefined && typeof options.deeplinkScheme != 'string') {
				if(onError) {
					onError("Invalid deeplink scheme provided!")
				}
			}
			else if(options.deeplinkScheme === undefined || options.deeplinkScheme === "") {
				if(onError) {
					onError("No deeplink scheme provided!")
				}
			}
			else {
				exec(onSuccess, onError, 'RuStorePlugin', 'initPurchases', [options])
			}
		}
	}
	
	/**
	 * Check if purchases are available in this app
	 * @param onSuccess A callback that will be called if all went ok (and if the purchases are available)
	 * @param onError A callback that will be called if purchases are unavailable (and if there's some error)
	 */
	RuStore.prototype.checkPurchasesAvailability = function (onSuccess, onError) {
		exec(onSuccess, onError, 'RuStorePlugin', 'checkPurchasesAvailability', [])
	}
	
	/**
	 * Get all the available products that can be purchased in this app
	 * @param onSuccess A callback that will be called if all went ok (and the products list will be accessible through here)
	 * @param onError A callback that will be called if there's some error
	 */
	RuStore.prototype.getProducts = function (productIds, onSuccess, onError) {
		// TODO: argscheck?
		exec(onSuccess, onError, 'RuStorePlugin', 'getProducts', [productIds])
	}
	
	/**
	 * Get all the purchases of the current app user
	 * @param onSuccess A callback that will be called if all went ok (and the purchases list will be accessible through here)
	 * @param onError A callback that will be called if there's some error
	 */
	RuStore.prototype.getPurchases = function (onSuccess, onError) {
		exec(onSuccess, onError, 'RuStorePlugin', 'getPurchases', [])
	}
	
	/**
	 * Purchase the specified product
	 * @param product A product which needs to be purchased
	 * @param onSuccess A callback that will be called if all went ok
	 * @param onError A callback that will be called if there's some error
	 */
	RuStore.prototype.purchaseProduct = function (product, onSuccess, onError) {
		argscheck.checkArgs('*', 'RuStore.purchaseProduct', arguments)
		exec(onSuccess, onError, 'RuStorePlugin', 'purchaseProduct', [product])
	}

	 /**
          * Confirm the specified purchase
          * @param product A product which needs to be confirmed
          * @param onSuccess A callback that will be called if all went ok
          * @param onError A callback that will be called if there's some error
          */
         RuStore.prototype.confirmPurchase = function (product, onSuccess, onError) {
                 argscheck.checkArgs('S', 'RuStore.confirmPurchase', arguments)
                 exec(onSuccess, onError, 'RuStorePlugin', 'confirmPurchase', [product])
         }

         /**
          * Delete the specified purchase
          * @param product A product which needs to be deleted
          * @param onSuccess A callback that will be called if all went ok
          * @param onError A callback that will be called if there's some error
          */
         RuStore.prototype.deletePurchase = function (product, onSuccess, onError) {
                 argscheck.checkArgs('S', 'RuStore.deletePurchase', arguments)
                 exec(onSuccess, onError, 'RuStorePlugin', 'deletePurchase', [product])
         }
	
	module.exports = new RuStore()
})(window)
