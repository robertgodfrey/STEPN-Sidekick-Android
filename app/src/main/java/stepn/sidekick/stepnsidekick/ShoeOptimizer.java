package stepn.sidekick.stepnsidekick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Shoe optimizer. Uses community data to determine best points for GST earning and mystery box
 * chance.
 *
 * @author Bob Godfrey
 * @version 1.3.0
 *
 */

public class ShoeOptimizer extends AppCompatActivity {

    private final int COMMON = 0;
    private final int UNCOMMON = 1;
    private final int RARE = 2;
    private final int EPIC = 3;
    private final int WALKER = 0;
    private final int JOGGER = 1;
    private final int RUNNER = 2;
    private final int TRAINER = 3;

    ImageButton shoeRarityButton, shoeTypeButton, optimizeButton;
    SeekBar levelSeekbar;
    EditText energyEditText, effEditText, luckEditText, comfortEditText, resEditText;

    TextView shoeRarityTextView, shoeTypeTextView, levelTextView, effTotalTextView, luckTotalTextView,
            comfortTotalTextView, resTotalTextView, pointsAvailableTextView, gstEarnedTextView,
            gstLimitTextView, durabilityLossTextView, repairCostTextView, gstTotalTextView,
            effMinusTv, effPlusTv, luckMinusTv, luckPlusTv, comfMinusTv, comfPlusTv, resMinusTv,
            resPlusTv, optimizeTextView;

    ImageView gemSocketOne, gemSocketOneShadow, gemSocketOneLockPlus, gemSocketTwo,
            gemSocketTwoShadow, gemSocketTwoLockPlus, gemSocketThree, gemSocketThreeShadow,
            gemSocketThreeLockPlus, gemSocketFour, gemSocketFourShadow, gemSocketFourLockPlus,
            shoeTypeImageView, shoeCircles, shoeRarityButtonShadow, shoeTypeButtonShadow, minLevelImageView,
            optimizeButtonShadow, mysteryBox1, mysteryBox2, mysteryBox3, mysteryBox4, mysteryBox5,
            mysteryBox6, mysteryBox7, mysteryBox8, mysteryBox9, mysteryBox10, footOne, footTwo,
            footThree;

