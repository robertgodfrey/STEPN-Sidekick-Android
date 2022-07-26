package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Main activity - Container for useolr to select which fragment to view (exercise, optimizer, or about)
 *
 * @author Bob Godfrey
 * @version 1.3.7 Added multiple shoes, mb fixes, small bug fixes
 *
 */

public class MainActivity extends AppCompatActivity {

    public boolean ads;

    Button goToExerciseButton, goToOptimizerButton, goToInfoButton;
    ImageView exerciseSelected, optimizerSelected, infoSelected;
    AdView bannerAd;
    FragmentManager fragmentManager;

    ConstraintLayout bottomNav;
    ScrollView scrollView;

    private BillingClient billingClient;
    private ProductDetails productDetails;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getSharedPrefs = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
                ads = getSharedPrefs.getBoolean(AD_PREF, true);
            }
        }).start();

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

        if (ads) {
            billingSetup();
            MobileAds.initialize(this);
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);
        } else {
            bannerAd.setVisibility(View.GONE);
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

    private void billingSetup() {
        billingClient = BillingClient.newBuilder(getApplicationContext())
                .setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProduct();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                if (billingClient != null && billingClient.isReady()) {
                    try {
                        new Handler().postDelayed(() -> billingSetup(),1000);
                    } catch (IllegalStateException e) {
                        Toast.makeText(MainActivity.this, "Can't connect to the Play Store", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void queryProduct() {
        QueryProductDetailsParams queryProductDetailsParams =QueryProductDetailsParams.newBuilder()
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
                        }
                    }
                }
        );


    }

    public void makePurchase(View view) {
        if (productDetails != null) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(ImmutableList.of(
                            BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).build()))
                    .build();

            billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
        } else {
            Toast.makeText(MainActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
        }
    }

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    completePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                ads = false;
                Toast.makeText(MainActivity.this, "Thank you! Ads are now disabled", Toast.LENGTH_SHORT).show();
                bannerAd.setVisibility(View.GONE);
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Toast.makeText(MainActivity.this, "Purchase cancelled", Toast.LENGTH_SHORT).show();
                ads = true;
            } else {
                ads = true;
            }


        }
    };

    private void completePurchase(Purchase item) {

        if (item.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!item.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(item.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            ads = false;
                            Toast.makeText(MainActivity.this, "Thank you! Ads are now disabled", Toast.LENGTH_SHORT).show();
                            bannerAd.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                ads = false;
                bannerAd.setVisibility(View.GONE);
            }

        }
    }

    @Override
    protected void onResume() {
        bannerAd.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        bannerAd.pause();
        super.onPause();
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