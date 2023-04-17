package org.apache.cordova.plugin

import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CallbackContext

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import android.os.Bundle
import android.content.Intent
import android.app.Application

//import ru.rustore.sdk.core.tasks.Task
import ru.rustore.sdk.core.tasks.OnCompleteListener
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult

import ru.vk.store.sdk.review.RuStoreReviewManager
import ru.vk.store.sdk.review.RuStoreReviewManagerFactory

import ru.vk.store.sdk.review.model.ReviewInfo

import ru.rustore.sdk.billingclient.RuStoreBillingClient

import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.product.ProductType
import ru.rustore.sdk.billingclient.model.product.ProductsResponse

import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.model.purchase.PaymentFinishCode

import ru.rustore.sdk.billingclient.model.purchase.response.PurchasesResponse
import ru.rustore.sdk.billingclient.model.purchase.response.DeletePurchaseResponse
import ru.rustore.sdk.billingclient.model.purchase.response.ConfirmPurchaseResponse

class RuStorePlugin : CordovaPlugin() {
  lateinit var app: Application
  
  lateinit var reviewManager: RuStoreReviewManager
  
  var userPurchases: List<Purchase>? = null
  
  /**
  * Called when initializing the plugin
  */
  override fun pluginInitialize() {
    super.pluginInitialize()
	
	this.app = this.cordova.getActivity().getApplication()
	
	var context = this.cordova.getActivity().getContext()
	this.reviewManager = RuStoreReviewManagerFactory.Create(context) // TODO: context of what?
  }
  
  /**
  * Called when executing an action from the JS code
  *
  * @param action A string naming the action
  * @param args JSON array containing all the arguments this action was called with
  * @param callbackContext The callback context used when calling back into JS code
  * @return true if success, false otherwise
  */
  override fun execute(
    action: String,
    args: JSONArray,
    callbackContext: CallbackContext
  ): Boolean {
	// Reviews
	if(action == "openReviewForm") {
		openReviewForm(callbackContext)
		return true
	}
	// Purchases
	else if(action == "initPurchases") { // TODO: initBillingClient?
	  initBillingClient(args, callbackContext)
	  return true
	}
	else if(action == "checkPurchasesAvailability") {
	  checkPurchasesAvailability(callbackContext)
	  return true
	}
	else if(action == "getProducts") {
		getProducts(args, callbackContext)
		return true
	}
	else if(action == "getPurchases") {
	  getPurchases(args, callbackContext)
	  return true
	}
	else if(action == "purchaseProduct") {
	  purchaseProduct(args, callbackContext)
	  return true
	}
	//else if(action == "deletePurchase") {
	  //deletePurchase(args, callbackContext)
	  //return true
	//}
	//else if(action == "confirmPurchase") {
	  //confirmPurchase(args, callbackContext)
	  //return true
	//}

    return false
  }
  
  /**
  * Called when the activity is starting (= becoming visible to the user)
  */
  //override fun onCreate() {
	//super.onCreate()
  //}
  
  /**
  * Called when the activity is starting (= becoming visible to the user)
  *
  * @param savedInstanceState A bundle filled with saved state (if present)
  */
  override fun onCreate(savedInstanceState: Bundle?) {
	super.onCreate(savedInstanceState)
	if(savedInstanceState == null)
		RuStoreBillingClient.onNewIntent(intent) // TODO: wut?
  }
  
  /**
  * Called when the activity receives a new intent
  *
  * @param intent The new intent
  */
  override fun onNewIntent(intent: Intent?) {
	super.onNewIntent(intent)
	RuStoreBillingClient.onNewIntent(intent)
  }
  
