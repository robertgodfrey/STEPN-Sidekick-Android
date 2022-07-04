package stepn.sidekick.stepnsidekick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Information about the app.
 *
 * @author Bob Godfrey
 * @version 1.3.0
 *
 */

public class About extends AppCompatActivity {

    Button emailButton, solButton, bnbButton, backToExercise, goToOptimizer;
    TextView emailTextView;
    ClipboardManager clipboard;
    ScrollView mainScroll;
    ConstraintLayout bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        buildUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildUI() {

        emailButton = findViewById(R.id.emailButton);
        solButton = findViewById(R.id.solButton);
        bnbButton = findViewById(R.id.bnbButton);
        emailTextView = findViewById(R.id.contactEmailTextView);
        backToExercise = findViewById(R.id.backToExerciseButton);
        goToOptimizer = findViewById(R.id.goToOptimizerButton);

        mainScroll = findViewById(R.id.aboutScrollView);
        bottomNav = findViewById(R.id.navigationBar);

        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        mainScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int scrollChange = oldScrollY - scrollY;
                if (scrollChange < -5 && bottomNav.getVisibility() == View.VISIBLE
                        && mainScroll.getScrollY() > 0) {
                    bottomNav.startAnimation(slideDown);
                    bottomNav.setVisibility(View.INVISIBLE);
                } else if (scrollChange > 5
                        && bottomNav.getVisibility() == View.INVISIBLE
                        && mainScroll.getChildAt(0).getBottom() > (mainScroll.getHeight() + mainScroll.getScrollY())) {
                    bottomNav.startAnimation(slideUp);
                    bottomNav.setVisibility(View.VISIBLE);
                }
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clip = ClipData.newPlainText("email", getString(R.string.contact_email));
                clipboard.setPrimaryClip(clip);

                Toast.makeText(About.this, getString(R.string.email_copied), Toast.LENGTH_SHORT).show();
            }
        });

        emailButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        emailTextView.setTextColor(ContextCompat.getColor(About.this, R.color.energy_blue_darker));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        emailTextView.setTextColor(ContextCompat.getColor(About.this, R.color.luck_socket_border));
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

                Toast.makeText(About.this, getString(R.string.sol_address_copied), Toast.LENGTH_SHORT).show();
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

                Toast.makeText(About.this, getString(R.string.bnb_address_copied), Toast.LENGTH_SHORT).show();
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

        backToExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        goToOptimizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Intent startOptimizer = new Intent(getApplicationContext(), ShoeOptimizer.class);
                startActivity(startOptimizer);
                overridePendingTransition(0, 0);
            }
        });
    }

    // to remove transition anim
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
