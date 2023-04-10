# API

The list of available methods for this plugin with details is provided below

| method name | params | description |
|-------------|--------|-------------|
| [`openReviewForm`](#openReviewForm) | `(function success, function error)` | Open a form for app review |
| [`initPurchases`](#initPurchases) | `(options, function success, function error)` | Initialize the Purchases SDK |
| [`checkPurchasesAvailability`](#checkPurchasesAvailability) | `(function success, function error)` | Check purchases availability |
| [`getProducts`](#getProducts) | `(function success, function error)` | Get all products list |
| [`getPurchases`](#getPurchases) | `(function success, function error)` | Get user purchases list |
| [`purchaseProduct`](#purchaseProduct) | `(String product, function success, function error)` | Used to purchase a product on the list |
| [`deleteProduct`](#deleteProduct) | `(String product, function success, function error)` | Used to delete a product from the purchase list |
| [`confirmPurchase`](#confirmPurchase) | `(String purchase, function success, function error)` | Confirm a product's purchase |

---

##### <a id="openReviewForm"> **`openReviewForm(onSuccess, onError): void`**

open the review and rating form.

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

window.plugins.ruStore.openReviewForm(onSuccess, onError);
```

---

##### <a id="initPurchases"> **`initPurchases(options, onSuccess, onError): void`**

initialize the purchases SDK.

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

var options = {
devKey: 'd3Ac9qPardVYZxfWmCspwL',
appId: '123456789',
isDebug: false,
onInstallConversionDataListener: true // optional
};

window.plugins.ruStore.initPurchasesSdk(options, onSuccess, onError);
```

---

##### <a id="checkPurchasesAvailability"> **`checkPurchasesAvailability(onSuccess, onError): void`**

check is purchases are available.

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

window.plugins.ruStore.checkPurchasesAvailability(onSuccess, onError);
```

---

##### <a id="getProducts"> **`getProducts(onSuccess, onError): void`**

request the list with all products.

*Example:*

```javascript
var onSuccess = function(products) {
	// handle result
};

var onError = function(err) {
	// handle error
}

window.plugins.ruStore.getProducts(onSuccess, onError);
```

---

##### <a id="getPurchases"> **`getPurchases(onSuccess, onError): void`**

request the list with all user purchases.

*Example:*

```javascript
var onSuccess = function(purchases) {
	// handle result
};

var onError = function(err) {
	// handle error
}

window.plugins.ruStore.getPurchases(onSuccess, onError);
```

---

##### <a id="purchaseProduct"> **`purchaseProduct(product, onSuccess, onError): void`**

purchase the specified product.

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

var product = '';

window.plugins.ruStore.purchaseProduct(product, onSuccess, onError);
```

---

##### <a id="deleteProduct"> **`deleteProduct(product, onSuccess, onError): void`**

delete the specified product.

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

var product = '';

window.plugins.ruStore.deleteProduct(product, onSuccess, onError);
```

---

##### <a id="confirmPurchase"> **`confirmPurchase(purchase, onSuccess, onError): void`**

apply/use the specified purchase.

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

var purchase = '';

window.plugins.ruStore.confirmPurchase(purchase, onSuccess, onError);
```

---