  /**
  * Called to open the app review form which allows to leave a rating and a
  * comment about the app without quitting it. The user can rate the app
  * from 1 to 5 and leave an optional review
  *
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun openReviewForm(callbackContext: CallbackContext) {
	  // Call requestReviewFlow before calling the launchReviewFlow in order to prepare the necessary info to display the screen
	  reviewManager.requestReviewFlow().addOnCompleteListener(object: OnCompleteListener<ReviewInfo> {
		  override fun onSuccess(result: ReviewInfo) {
			  // Save the received review info for further interaction
			  // NOTE: the review info has a lifespan of ~5 mins
			  val reviewInfo = result
			  
			  // Actually open the review and rating form
			  // NOTE: after the interaction with the review form is complete it's not recommended to display any other forms related to review or rating, no matter which the result (either onSuccess or onFailure)
			  // NOTE: the frequent calls to launchReviewFlow won't actually display the form with the same frequency as that is controlled on the RuStore side
			  reviewManager.launchReviewFlow(reviewInfo).addOnCompleteListener(object: OnCompleteListener<Unit> {
				  override fun onSuccess(result: Unit) {
					  // Review flow has finished, continue the app flow
					  callbackContext.success()
				  }
				  
				  override fun onFailure(throwable: Throwable) {
					  // Review flow has finished, continue the app flow
					  callbackContext.error()
				  }
			  })
		  }
		  
		  override fun onFailure(throwable: Throwable) {
			  // NOTE: it is not recommended to display an error to the user here as he's not the one who launched the process
			  callbackContext.error("Failed to open the review form! (" + throwable.toString() + ")")
		  }
	  })
  }
  
  /**
  * Called to initialize the billing client
  *
  * @param args JSON array containing all the arguments this action was called with
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun initBillingClient(args: JSONArray, callbackContext: CallbackContext) { // TODO: initSdk? initPurchases?
	RuStoreBillingClient.init(
		application = this.app,
		consoleApplicationId = args.getString(0),
		deeplinkScheme = args.getString(1)
	)
	callbackContext.success()
  }
  
  /**
  * Called to check if purchases are available for the user
  *
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun checkPurchasesAvailability(callbackContext: CallbackContext) {
	  RuStoreBillingClient.purchases.checkPurchasesAvailability()
	  .addOnCompleteListener(object: OnCompleteListener<FeatureAvailabilityResult> {
		  override fun onSuccess(result: FeatureAvailabilityResult) {
			  when(result) {
				  is FeatureAvailabilityResult.Available -> {
					  callbackContext.success("Purchases availability check: Available")
				  }
				  is FeatureAvailabilityResult.Unavailable -> { // TODO: RuStoreException
					  callbackContext.error("Purchases availability check: Unavailable")
				  }
			  }
		  }
		  
		  override fun onFailure(throwable: Throwable) {
			  callbackContext.error("Purchases availability check failed! (" + throwable.toString() + ")")
		  }
	  })
  }
  
  /**
  */
  // TODO: private fun isBillingClientInitialized(args: JSONArray, callbackContext: CallbackContext)?
  
