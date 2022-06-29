package stepn.sidekick.stepnsidekick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private final int COMMON = 2;
    private final int UNCOMMON = 3;
    private final int RARE = 4;
    private final int EPIC = 5;
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
            resPlusTv, optimizeTextView, shoeRarityShadowTextView, shoeTypeShadowTextView;

    ImageView gemSocketOne, gemSocketOneShadow, gemSocketOneLockPlus, gemSocketTwo,
            gemSocketTwoShadow, gemSocketTwoLockPlus, gemSocketThree, gemSocketThreeShadow,
            gemSocketThreeLockPlus, gemSocketFour, gemSocketFourShadow, gemSocketFourLockPlus,
            shoeTypeImageView, shoeCircles, shoeRarityButtonShadow, shoeTypeButtonShadow, minLevelImageView,
            optimizeButtonShadow, mysteryBox1, mysteryBox2, mysteryBox3, mysteryBox4, mysteryBox5,
            mysteryBox6, mysteryBox7, mysteryBox8, mysteryBox9, mysteryBox10, footOne, footTwo,
            footThree, energyBox;

    int shoeRarity, shoeType, shoeLevel, pointsAvailable, baseMin;

    float baseMax, baseEff, baseLuck, baseComf, baseRes, totalEff, totalLuck, totalComf,
            totalRes, gstEarned, repairCost, totalIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_optimizer);

        // TODO get these from shared prefs
        shoeRarity = COMMON;
        shoeType = WALKER;
        shoeLevel = 28;

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
        shoeRarityShadowTextView = findViewById(R.id.shoeRarityShadowTextView);
        shoeTypeTextView = findViewById(R.id.shoeTypeTextView);
        shoeTypeShadowTextView = findViewById(R.id.shoeTypeShadowTextView);
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
        energyBox = findViewById(R.id.energyBoxOptimizer);
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
                if (shoeRarity == 5) {
                    shoeRarity = 2;
                } else {
                    shoeRarity++;
                }
                updateRarity();
            }
        });

        shoeRarityButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shoeRarityButton.setVisibility(View.INVISIBLE);
                        shoeRarityTextView.setVisibility(View.INVISIBLE);
                        shoeRarityShadowTextView.setText(shoeRarityTextView.getText().toString());
                        switch (shoeRarity) {
                            case UNCOMMON:
                                shoeRarityButtonShadow.setImageResource(R.drawable.box_uncommon);
                                break;
                            case RARE:
                                shoeRarityButtonShadow.setImageResource(R.drawable.box_rare);
                                break;
                            case EPIC:
                                shoeRarityButtonShadow.setImageResource(R.drawable.box_epic);
                                break;
                            default:
                                shoeRarityButtonShadow.setImageResource(R.drawable.box_common);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        shoeRarityButton.setVisibility(View.VISIBLE);
                        shoeRarityTextView.setVisibility(View.VISIBLE);
                        shoeRarityButtonShadow.setImageResource(R.drawable.main_button_shadow);
                        break;
                }
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
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shoeTypeButton.setVisibility(View.INVISIBLE);
                        shoeTypeTextView.setVisibility(View.INVISIBLE);
                        shoeTypeShadowTextView.setText(shoeTypeTextView.getText().toString());
                        switch (shoeRarity) {
                            case UNCOMMON:
                                shoeTypeButtonShadow.setImageResource(R.drawable.box_uncommon);
                                break;
                            case RARE:
                                shoeTypeButtonShadow.setImageResource(R.drawable.box_rare);
                                break;
                            case EPIC:
                                shoeTypeButtonShadow.setImageResource(R.drawable.box_epic);
                                break;
                            default:
                                shoeTypeButtonShadow.setImageResource(R.drawable.box_common);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        shoeTypeButton.setVisibility(View.VISIBLE);
                        shoeTypeTextView.setVisibility(View.VISIBLE);
                        shoeTypeButtonShadow.setImageResource(R.drawable.main_button_shadow);
                        break;
                }
                return false;
            }
        });

        energyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    energyBox.setImageResource(R.drawable.energy_box_active);
                } else {
                    energyBox.setImageResource(R.drawable.energy_input_box);
                }
            }
        });

        levelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                levelTextView.setText(String.valueOf(i + 1));
                if (i > 0) {
                    minLevelImageView.setVisibility(View.VISIBLE);
                } else {
                    minLevelImageView.setVisibility(View.INVISIBLE);
                }
                shoeLevel = i + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateValues();
            }
        });

        effEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!effEditText.getText().toString().isEmpty() && !effEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(effEditText.getText().toString()) < baseMin) {
                            effEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(effEditText.getText().toString()) > baseMax) {
                            effEditText.setText(String.valueOf(baseMax));
                        }
                        effTotalTextView.setText(String.format("%.1f", Float.parseFloat(effEditText.getText().toString())));
                    } else {
                        effTotalTextView.setText("0");
                    }
                }
            }
        });

        luckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!luckEditText.getText().toString().isEmpty() && !luckEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(luckEditText.getText().toString()) < baseMin) {
                            luckEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(luckEditText.getText().toString()) > baseMax) {
                            luckEditText.setText(String.valueOf(baseMax));
                        }
                        luckTotalTextView.setText(String.format("%.1f", Float.parseFloat(luckEditText.getText().toString())));
                    } else {
                        luckTotalTextView.setText("0");
                    }
                }
            }
        });

        comfortEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!comfortEditText.getText().toString().isEmpty() && !comfortEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(comfortEditText.getText().toString()) < baseMin) {
                            comfortEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(comfortEditText.getText().toString()) > baseMax) {
                            comfortEditText.setText(String.valueOf(baseMax));
                        }
                        comfortTotalTextView.setText(String.format("%.1f", Float.parseFloat(effEditText.getText().toString())));
                    } else {
                        comfortTotalTextView.setText("0");
                    }
                }
            }
        });

        resEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!resEditText.getText().toString().isEmpty() && !resEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(resEditText.getText().toString()) < baseMin) {
                            resEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(resEditText.getText().toString()) > baseMax) {
                            resEditText.setText(String.valueOf(baseMax));
                        }
                        resTotalTextView.setText(String.format("%.1f", Float.parseFloat(effEditText.getText().toString())));
                    } else {
                        resTotalTextView.setText("0");
                    }
                }
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

        levelSeekbar.setProgress(shoeLevel);

        updateRarity();
        updateType();
        updateValues();
    }

    // updates UI depending on shoe rarity
    private void updateRarity() {
        switch (shoeRarity) {
            case UNCOMMON:
                shoeCircles.setImageResource(R.drawable.circles_uncommon);
                shoeRarityTextView.setText("Uncommon");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_uncommon);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_uncommon);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                effEditText.setHint("8 - 21.6");
                luckEditText.setHint("8 - 21.6");
                comfortEditText.setHint("8 - 21.6");
                resEditText.setHint("8 - 21.6");
                baseMin = 8;
                baseMax = 21.6f;
                break;
            case RARE:
                shoeCircles.setImageResource(R.drawable.circles_rare);
                shoeRarityTextView.setText("Rare");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_rare);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_rare);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                effEditText.setHint("15 - 42");
                luckEditText.setHint("15 - 42");
                comfortEditText.setHint("15 - 42");
                resEditText.setHint("15 - 42");
                baseMin = 15;
                baseMax = 42f;
                break;
            case EPIC:
                shoeCircles.setImageResource(R.drawable.circles_epic);
                shoeRarityTextView.setText("Epic");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_epic);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_epic);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.white));
                effEditText.setHint("28 - 75.6");
                luckEditText.setHint("28 - 75.6");
                comfortEditText.setHint("28 - 75.6");
                resEditText.setHint("28 - 75.6");
                baseMin = 28;
                baseMax = 75.6f;
                break;
            default:
                shoeCircles.setImageResource(R.drawable.circles_common);
                shoeRarityTextView.setText("Common");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                shoeRarityButton.setImageResource(R.drawable.box_common);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                shoeTypeButton.setImageResource(R.drawable.box_common);
                footOne.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                footTwo.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                footThree.setColorFilter(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
                effEditText.setHint("1 - 10");
                luckEditText.setHint("1 - 10");
                comfortEditText.setHint("1 - 10");
                resEditText.setHint("1 - 10");
                baseMin = 1;
                baseMax = 10;
        }

        shoeCircles.setScaleX(1.1f);
        shoeCircles.setScaleY(1.1f);
        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                shoeCircles,
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f));
        scaler.setDuration(1000);
        scaler.start();

        updateValues();
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

    // updates values depending on level, rarity, stats, and energy
    private void updateValues() {
        pointsAvailable = shoeLevel * 2 * shoeRarity;
        pointsAvailableTextView.setText(String.valueOf(pointsAvailable));

        if (shoeLevel >= 5) {
            gemSocketOneLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketOneLockPlus.setImageResource(R.mipmap.gem_socket_lock);
        }

        if (shoeLevel >= 10) {
            gemSocketTwoLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketTwoLockPlus.setImageResource(R.mipmap.gem_socket_lock);
        }

        if (shoeLevel >= 15) {
            gemSocketThreeLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketThreeLockPlus.setImageResource(R.mipmap.gem_socket_lock);
        }

        if (shoeLevel >= 20) {
            gemSocketFourLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketFourLockPlus.setImageResource(R.mipmap.gem_socket_lock);
        }
    }

}