    int shoeRarity, shoeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_optimizer);

        // TODO get these from shared prefs
        shoeRarity = 1;
        shoeType = 0;

        buildUI();
    }

    // initializes all UI objects
    @SuppressLint("ClickableViewAccessibility")
    private void buildUI() {
        shoeRarityButton = findViewById(R.id.shoeRarityButton);
        shoeTypeButton = findViewById(R.id.shoeTypeButton);
        optimizeButton = findViewById(R.id.optimizeButton);

        levelSeekbar = findViewById(R.id.levelSeekBar);

        energyEditText = findViewById(R.id.energyToSpendOptimizerEditText);
        effEditText = findViewById(R.id.baseEffEditText);
        luckEditText = findViewById(R.id.baseLuckEditText);
        comfortEditText = findViewById(R.id.baseComfortEditText);
        resEditText = findViewById(R.id.baseResEditText);

        shoeRarityTextView = findViewById(R.id.shoeRarityTextView);
        shoeTypeTextView = findViewById(R.id.shoeTypeTextView);
        levelTextView = findViewById(R.id.levelTextView);
        effTotalTextView = findViewById(R.id.totalEffTextView);
        luckTotalTextView = findViewById(R.id.totalLuckTextView);
        comfortTotalTextView = findViewById(R.id.totalComfTextView);
        resTotalTextView = findViewById(R.id.totalResTextView);
        pointsAvailableTextView = findViewById(R.id.pointsTextView);
        gstEarnedTextView = findViewById(R.id.gstPerDayTextView);
        gstLimitTextView = findViewById(R.id.gstLimitPerDayTextView);
        durabilityLossTextView = findViewById(R.id.durabilityLossTextView);
        repairCostTextView = findViewById(R.id.repairCostTextView);
        gstTotalTextView = findViewById(R.id.gstIncomeTextView);
        optimizeTextView = findViewById(R.id.optimizeTextView);

        effMinusTv = findViewById(R.id.subEffTextView);
        effPlusTv = findViewById(R.id.addEffTextView);
        luckMinusTv = findViewById(R.id.subLuckTextView);
        luckPlusTv = findViewById(R.id.addLuckTextView);
        comfMinusTv = findViewById(R.id.subComfTextView);
        comfPlusTv = findViewById(R.id.addComfTextView);
        resMinusTv = findViewById(R.id.subResTextView);
        resPlusTv = findViewById(R.id.addResTextView);

        gemSocketOne = findViewById(R.id.gemSocketOne);
        gemSocketOneShadow = findViewById(R.id.gemSocketOneShadow);
        gemSocketOneLockPlus = findViewById(R.id.gemSocketOneLockPlus);
        gemSocketTwo = findViewById(R.id.gemSocketTwo);
        gemSocketTwoShadow = findViewById(R.id.gemSocketTwoShadow);
        gemSocketTwoLockPlus = findViewById(R.id.gemSocketTwoLockPlus);
        gemSocketThree = findViewById(R.id.gemSocketThree);
        gemSocketThreeShadow = findViewById(R.id.gemSocketThreeShadow);
        gemSocketThreeLockPlus = findViewById(R.id.gemSocketThreeLockPlus);
        gemSocketFour = findViewById(R.id.gemSocketFour);
        gemSocketFourShadow = findViewById(R.id.gemSocketFourShadow);
        gemSocketFourLockPlus = findViewById(R.id.gemSocketFourLockPlus);

        shoeTypeImageView = findViewById(R.id.shoeTypeImageView);
        shoeCircles = findViewById(R.id.shoeBackground);
        shoeRarityButtonShadow = findViewById(R.id.shoeRarityBoxShadow);
        shoeTypeButtonShadow = findViewById(R.id.shoeTypeBoxShadow);
        minLevelImageView = findViewById(R.id.seekbarMinLevel);
        optimizeButtonShadow = findViewById(R.id.optimizeButtonShadow);

        mysteryBox1 = findViewById(R.id.mysteryBoxLvl1);
        mysteryBox2 = findViewById(R.id.mysteryBoxLvl2);
        mysteryBox3 = findViewById(R.id.mysteryBoxLvl3);
        mysteryBox4 = findViewById(R.id.mysteryBoxLvl4);
        mysteryBox5 = findViewById(R.id.mysteryBoxLvl5);
        mysteryBox6 = findViewById(R.id.mysteryBoxLvl6);
        mysteryBox7 = findViewById(R.id.mysteryBoxLvl7);
        mysteryBox8 = findViewById(R.id.mysteryBoxLvl8);
        mysteryBox9 = findViewById(R.id.mysteryBoxLvl9);
        mysteryBox10 = findViewById(R.id.mysteryBoxLvl10);

        footOne = findViewById(R.id.footprint1ImageView);
        footTwo = findViewById(R.id.footprint2ImageView);
        footThree = findViewById(R.id.footprint3ImageView);

        shoeRarityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeRarity == 3) {
                    shoeRarity = 0;
                } else {
                    shoeRarity++;
                }

                updateRarity();
            }
        });

        shoeRarityButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // TODO
                return false;
            }
        });

        shoeTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeType == 3) {
                    shoeType = 0;
                } else {
                    shoeType++;
                }

                updateType();
            }
        });

        shoeTypeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // TODO
                return false;
            }
        });

        optimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        optimizeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        optimizeButton.setVisibility(View.INVISIBLE);
                        optimizeTextView.setVisibility(View.INVISIBLE);
                        optimizeButtonShadow.setImageResource(R.drawable.start_button);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        optimizeButton.setVisibility(View.VISIBLE);
                        optimizeTextView.setVisibility(View.VISIBLE);
                        optimizeButtonShadow.setImageResource(R.drawable.start_button_shadow);
                        break;
                }
                return false;
            }
        });

        updateRarity();
        updateType();
    }

    // updates UI depending on shoe rarity
    private void updateRarity() {
        switch (shoeRarity) {
            case UNCOMMON:
                shoeCircles.setImageResource(R.drawable.circles_uncommon);
                shoeRarityTextView.setText("Uncommon");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_uncommon);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_uncommon);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                effEditText.setHint("8 - 21.6");
                luckEditText.setHint("8 - 21.6");
                comfortEditText.setHint("8 - 21.6");
                resEditText.setHint("8 - 21.6");
                break;
            case RARE:
                shoeCircles.setImageResource(R.drawable.circles_rare);
                shoeRarityTextView.setText("Rare");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_rare);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_rare);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                effEditText.setHint("15 - 42");
                luckEditText.setHint("15 - 42");
                comfortEditText.setHint("15 - 42");
                resEditText.setHint("15 - 42");
                break;
            case EPIC:
                shoeCircles.setImageResource(R.drawable.circles_epic);
                shoeRarityTextView.setText("Epic");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_epic);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_epic);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                effEditText.setHint("28 - 75.6");
                luckEditText.setHint("28 - 75.6");
                comfortEditText.setHint("28 - 75.6");
                resEditText.setHint("28 - 75.6");
                break;
            default:
                shoeCircles.setImageResource(R.drawable.circles_common);
                shoeRarityTextView.setText("Common");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                shoeRarityButton.setImageResource(R.drawable.box_common);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                shoeTypeButton.setImageResource(R.drawable.box_common);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                effEditText.setHint("1 - 10");
                luckEditText.setHint("1 - 10");
                comfortEditText.setHint("1 - 10");
                resEditText.setHint("1 - 10");
        }

        shoeCircles.setScaleX(1.1f);
        shoeCircles.setScaleY(1.1f);
        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                shoeCircles,
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f));
        scaler.setDuration(1000);
        scaler.start();
    }

    // updates UI depending on shoe type
    private void updateType() {
        switch (shoeType) {
            case JOGGER:
                shoeTypeImageView.setImageResource(R.mipmap.jogger);
                shoeTypeTextView.setText("Jogger");
                footOne.setImageResource(R.mipmap.footprint);
                footTwo.setVisibility(View.VISIBLE);
                footThree.setVisibility(View.GONE);
                break;
            case RUNNER:
                shoeTypeImageView.setImageResource(R.mipmap.runner);
                shoeTypeTextView.setText("Runner");
                footOne.setImageResource(R.mipmap.footprint);
                footTwo.setVisibility(View.VISIBLE);
                footThree.setVisibility(View.VISIBLE);
                break;
            case TRAINER:
                shoeTypeImageView.setImageResource(R.mipmap.trainer);
                shoeTypeTextView.setText("Trainer");
                footOne.setImageResource(R.mipmap.trainer_t);
                footTwo.setVisibility(View.GONE);
                footThree.setVisibility(View.GONE);
                break;
            default:
                shoeTypeImageView.setImageResource(R.mipmap.walker);
                shoeTypeTextView.setText("Walker");
                footOne.setImageResource(R.mipmap.footprint);
                footTwo.setVisibility(View.GONE);
                footThree.setVisibility(View.GONE);
        }

        shoeTypeImageView.setScaleX(1.1f);
        shoeTypeImageView.setScaleY(1.1f);
        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                shoeTypeImageView,
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f));
        scaler.setDuration(1000);
        scaler.start();

    }

}