  /**
  * Called to get the list of app's products
  *
  * @param args JSON array containing all the arguments this action was called with
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun getProducts(args: JSONArray, callbackContext: CallbackContext) {
	  val productIds = List<String>(args.length()) {
		args.getString(it)
	  }
	  
	  RuStoreBillingClient.products.getProducts(productIds)
	  .addOnCompleteListener(object: OnCompleteListener<ProductsResponse> {
		  override fun onSuccess(result: ProductsResponse) {
			  if(result.products == null) {
				callbackContext.error("Failed to load the products list!" + result.errorMessage ? " (Code " + result.code + " : " + result.errorMessage + result.errorDescription ? " - " + result.errorDescription : "" + ")" : "")
			}
			
			var products = JSONArray()
			val product = JSONObject()
			result.products.forEach {
				product.put("productId", it.productId)
				
				it.productType?.let {
					// Possible types:
					// CONSUMABLE - can be purchased multiple times, represents things like crystals, for example
					// NON-CONSUMABLE - can be purchased only once, for things like ad disabling
					// SUBSCRIPTION - can be purchased for a time period, for things like streaming service subscription
					when(it.productType?) {
						is ProductType.CONSUMABLE -> {
							product.put("productType", "CONSUMABLE")
						}
						is ProductType.NON_CONSUMABLE -> {
							product.put("productType", "NON-CONSUMABLE")
						}
						is ProductType.SUBSCRIPTION -> {
							product.put("productType", "SUBSCRIPTION")
						}
					}
				}
				
				//product.put("productStatus", it.productStatus) // ProductStatus // TODO
				
				it.priceLabel?.let {
					product.put("priceLabel", it.priceLabel?)
				}
				
				it.price?.let {
					product.put("price", it.price?)
				}
				
				it.currency?.let {
					product.put("currency", it.currency?)
				}
				
				it.language?.let {
					product.put("language", it.language?)
				}
				
				it.title?.let {
					product.put("title", it.title?)
				}
				
				it.description?.let {
					product.put("description", it.description?)
				}
				
				it.imageUrl?.let {
					product.put("imageUrl", it.imageUrl?.toString())
				}
				
				it.promoImageUrl?.let {
					product.put("promoImageUrl", it.promoImageUrl?.toString())
				}
				
				it.subscription?.let {
					val sub = JSONObject()
					
					it.subscription?.subscriptionPeriod?.let {
						val period = JSONObject()
						period.put("years", it.subscription?.subscriptionPeriod?.years)
						period.put("months", it.subscription?.subscriptionPeriod?.months)
						period.put("days", it.subscription?.subscriptionPeriod?.days)
						sub.put("subscriptionPeriod", period)
					}
					
					it.subscription?.freeTrialPeriod?.let {
						val period = JSONObject()
						period.put("years", it.subscription?.freeTrialPeriod?.years)
						period.put("months", it.subscription?.freeTrialPeriod?.months)
						period.put("days", it.subscription?.freeTrialPeriod?.days)
						sub.put("freeTrialPeriod", period)
					}
					
					it.subscription?.gracePeriod?.let {
						val period = JSONObject()
						period.put("years", it.subscription?.gracePeriod?.years)
						period.put("months", it.subscription?.gracePeriod?.months)
						period.put("days", it.subscription?.gracePeriod?.days)
						sub.put("gracePeriod", period)
					}
					
					it.subscription?.introductoryPrice?.let {
						sub.put("introductoryPrice", it.subscription?.introductoryPrice?)
					}
					
					it.subscription?.introductoryPriceAmount?.let {
						sub.put("introductoryPriceAmount", it.subscription?.introductoryPriceAmount?)
					}
					
					it.subscription?.introductoryPricePeriod?.let {
						val period = JSONObject()
						period.put("years", it.subscription?.introductoryPricePeriod?.years)
						period.put("months", it.subscription?.introductoryPricePeriod?.months)
						period.put("days", it.subscription?.introductoryPricePeriod?.days)
						sub.put("introductoryPricePeriod", period)
					}
					
					product.put("subscription", sub)
				}
				
				products.put(product)
			}
			callbackContext.success(products)
		  }
		  
		  override fun onFailure(throwable: Throwable) {
			  callbackContext.error("Failed to get the products! (" + throwable.toString() + ")")
		  }
	  })
  }
  
  /**
  * Called to get the list of app's purchases
  *
  * @param args JSON array containing all the arguments this action was called with
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun getPurchases(args: JSONArray, callbackContext: CallbackContext) {
	// TODO: check if initialized?
	
	// TODO: no args
	
	//val purchaseId = args.getString(0)
	
	//if(purchaseId.isEmpty())
		//callbackContext.error("Empty purchase ID provided!")
	
	RuStoreBillingClient.purchases.getPurchases()
	.addOnCompleteListener(object: OnCompleteListener<PurchasesResponse> {
		override fun onSuccess(result: PurchasesResponse) {
			if(result.purchases == null) {
				callbackContext.error("Failed to load the purchases list!" + result.errorMessage ? " (Code " + result.code + " : " + result.errorMessage + result.errorDescription ? " - " + result.errorDescription : "" + ")" : "")
			}
			
			userPurchases = result.purchases
			
			var purchases = JSONArray()
			val purchase = JSONObject()
			
			// TODO: purchases can be null here
			
			result.purchases?.forEach {
				it.purchaseId?.let {
					purchase.put("purchaseId", it.purchaseId?)
				}
				
				purchase.put("productId", it.productId)
				
				it.productType?.let {
					// Possible types:
					// CONSUMABLE - can be purchased multiple times, represents things like crystals, for example
					// NON-CONSUMABLE - can be purchased only once, for things like ad disabling
					// SUBSCRIPTION - can be purchased for a time period, for things like streaming service subscription
					when(it.productType?) {
						is ProductType.CONSUMABLE -> {
							purchase.put("productType", "CONSUMABLE")
						}
						is ProductType.NON_CONSUMABLE -> {
							purchase.put("productType", "NON-CONSUMABLE")
						}
						is ProductType.SUBSCRIPTION -> {
							purchase.put("productType", "SUBSCRIPTION")
						}
					}
				}
				
				it.invoiceId?.let {
					purchase.put("invoiceId", it.invoiceId?)
				}
				
				it.description?.let {
					purchase.put("description", it.description?)
				}
				
				it.language?.let {
					purchase.put("language", it.language?)
				}
				
				it.purchaseTime?.let {
					purchase.put("purchaseTime", it.purchaseTime?.toUTCString())
				}
				
				it.orderId?.let {
					purchase.put("orderId", it.orderId?)
				}
				
				it.amountLabel?.let {
					purchase.put("amountLabel", it.amountLabel?)
				}
				
				it.amount?.let {
					purchase.put("amount", it.amount?)
				}
				
				it.currency?.let {
					purchase.put("currency", it.currency?)
				}
				
				it.quantity?.let {
					purchase.put("quantity", it.quantity?)
				}
				
				//it.purchaseState?.let {
					//purchase.put("purchaseState", it.purchaseState?) // PurchaseState // TODO
				//}
				
				it.developerPayload?.let {
					purchase.put("developerPayload", it.developerPayload?)
				}
				
				it.subscriptionToken?.let {
					purchase.put("subscriptionToken", it.subscriptionToken?)
				}
				
				purchases.put(purchase)
			}
			callbackContext.success(purchases)
		}
		
		override fun onFailure(throwable: Throwable) {
			userPurchases = null
			callbackContext.error("Failed to get the purchases! (" + throwable.toString() + ")")
		}
	})
  }
  
  /**
  * Called to make a purchase
  *
  * @param args JSON array containing all the arguments this action was called with
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun purchaseProduct(args: JSONArray, callbackContext: CallbackContext) {
	// TODO: check if initialized?
	
	val productId = args.getString(0)
	val orderId = args.isNull(1) ? null : args.getString(1)
	val quantity = args.isNull(2) ? 1 : args.getInt(2)
	val developerPayload = args.isNull(3) ? null : args.getInt(3)
	
	if(productId.isEmpty())
		callbackContext.error("Empty product ID provided!")
	
	RuStoreBillingClient.purchases.purchaseProduct(productId, orderId, quantity, developerPayload)
	.addOnCompleteListener(object: OnCompleteListener<PaymentResult> {
		override fun onSuccess(result: PaymentResult) {
			val response = JSONObject()
			when(result) {
				// The purchase have not been completed
				is PaymentResult.InvoiceResult -> {
					var success = false
					
					response.put("invoiceId", result.invoiceId)
					
					/*
					Possible values of the finishCode:
					
					SUCCESSFUL_PAYMENT — sucessful payment
					CLOSED_BY_USER — cancelled by user
					UNHANDLED_FORM_ERROR — unknown error
					PAYMENT_TIMEOUT — an error due to timeout
					DECLINED_BY_SERVER — cancelled by server
					RESULT_UNKNOWN — unknown status
					*/
					when(result.finishCode) {
						PaymentFinishCode.SUCCESSFUL_PAYMENT -> {
							response.put("finishCode", "SUCCESSFUL_PAYMENT")
							success = true
						}
						PaymentFinishCode.CLOSED_BY_USER -> {
							response.put("finishCode", "CLOSED_BY_USER")
						}
						PaymentFinishCode.UNHANDLED_FORM_ERROR -> {
							response.put("finishCode", "UNHANDLED_FORM_ERROR")
						}
						PaymentFinishCode.PAYMENT_TIMEOUT -> {
							response.put("finishCode", "PAYMENT_TIMEOUT")
						}
						PaymentFinishCode.DECLINED_BY_SERVER -> {
							response.put("finishCode", "DECLINED_BY_SERVER")
						}
						PaymentFinishCode.RESULT_UNKNOWN -> {
							response.put("finishCode", "RESULT_UNKNOWN")
						}
					}
					
