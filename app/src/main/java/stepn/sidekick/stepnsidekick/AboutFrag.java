package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.MainActivity.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about the app, 'remove ads' button, option to donate
 *
 * @author Bob Godfrey
 * @version 1.3.2 Updated app layout to use fragments instead of activities for menu items
 *
 */

public class AboutFrag extends Fragment {

    Button emailButton, solButton, bnbButton;
    ImageButton removeAdsButton;
    ImageView removeAdsShadow;
    TextView emailTextView, removeAdsTextView, removeAdsShadowTextView;
    ClipboardManager clipboard;

    private BillingClient billingClient;
    private ProductDetails productDetails;
    private Purchase purchase;

    // TODO remove
    String TAG = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";

    public AboutFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (ads) {
            billingSetup();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        emailButton = view.findViewById(R.id.emailButton);
        solButton = view.findViewById(R.id.solButton);
        bnbButton = view.findViewById(R.id.bnbButton);
        emailTextView = view.findViewById(R.id.contactEmailTextView);

        removeAdsButton = view.findViewById(R.id.removeAdsButton);
        removeAdsTextView = view.findViewById(R.id.removeAdsTextView);
        removeAdsShadow = view.findViewById(R.id.removeAdsShadowButton);
        removeAdsShadowTextView = view.findViewById(R.id.removeAdsShadowTextView);

        if (ads) {
            removeAdsButton.setVisibility(View.VISIBLE);
            removeAdsShadow.setVisibility(View.VISIBLE);
            removeAdsTextView.setVisibility(View.VISIBLE);
            removeAdsShadowTextView.setVisibility(View.VISIBLE);
        } else {
            removeAdsButton.setVisibility(View.GONE);
            removeAdsShadow.setVisibility(View.GONE);
            removeAdsTextView.setVisibility(View.GONE);
            removeAdsShadowTextView.setVisibility(View.GONE);
        }

        removeAdsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        removeAdsButton.setVisibility(View.INVISIBLE);
                        removeAdsTextView.setVisibility(View.INVISIBLE);
                        removeAdsShadow.setImageResource(R.drawable.start_button);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        removeAdsButton.setVisibility(View.VISIBLE);
                        removeAdsTextView.setVisibility(View.VISIBLE);
                        removeAdsShadow.setImageResource(R.drawable.start_button_shadow);
                        break;
                }
                return false;
            }
        });

        removeAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePurchase(view);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.contact_email));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), getString(R.string.email_copied), Toast.LENGTH_SHORT).show();
            }
        });

        emailButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        emailTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.energy_blue_darker));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        emailTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.luck_socket_border));
                        break;
                }
                return false;
            }
        });

        solButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.solana_chain));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), getString(R.string.sol_address_copied), Toast.LENGTH_SHORT).show();
            }
        });

        solButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        solButton.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        solButton.setAlpha(0.0f);
                        break;
                }
                return false;
            }
        });

        bnbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.bnb_chain));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getString(R.string.bnb_address_copied), Toast.LENGTH_SHORT).show();
            }
        });

        bnbButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bnbButton.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        bnbButton.setAlpha(0.0f);
                        break;
                }
                return false;
            }
        });

        return view;
    }

    private void billingSetup() {
        billingClient = BillingClient.newBuilder(requireActivity())
                .setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(
                    @NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() ==
                        BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "OnBillingSetupFinish connected");
                    queryProduct();
                } else {
                    Log.i(TAG, "OnBillingSetupFinish failed");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i(TAG, "OnBillingSetupFinish connection lost");
            }
        });

    }

    private void queryProduct() {
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("remove_ads")
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(
                            @NonNull BillingResult billingResult, @NonNull List<ProductDetails> productDetailsList) {
                        if (!productDetailsList.isEmpty()) {
                            productDetails = productDetailsList.get(0);
                        } else {
                            Log.i(TAG, "onProductDetailsResponse: No products");
                        }
                    }
                }
        );
    }

    public void makePurchase(View view) {
        if (productDetails != null) {
            BillingFlowParams billingFlowParams =
                    BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(ImmutableList.of(BillingFlowParams
                                    .ProductDetailsParams.newBuilder().setProductDetails(productDetails).build()))
                            .build();

            billingClient.launchBillingFlow(requireActivity(), billingFlowParams);
        } else {
            Log.d(TAG, "makePurchase: oopsie daisy tehehe :)");
        }
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult,
                                       List<Purchase> purchases) {

            if (billingResult.getResponseCode() ==
                    BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    completePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() ==
                    BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.i(TAG, "onPurchasesUpdated: Purchase Canceled");
            } else {
                Log.i(TAG, "onPurchasesUpdated: Error");
            }
        }
    };

    private void completePurchase(Purchase item) {

        purchase = item;

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            ads = false;
    }




    /*
    billingClient = BillingClient.newBuilder(requireActivity()).setListener(new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                    // TODO
                }
            }).enablePendingPurchases().build();

            connectGooglePlayBilling();


    // connect to google billing
    private void connectGooglePlayBilling() {
        Log.d(TAG, "connectGooglePlayBilling: ");
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getProducts();
                }
            }
        });
    }

    @Override
    public void onResume() {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                        // check billingResult
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (list.get(0).getPurchaseState() == Purchase.PurchaseState.PURCHASED && !list.get(0).isAcknowledged()) {
                                verifyPayment(list.get(0));
                            }
                        }
                    }
                }
        );

        super.onResume();
    }



    // get list of products from google play (there is only one)
    private void getProducts() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        productList.add(QueryProductDetailsParams.Product.newBuilder().setProductId("remove_ads").setProductType(BillingClient.ProductType.INAPP).build());


        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(productList).build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                            Log.d(TAG, "onProductDetailsResponse: removeAddProductDetails.get(0).getTitle: " + productDetailsList);

                            ProductDetails removeAdsProductDetails = productDetailsList.get(0);

                            Log.d(TAG, "onProductDetailsResponse: descr: " + removeAdsProductDetails.getDescription());

                            List<BillingFlowParams.ProductDetailsParams> produx = new ArrayList<>();
                            produx.add(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetailsList.get(0)).build());

                            billingClient.launchBillingFlow(MainActivity.this, BillingFlowParams.newBuilder().setProductDetailsParamsList(produx).build());

                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(produx).build();

                        }
                    }
                }
        );
    }

    private void verifyPayment(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            ads = false;
                        } else {
                            ads = true;
                        }
                    }
                });
            }
        }
    }

     */

}