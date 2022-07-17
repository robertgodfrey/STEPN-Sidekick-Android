package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity - Container for user to select which fragment to view (exercise, optimizer, or about)
 *
 * @author Bob Godfrey
 * @version 1.3.2 - Updated app layout to use fragments instead of activities for menu items
 *
 */

public class MainActivity extends AppCompatActivity {

    public static boolean ads;

    Button goToExerciseButton, goToOptimizerButton, goToInfoButton;
    ImageView exerciseSelected, optimizerSelected, infoSelected;
    AdView bannerAd;
    BillingClient billingClient;
    FragmentManager fragmentManager;

    ConstraintLayout bottomNav;
    ScrollView scrollView;

    // TODO remove
    String TAG = "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        billingClient = BillingClient.newBuilder(getApplicationContext()).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                for (Purchase purchase : list) {
                    verifyPayment(purchase);
                }
            }
        }).enablePendingPurchases().build();

        connectGooglePlayBilling();

        SharedPreferences getSharedPrefs = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        ads = getSharedPrefs.getBoolean(AD_PREF, false);

        buildUI();
    }

    // inits UI
    @SuppressLint("ClickableViewAccessibility")
    private void buildUI() {
        bannerAd = findViewById(R.id.bannerAd);
        scrollView = findViewById(R.id.scrollView);

        bottomNav = findViewById(R.id.navigationBar);
        goToExerciseButton = findViewById(R.id.goToExerciseButton);
        goToOptimizerButton = findViewById(R.id.goToOptimizerButton);
        goToInfoButton = findViewById(R.id.goToInfoButton);
        exerciseSelected = findViewById(R.id.runnerSelected);
        optimizerSelected = findViewById(R.id.optimizerSelected);
        infoSelected = findViewById(R.id.infoSelected);

        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        if (!ads) {
            bannerAd.setVisibility(View.GONE);
        } else {
            MobileAds.initialize(this);
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);
        }

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int scrollChange = oldScrollY - scrollY;
                if (scrollChange < -5 && bottomNav.getVisibility() == View.VISIBLE
                        && scrollView.getScrollY() > 0) {
                    bottomNav.startAnimation(slideDown);
                    bottomNav.setVisibility(View.INVISIBLE);
                } else if (scrollChange > 5
                        && bottomNav.getVisibility() == View.INVISIBLE
                        && scrollView.getChildAt(0).getBottom() > (scrollView.getHeight() + scrollView.getScrollY())) {
                    bottomNav.startAnimation(slideUp);
                    bottomNav.setVisibility(View.VISIBLE);
                }
            }
        });

        goToExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseSelected.setVisibility(View.VISIBLE);
                optimizerSelected.setVisibility(View.INVISIBLE);
                infoSelected.setVisibility(View.INVISIBLE);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, StartActivityFrag.class, null)
                        .setReorderingAllowed(false)
                        .commit();
            }
        });

        goToOptimizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseSelected.setVisibility(View.INVISIBLE);
                optimizerSelected.setVisibility(View.VISIBLE);
                infoSelected.setVisibility(View.INVISIBLE);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, OptimizerFrag.class, null)
                        .setReorderingAllowed(false)
                        .commit();
            }
        });

        goToInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseSelected.setVisibility(View.INVISIBLE);
                optimizerSelected.setVisibility(View.INVISIBLE);
                infoSelected.setVisibility(View.VISIBLE);

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, AboutFrag.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

    }

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

    // get list of products from google play (there is only one)
    private void getProducts() {
        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();

        productList.add(QueryProductDetailsParams.Product.newBuilder().setProductId("remove_ads").setProductType(BillingClient.ProductType.INAPP).build());
        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(productList).build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult, List<ProductDetails> productDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                            //Log.d(TAG, "onProductDetailsResponse: removeAddProductDetails.get(0).getTitle: " + productDetailsList);
                            ProductDetails removeAdsProductDetails = productDetailsList.get(0);

                            //Log.d(TAG, "onProductDetailsResponse: descr: " + removeAdsProductDetails.getDescription());

                            if (productDetailsList.isEmpty()) {
                                Toast.makeText(MainActivity.this, "empty array :(", Toast.LENGTH_SHORT).show();
                            }
                            for (ProductDetails productDetails : productDetailsList) {
                                if (productDetails.getProductId().equals("remove_ads")) {
                                    String test = productDetails.getName();
                                    if (test.isEmpty()) {
                                        test = "test failed";
                                    }
                                    Toast.makeText(MainActivity.this, test, Toast.LENGTH_SHORT).show();
                                    //Log.d(TAG, "onProductDetailsResponse: productId equals remove_ads");
                                }
                            }
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

    @Override
    protected void onResume() {
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

    // to save prefs
    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(AD_PREF, ads);
        editor.apply();

        super.onStop();
    }
}