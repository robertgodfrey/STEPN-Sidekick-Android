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
import com.android.billingclient.api.BillingFlowParams;
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
    FragmentManager fragmentManager;

    ConstraintLayout bottomNav;
    ScrollView scrollView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        SharedPreferences getSharedPrefs = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        // TODO ads = getSharedPrefs.getBoolean(AD_PREF, false);
        ads = true;

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