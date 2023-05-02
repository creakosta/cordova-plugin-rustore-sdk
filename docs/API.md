# API

The list of available methods for this plugin with details is provided below

| method name | params | description |
|-------------|--------|-------------|
| [`openReviewForm`](#openReviewForm) | `(function success, function error)` | Open a form for app review |
| [`initPurchases`](#initPurchases) | `(Object options, function success, function error)` | Initialize the Purchases SDK |
| [`checkPurchasesAvailability`](#checkPurchasesAvailability) | `(function success, function error)` | Check purchases availability |
| [`getProducts`](#getProducts) | `(String[] productIds, function success, function error)` | Get all products list |
| [`getPurchases`](#getPurchases) | `(function success, function error)` | Get user purchases list |
| [`purchaseProduct`](#purchaseProduct) | `(Object data, function success, function error)` | Used to purchase a product on the list |
| [`deleteProduct`](#deleteProduct) | `(String product, function success, function error)` | Used to delete a product from the purchase list |
| [`confirmPurchase`](#confirmPurchase) | `(String purchase, function success, function error)` | Confirm a product's purchase |

---

##### <a id="openReviewForm"> **`openReviewForm(onSuccess, onError): void`**

open the review and rating form.

| parameter | type | description |
|-----------|------|-------------|
| `onSuccess` | `()=>void` | Success callback which will be called if everything executed fine |
| `onError` | `(message: string)=>void` | Error callback which will be called if there will be any error in the process |

*Example:*

```javascript
var onSuccess = function() {
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

| parameter | type | description |
|-----------|------|-------------|
| `options` | `Object` | SDK config options |
| `onSuccess` | `()=>void` | Success callback which will be called upon successful SDK initialization |
| `onError` | `(message: string)=>void` | Error callback which will be called if there will be any error during the initialization |

**`options`**

| name | type | description |
|------|------|-------------|
| consoleApplicationId | String | Application ID from the dev console |
| deeplinkScheme | String | Your deeplink scheme which should be the same as inside of your AppManifest.xml file |
| enableLogging | Boolean | Whether or not the IAP actions should be logged **(optional)** |

*Example:*

```javascript
var onSuccess = function() {
	// handle result
};

var onError = function(err) {
	// handle error
}

var options = {
	consoleApplicationId: '111111',
	deeplinkScheme: 'yourappscheme',
	enableLogging: false, // optional
};

window.plugins.ruStore.initPurchases(options, onSuccess, onError);
```

---

##### <a id="checkPurchasesAvailability"> **`checkPurchasesAvailability(onSuccess, onError): void`**

check is purchases are available.

| parameter | type | description |
|-----------|------|-------------|
| `onSuccess` | `(message: string)=>void` | Success callback which will be called if everything executed fine and indicating that purchases are available |
| `onError` | `(message: string)=>void` | Error callback which will be called if there will be any error in the process and indicating that purchases are unavailable |

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

##### <a id="getProducts"> **`getProducts(productIds, onSuccess, onError): void`**

request the list with all products.

| parameter | type | description |
|-----------|------|-------------|
| `productIds` | `String[]` | Array of strings with product names |
| `onSuccess` | `(products: Object[])=>void` | Success callback which will be called if everything executed fine and will contain an array of products |
| `onError` | `(message: string)=>void` | Error callback which will be called if there will be any error in the process |

The success callback will receive an array of product objects with the following fields (the fields marked as **(optional)** can be missing if they weren't specified for the particular product):

| name | type | description |
|------|------|-------------|
| `productId` | `String` | Product name |
| `productType` | `String` | Product type (one of the `ProductType` values) **(optional)** |
| `productStatus` | `String` | Product status (one of the `ProductStatus` values) |
| `productLabel` | `String` | Product label **(optional)** |
| `price` | `Number` | Product price **(optional)** |
| `currency` | `String` | Product currency **(optional)** |
| `language` | `String` | Product language **(optional)** |
| `title` | `String` | Product title **(optional)** |
| `description` | `String` | Product description **(optional)** |
| `imageUrl` | `String` | Product image url string **(optional)** |
| `promoImageUrl` | `String` | Product promotional image url string **(optional)** |
| `subscription` | `Object` | Product subscription data in case the product type is a subscription (see `ProductSubscription` below) **(optional)** |

**`ProductType`**

| type | description |
|------|-------------|
| `CONSUMABLE` | Can be purchased multiple times, represents things like crystals, for example |
| `NON-CONSUMABLE` | Can be purchased only once, for things like ad disabling |
| `SUBSCRIPTION` | Can be purchased for a time period, for things like streaming service subscription |

**`ProductStatus`**

| status | description |
|--------|-------------|
| `ACTIVE` | Product is active |
| `INACTIVE` | Product is inactive |

**`ProductSubscription`**

| name | type | description |
|------|------|-------------|
| `subscriptionPeriod` | `Object` | Subscription period (see `SubscriptionPeriod` below) **(optional)** |
| `freeTrialPeriod` | `Object` | Free trial period (see `SubscriptionPeriod` below) **(optional)** |
| `gracePeriod` | `Object` | Grace period (see `SubscriptionPeriod` below) **(optional)** |
| `introductoryPrice` | `String` | Formatted introductory price, including the currency sign **(optional)**) |
| `introductoryPriceAmount` | `String` | Introductory price amount (in min units) **(optional)** |
| `introductoryPricePeriod` | `Object` | Introductory price period (see `SubscriptionPeriod` below) **(optional)** |

**`SubscriptionPeriod`**

| name | type | description |
|------|------|-------------|
| `years` | `Number` | Number of years in the period |
| `months` | `Number` | Number of months in the period |
| `days` | `Number` | Number of days in the period |

*Example:*

```javascript
var onSuccess = function(products) {
	// handle result
	//console.log(products)
	//console.log(products[0].title)
};

var onError = function(err) {
	// handle error
}

let productIds = ['id1', 'id2'];

window.plugins.ruStore.getProducts(productIds, onSuccess, onError);
```

---

##### <a id="getPurchases"> **`getPurchases(onSuccess, onError): void`**

request the list with all user purchases.

| parameter | type | description |
|-----------|------|-------------|
| `onSuccess` | `(purchases: Object[])=>void` | Success callback which will be called if everything executed fine and will contain the purchases array |
| `onError` | `(message: string)=>void` | Error callback which will be called if there will be any error in the process |

The success callback will receive an array of purchase objects with the following fields (the fields marked as **(optional)** can be missing if they weren't specified for the particular purchase):

| name | type | description |
|------|------|-------------|
| `purchaseId` | `String` | Purchase ID **(optional)** |
| `productId` | `String` | Product name |
| `productType` | `String` | Product type (one of the `ProductType` values) **(optional)** |
| `invoiceId` | `String` | Invoide ID of the purchase **(optional)** |
| `description` | `String` | Purchase description **(optional)** |
| `language` | `String` | Purchase language **(optional)** |
| `purchaseTime` | `String` | Time the purchase was made (RFC 3339) **(optional)** |
| `orderId` | `String` | Order ID (UUID) of the purchase **(optional)** |
| `amountLabel` | `String` | Formatted purchase price label **(optional)** |
| `amount` | `Number` | Purchase price (in min units) **(optional)** |
| `currency` | `String` | Purchase currency code (ISO 4217) **(optional)** |
| `quantity` | `Number` | Purchase quantity **(optional)** |
| `purchaseState` | `String` | Current state of the purchase (one of the `PurchaseState` values) **(optional)** |
| `developerPayload` | `String` | Purchase developer payload string for server-side validation **(optional)** |
| `subscriptionToken` | `String` | Purchase subscription token **(optional)** |

**`ProductType`**

| type | description |
|------|-------------|
| `CONSUMABLE` | Can be purchased multiple times, represents things like crystals, for example |
| `NON-CONSUMABLE` | Can be purchased only once, for things like ad disabling |
| `SUBSCRIPTION` | Can be purchased for a time period, for things like streaming service subscription |

**`PurchaseState`**

| type | description |
|------|-------------|
| `CREATED` | The purchase was created but still doesn't have an invoice |
| `INVOICE CREATED` | Invoice was created but the purchase is still pending and either will be CANCELLED or CONFIRMED/PAID soon |
| `CONFIRMED` | Purchase was confirmed (for NON-CONSUMABLE and SUBSCRIPTION products) |
| `PAID` | Purchase was paid (for CONSUMABLE products) |
| `CANCELLED` | The purchase was cancelled |
| `CONSUMED` | The CONSUMABLE purchase was consumed |
| `CLOSED` | The purchase was closed |
| `TERMINATED` | The purchase was terminated |

*Example:*

```javascript
var onSuccess = function(purchases) {
	// handle result
	//console.log(purchases)
	//console.log(purchases[0]["quantity"])
};

var onError = function(err) {
	// handle error
}

window.plugins.ruStore.getPurchases(onSuccess, onError);
```

---

##### <a id="purchaseProduct"> **`purchaseProduct(data, onSuccess, onError): void`**

purchase the specified product.

| parameter | type | description |
|-----------|------|-------------|
| `data` | `Object` | The data about the product which needs to be purchased |
| `onSuccess` | `(response: Object)=>void` | Success callback which will be called if everything executed fine and will provide the response data |
| `onError` | Either `(message: string)=>void` or `(response: Object)=>void` | Error callback which will be called if there will be any error in the process |

**`data`**

The fields marked as **(optional)** can be skipped if unused

| name | type | description |
|------|------|-------------|
| `productId` | `String` | The product id which needs to be purchased |
| `orderId` | `String` | Order ID string **(optional)** |
| `quantity` | `Number` | The amount of products to buy **(optional)** |
| `developerPayload` | `String` | A string used for server-side validation **(optional)** |

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

var data = {
	productId: 'noads',
	orderId: 'someid', // optional
	quantity: 1, // optional
	developerPayload: 'abcd', // optional
};

window.plugins.ruStore.purchaseProduct(data, onSuccess, onError);
```

---

##### <a id="deleteProduct"> **`deleteProduct(product, onSuccess, onError): void`**

delete the specified product.

| parameter | type | description |
|-----------|------|-------------|
| `purchase` | `String` | The purchase id which needs to be deleted |
| `onSuccess` | `(response: Object)=>void` | Success callback which will be called if everything executed fine |
| `onError` | `(message: string)=>void` | Error callback which will be called if there will be any error in the process |

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

let product = 'id1';

window.plugins.ruStore.deleteProduct(product, onSuccess, onError);
```

---

##### <a id="confirmPurchase"> **`confirmPurchase(purchase, onSuccess, onError): void`**

confirm purchase for the specified purchase.

| parameter | type | description |
|-----------|------|-------------|
| `purchase` | `String` | The purchase id which needs to be confirmed |
| `onSuccess` | `(response: Objest)=>void` | Success callback which will be called if everything executed fine |
| `onError` | `(message: string)=>void` | Error callback which will be called if there will be any error in the process |

*Example:*

```javascript
var onSuccess = function(result) {
	// handle result
};

var onError = function(err) {
	// handle error
}

let purchase = 'id1';

window.plugins.ruStore.confirmPurchase(purchase, onSuccess, onError);
```

---