					if(success)
						callbackContext.success(response)
					else
						callbackContext.error(response)
				}
				
				// The purchase have been completed without the invoice specified (probably they were launched with incorrect one, like an empty string)
				is PaymentResult.InvalidInvoice -> {
					callbackContext.error("Failed to purchase the product - invalid invoice provided!" + result.invoiceId ? " (" + result.invoiceId + ")" : "")
				}
				
				// The purchase have been completed with some result
				is PaymentResult.PurchaseResult -> {
					var success = false
					
					/*
					Possible values of the finishCode:
					
					SUCCESSFUL_PAYMENT — sucessful payment
					CLOSED_BY_USER — cancelled by user
					UNHANDLED_FORM_ERROR — unknown error
					PAYMENT_TIMEOUT — an error due to timeout
					DECLINED_BY_SERVER — cancelled by server
					RESULT_UNKNOWN — unknown status
					*/
					when(result.finishCode) {
						PaymentFinishCode.SUCCESSFUL_PAYMENT -> {
							response.put("finishCode", "SUCCESSFUL_PAYMENT")
							if(userPurchases.get(userPurchases.indexOf(productId)).productType == ProductType.CONSUMABLE)
								RuStoreBillingClient.purchases.confirmPurchase(result.purchaseId)
							success = true
						}
						PaymentFinishCode.CLOSED_BY_USER -> {
							response.put("finishCode", "CLOSED_BY_USER")
							deletePurchase(result.purchaseId)
						}
						PaymentFinishCode.UNHANDLED_FORM_ERROR -> {
							response.put("finishCode", "UNHANDLED_FORM_ERROR")
							deletePurchase(result.purchaseId)
						}
						PaymentFinishCode.PAYMENT_TIMEOUT -> {
							response.put("finishCode", "PAYMENT_TIMEOUT")
							deletePurchase(result.purchaseId)
						}
						PaymentFinishCode.DECLINED_BY_SERVER -> {
							response.put("finishCode", "DECLINED_BY_SERVER")
							deletePurchase(result.purchaseId)
						}
						PaymentFinishCode.RESULT_UNKNOWN -> {
							response.put("finishCode", "RESULT_UNKNOWN")
							deletePurchase(result.purchaseId)
						}
					}
					
					response.put("orderId", result.orderId)
					response.put("purchaseId", result.purchaseId)
					response.put("productId", result.productId)
					
					result.subscriptionToken?.let {
						response.put("subscriptionToken", result.subscriptionToken)
					}
					
					if(success)
						callbackContext.success(response)
					else
						callbackContext.error(response)
				}
				
				// An error happened during the purchase
				is PaymentResult.InvalidPurchase -> {
					result.purchaseId?.let {
						response.put("purchaseId", result.purchaseId)
						deletePurchase(it, callbackContext) // TODO: result.purchaseId?
					}
					
					result.invoiceId?.let {
						response.put("invoiceId", result.invoiceId)
					}
					
					result.orderId?.let {
						response.put("orderId", result.orderId)
					}
					
					result.quantity?.let {
						response.put("quantity", result.quantity)
					}
					
					result.productId?.let {
						response.put("productId", result.productId)
					}
					
					result.errorCode?.let {
						response.put("errorCode", result.errorCode)
					}
					
					callbackContext.error(response)
				}
				
				// No payment state received during the payment
				is PaymentResult.InvalidPaymentState -> {
					callbackContext.error("Failed to purcase the product - No payment state received during the payment process!")
				}
			}
		}
		
		override fun onFailure(throwable: Throwable) {
			callbackContext.error("Failed to purchase the product! (" + throwable.toString() + ")")
		}
	})
  }
  
  /**
  * Called to delete the specified purchase
  *
  * @param args JSON array containing all the arguments this action was called with
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun deletePurchase(args: JSONArray, callbackContext: CallbackContext) {
	// TODO: check if initialized?
	
	val purchaseId = args.getString(0)
	
	if(purchaseId.isEmpty())
		callbackContext.error("Empty purchase ID provided!")
	
	RuStoreBillingClient.purchases.deletePurchase(purchaseId)
	.addOnCompleteListener(object: OnCompleteListener<DeletePurchaseResponse> {
		override fun onSuccess(result: DeletePurchaseResponse) {
			val response = JSONObject()
			
			result.meta?.let {
				val meta = JSONObject()
				meta.put("traceId", result.meta.traceId)
				response.put("meta", meta)
			}
			
			response.put("code", result.code)
			
			result.errorMessage?.let {
				response.put("errorMessage", result.errorMessage)
			}
			
			result.errorDescription?.let {
				response.put("errorDescription", result.errorDescription)
			}
			
			// TODO: errors field support?
			
			callbackContext.success(response)
		}
		
		override fun onFailure(throwable: Throwable) {
			callbackContext.error("Failed to delete/cancel the purchase! (" + throwable.toString() + ")")
		}
	})
  }
  
  /**
  * Called to confirm the specified purchase
  *
  * @param args JSON array containing all the arguments this action was called with
  * @param callbackContext The callback context used when calling back into JS code
  */
  private fun confirmPurchase(args: JSONArray, callbackContext: CallbackContext) {
	// TODO: check if initialized?
	
	val purchaseId = args.getString(0)
	val developerPayload = args.isNull(1) ? null : args.getString(1)
	
	if(purchaseId.isEmpty())
		callbackContext.error("Empty purchase ID provided!")
	
	RuStoreBillingClient.purchases.confirmPurchase(purchaseId, developerPayload)
	.addOnCompleteListener(object: OnCompleteListener<ConfirmPurchaseResponse> {
		override fun onSuccess(result: ConfirmPurchaseResponse) {
			val response = JSONObject()
			
			result.meta?.let {
				val meta = JSONObject()
				meta.put("traceId", result.meta.traceId)
				response.put("meta", meta)
			}
			
			response.put("code", result.code)
			
			result.errorMessage?.let {
				response.put("errorMessage", result.errorMessage)
			}
			
			result.errorDescription?.let {
				response.put("errorDescription", result.errorDescription)
			}
			
			// TODO: errors field support?
			
			callbackContext.success(response)
		}
		
		override fun onFailure(throwable: Throwable) {
			callbackContext.error("Failed to confirm/consume the purchase! (" + throwable.toString() + ")")
		}
	})
  }
}