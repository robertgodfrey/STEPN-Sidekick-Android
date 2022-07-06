package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Shoe optimizer. Uses community data to determine best points for GST earning and mystery box
 * chance.
 *
 * @author Bob Godfrey
 * @version 1.3.0
 *
 */

public class ShoeOptimizer extends AppCompatActivity {

    private final String SHOE_RARITY_PREF = "shoeRarity";
    private final String SHOE_LEVEL_PREF = "shoeLevel";
    private final String BASE_EFF_PREF = "baseEff";
    private final String ADDED_EFF_PREF = "addedEff";
    private final String BASE_LUCK_PREF = "baseLuck";
    private final String ADDED_LUCK_PREF = "addedLuck";
    private final String BASE_COMF_PREF = "baseComf";
    private final String ADDED_COMF_PREF = "addedComf";
    private final String BASE_RES_PREF = "baseRes";
    private final String ADDED_RES_PREF = "addedRes";

    // TODO still need ot add prefs for gems :,)

    private final int COMMON = 2;
    private final int UNCOMMON = 3;
    private final int RARE = 4;
    private final int EPIC = 5;
    private final int WALKER = 0;
    private final int JOGGER = 1;
    private final int RUNNER = 2;
    private final int TRAINER = 3;

    ImageButton shoeRarityButton, shoeTypeButton, optimizeButton, backgroundButton;
    Button gemSocketOneButton, gemSocketTwoButton, gemSocketThreeButton, gemSocketFourButton,
            subEffButton, addEffButton, subLuckButton, addLuckButton, subComfButton, addComfButton,
            subResButton, addResButton, backToMainButton, goToInfoButton;
    SeekBar levelSeekbar;
    EditText energyEditText, effEditText, luckEditText, comfortEditText, resEditText, focusThief;

    TextView shoeRarityTextView, shoeTypeTextView, levelTextView, effTotalTextView, luckTotalTextView,
            comfortTotalTextView, resTotalTextView, pointsAvailableTextView, gstEarnedTextView,
            gstLimitTextView, durabilityLossTextView, repairCostTextView, gstIncomeTextView,
            effMinusTv, effPlusTv, luckMinusTv, luckPlusTv, comfMinusTv, comfPlusTv, resMinusTv,
            resPlusTv, optimizeTextView, shoeRarityShadowTextView, shoeTypeShadowTextView;

    ImageView gemSocketOne, gemSocketOneShadow, gemSocketOneLockPlus, gemSocketTwo,
            gemSocketTwoShadow, gemSocketTwoLockPlus, gemSocketThree, gemSocketThreeShadow,
            gemSocketThreeLockPlus, gemSocketFour, gemSocketFourShadow, gemSocketFourLockPlus,
            shoeTypeImageView, shoeCircles, shoeRarityButtonShadow, shoeTypeButtonShadow, minLevelImageView,
            optimizeButtonShadow, mysteryBox1, mysteryBox2, mysteryBox3, mysteryBox4, mysteryBox5,
            mysteryBox6, mysteryBox7, mysteryBox8, mysteryBox9, mysteryBox10, footOne, footTwo,
            footThree, energyBox;

    ScrollView mainScroll;
    ConstraintLayout bottomNav;

    private int shoeRarity, shoeType, shoeLevel, pointsAvailable, gstLimit, addedEff, addedLuck,
            addedComf, addedRes;

    private float baseMin, baseMax, baseEff, baseLuck, baseComf, baseRes, gemEff, gemLuck, gemComf,
            gemRes;

    private double energy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_optimizer);

        SharedPreferences getSharedPrefs = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        energy = (double) getSharedPrefs.getInt(ENERGY_PREF, 0) / 10;
        shoeType = getSharedPrefs.getInt(SHOE_TYPE_ITERATOR_PREF, 0);
        shoeRarity = getSharedPrefs.getInt(SHOE_RARITY_PREF, COMMON);
        shoeLevel = getSharedPrefs.getInt(SHOE_LEVEL_PREF, 0);
        baseEff = getSharedPrefs.getFloat(BASE_EFF_PREF, 0);
        addedEff = getSharedPrefs.getInt(ADDED_EFF_PREF, 0);
        baseLuck = getSharedPrefs.getFloat(BASE_LUCK_PREF, 0);
        addedLuck = getSharedPrefs.getInt(ADDED_LUCK_PREF, 0);
        baseComf = getSharedPrefs.getFloat(BASE_COMF_PREF, 0);
        addedComf = getSharedPrefs.getInt(ADDED_COMF_PREF, 0);
        baseRes = getSharedPrefs.getFloat(BASE_RES_PREF, 0);
        addedRes = getSharedPrefs.getInt(ADDED_RES_PREF, 0);

        // TODO
        gemEff = 0;
        gemLuck = 0;
        gemComf = 0;
        gemRes =0 ;

        buildUI();
    }

    // initializes all UI objects
    @SuppressLint("ClickableViewAccessibility")
    private void buildUI() {
        shoeRarityButton = findViewById(R.id.shoeRarityButton);
        shoeTypeButton = findViewById(R.id.shoeTypeButton);
        optimizeButton = findViewById(R.id.optimizeButton);

        gemSocketOneButton = findViewById(R.id.gemSocketOneButton);
        gemSocketTwoButton = findViewById(R.id.gemSocketTwoButton);
        gemSocketThreeButton = findViewById(R.id.gemSocketThreeButton);
        gemSocketFourButton = findViewById(R.id.gemSocketFourButton);

        subEffButton = findViewById(R.id.subEffButton);
        addEffButton = findViewById(R.id.addEffButton);
        subLuckButton = findViewById(R.id.subLuckButton);
        addLuckButton = findViewById(R.id.addLuckButton);
        subComfButton = findViewById(R.id.subComfButton);
        addComfButton = findViewById(R.id.addComfButton);
        subResButton = findViewById(R.id.subResButton);
        addResButton = findViewById(R.id.addResButton);

        backToMainButton = findViewById(R.id.goToMainButton);
        goToInfoButton = findViewById(R.id.goToInfoButton);
        backgroundButton = findViewById(R.id.backgroundThingButton);

        levelSeekbar = findViewById(R.id.levelSeekBar);

        energyEditText = findViewById(R.id.energyToSpendOptimizerEditText);
        effEditText = findViewById(R.id.baseEffEditText);
        luckEditText = findViewById(R.id.baseLuckEditText);
        comfortEditText = findViewById(R.id.baseComfortEditText);
        resEditText = findViewById(R.id.baseResEditText);
        focusThief = findViewById(R.id.focusThief);

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
        gstIncomeTextView = findViewById(R.id.gstIncomeTextView);
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

        mainScroll = findViewById(R.id.optimizerScrollView);
        bottomNav = findViewById(R.id.navigationBar);

        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus(view);
            }
        });

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

        gemSocketOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 5) {
                    Toast.makeText(ShoeOptimizer.this, "Socket available at level 5", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType();
                }
                clearFocus(view);
            }
        });

        gemSocketTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 10) {
                    Toast.makeText(ShoeOptimizer.this, "Socket available at level 10", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType();
                }
                clearFocus(view);
            }
        });

        gemSocketThreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 15) {
                    Toast.makeText(ShoeOptimizer.this, "Socket available at level 15", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType();
                }
                clearFocus(view);
            }
        });

        gemSocketFourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 20) {
                    Toast.makeText(ShoeOptimizer.this, "Socket available at level 20", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType();
                }
                clearFocus(view);
            }
        });

        shoeRarityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeRarity == 5) {
                    shoeRarity = 2;
                } else {
                    shoeRarity++;
                }
                updateRarity();

                shoeCircles.setScaleX(1.1f);
                shoeCircles.setScaleY(1.1f);
                ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                        shoeCircles,
                        PropertyValuesHolder.ofFloat("scaleX", 1f),
                        PropertyValuesHolder.ofFloat("scaleY", 1f));
                scaler.setDuration(1000);
                scaler.start();

                clearFocus(view);
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
                calcTotals();

                ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                        shoeTypeImageView,
                        PropertyValuesHolder.ofFloat("scaleX", 1f),
                        PropertyValuesHolder.ofFloat("scaleY", 1f));
                scaler.setDuration(1000);
                scaler.start();

                clearFocus(view);
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
                    if (!energyEditText.getText().toString().isEmpty() && !energyEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(energyEditText.getText().toString()) < 0.2) {
                            energyEditText.setText("0.2");
                        } else if (Float.parseFloat(energyEditText.getText().toString()) > 25) {
                            energyEditText.setText("25");
                        }
                    } else {
                        energyEditText.setText("0");
                    }
                    energy = Float.parseFloat(energyEditText.getText().toString());
                    calcTotals();
                }
            }
        });

        energyEditText.setText(String.valueOf(energy));

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
                updateLevel();
                calcTotals();
            }
        });

        effEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!effEditText.getText().toString().isEmpty() && !effEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(effEditText.getText().toString()) < baseMin) {
                            baseEff = baseMin;
                            effEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(effEditText.getText().toString()) > baseMax) {
                            baseEff = baseMax;
                            effEditText.setText(String.valueOf(baseMax));
                        } else {
                            baseEff = Float.parseFloat(effEditText.getText().toString());
                        }
                    }

                    updatePoints();
                }
            }
        });

        luckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!luckEditText.getText().toString().isEmpty() && !luckEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(luckEditText.getText().toString()) < baseMin) {
                            baseLuck = baseMin;
                            luckEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(luckEditText.getText().toString()) > baseMax) {
                            baseLuck = baseMax;
                            luckEditText.setText(String.valueOf(baseMax));
                        } else {
                            baseLuck = Float.parseFloat(luckEditText.getText().toString());
                        }
                    }

                    updatePoints();
                }
            }
        });

        comfortEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!comfortEditText.getText().toString().isEmpty() && !comfortEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(comfortEditText.getText().toString()) < baseMin) {
                            baseComf = baseMin;
                            comfortEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(comfortEditText.getText().toString()) > baseMax) {
                            baseComf = baseMax;
                            comfortEditText.setText(String.valueOf(baseMax));
                        } else {
                            baseComf = Float.parseFloat(comfortEditText.getText().toString());
                        }
                    }

                    updatePoints();
                }
            }
        });

        resEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!resEditText.getText().toString().isEmpty() && !resEditText.getText().toString().equals(".")) {
                        if (Float.parseFloat(resEditText.getText().toString()) < baseMin) {
                            baseRes = baseMin;
                            resEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(resEditText.getText().toString()) > baseMax) {
                            baseRes = baseMax;
                            resEditText.setText(String.valueOf(baseMax));
                        } else {
                            baseRes = Float.parseFloat(resEditText.getText().toString());
                        }
                    }

                    updatePoints();
                }
            }
        });

        // was going to make a function for all these damn buttons but without pointers I can't think
        // of how to do it :(

        subEffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addedEff > 0) {
                    addedEff--;
                    pointsAvailable++;

                    effTotalTextView.setText(String.valueOf(baseEff + addedEff + gemEff));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        subEffButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (addedEff > 0) {
                    pointsAvailable += addedEff;
                    addedEff = 0;

                    effTotalTextView.setText(String.valueOf(baseEff + addedEff + gemEff));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        addEffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsAvailable > 0) {
                    addedEff++;
                    pointsAvailable--;

                    effTotalTextView.setText(String.valueOf(baseEff + addedEff + gemEff));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        addEffButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (pointsAvailable > 0) {
                    addedEff += pointsAvailable;
                    pointsAvailable = 0;

                    effTotalTextView.setText(String.valueOf(baseEff + addedEff + gemEff));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        subLuckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addedLuck > 0) {
                    addedLuck--;
                    pointsAvailable++;

                    luckTotalTextView.setText(String.valueOf(baseLuck + addedLuck + gemLuck));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        subLuckButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (addedLuck > 0) {
                    pointsAvailable += addedLuck;
                    addedLuck = 0;

                    luckTotalTextView.setText(String.valueOf(baseLuck + addedLuck + gemLuck));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        addLuckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsAvailable > 0) {
                    addedLuck++;
                    pointsAvailable--;

                    luckTotalTextView.setText(String.valueOf(baseLuck + addedLuck + gemLuck));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        addLuckButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (pointsAvailable > 0) {
                    addedLuck += pointsAvailable;
                    pointsAvailable = 0;

                    luckTotalTextView.setText(String.valueOf(baseLuck + addedLuck + gemLuck));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        subComfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addedComf > 0) {
                    addedComf--;
                    pointsAvailable++;

                    comfortTotalTextView.setText(String.valueOf(baseComf + addedComf + gemComf));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        subComfButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (addedComf > 0) {
                    pointsAvailable += addedComf;
                    addedComf = 0;

                    comfortTotalTextView.setText(String.valueOf(baseComf + addedComf + gemComf));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        addComfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsAvailable > 0) {
                    addedComf++;
                    pointsAvailable--;

                    comfortTotalTextView.setText(String.valueOf(baseComf + addedComf + gemComf));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        addComfButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (pointsAvailable > 0) {
                    addedComf += pointsAvailable;
                    pointsAvailable = 0;

                    comfortTotalTextView.setText(String.valueOf(baseComf + addedComf + gemComf));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        subResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addedRes > 0) {
                    addedRes--;
                    pointsAvailable++;

                    resTotalTextView.setText(String.valueOf(baseRes + addedRes + gemRes));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        subResButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (addedRes > 0) {
                    pointsAvailable += addedRes;
                    addedRes = 0;

                    resTotalTextView.setText(String.valueOf(baseRes + addedRes + gemRes));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        addResButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsAvailable > 0) {
                    addedRes++;
                    pointsAvailable--;

                    resTotalTextView.setText(String.valueOf(baseRes + addedRes + gemRes));
                    updatePoints();
                    clearFocus(view);
                }
            }
        });

        addResButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (pointsAvailable > 0) {
                    addedRes += pointsAvailable;
                    pointsAvailable = 0;

                    resTotalTextView.setText(String.valueOf(baseRes + addedRes + gemRes));
                    updatePoints();
                    clearFocus(view);
                }
                return false;
            }
        });

        optimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                clearFocus(view);
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

        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        goToInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Intent startInfo = new Intent(getApplicationContext(), About.class);
                startActivity(startInfo);
                overridePendingTransition(0, 0);
            }
        });

        levelSeekbar.setProgress(shoeLevel - 1);

        updateRarity();
        updateType();
        updateLevel();
        loadPoints();
        calcTotals();
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

        updatePoints();
    }

    // updates UI depending on shoe type
    private void updateType() {
        switch (shoeType) {
            case JOGGER:
                shoeTypeImageView.setImageResource(R.drawable.shoe_jogger);
                shoeTypeTextView.setText("Jogger");
                footOne.setImageResource(R.drawable.footprint);
                footTwo.setVisibility(View.VISIBLE);
                footThree.setVisibility(View.GONE);
                break;
            case RUNNER:
                shoeTypeImageView.setImageResource(R.drawable.shoe_runner);
                shoeTypeTextView.setText("Runner");
                footOne.setImageResource(R.drawable.footprint);
                footTwo.setVisibility(View.VISIBLE);
                footThree.setVisibility(View.VISIBLE);
                break;
            case TRAINER:
                shoeTypeImageView.setImageResource(R.drawable.shoe_trainer);
                shoeTypeTextView.setText("Trainer");
                footOne.setImageResource(R.mipmap.trainer_t);
                footTwo.setVisibility(View.GONE);
                footThree.setVisibility(View.GONE);
                break;
            default:
                shoeTypeImageView.setImageResource(R.drawable.shoe_walker);
                shoeTypeTextView.setText("Walker");
                footOne.setImageResource(R.drawable.footprint);
                footTwo.setVisibility(View.GONE);
                footThree.setVisibility(View.GONE);
        }

        shoeTypeImageView.setScaleX(1.1f);
        shoeTypeImageView.setScaleY(1.1f);
    }

    // updates values depending on level
    private void updateLevel() {

        if (shoeLevel >= 5) {
            gemSocketOneLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketOneLockPlus.setImageResource(R.drawable.gem_socket_lock);
        }

        if (shoeLevel >= 10) {
            gemSocketTwoLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketTwoLockPlus.setImageResource(R.drawable.gem_socket_lock);
        }

        if (shoeLevel >= 15) {
            gemSocketThreeLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketThreeLockPlus.setImageResource(R.drawable.gem_socket_lock);
        }

        if (shoeLevel >= 20) {
            gemSocketFourLockPlus.setImageResource(R.mipmap.gem_socket_plus);
        } else {
            gemSocketFourLockPlus.setImageResource(R.drawable.gem_socket_lock);
        }

        if (shoeLevel < 10) {
            gstLimit = 5 + (shoeLevel * 5);
        } else if (shoeLevel < 23) {
            gstLimit = 60 + ((shoeLevel - 10) * 10);
        } else {
            gstLimit = 195 + ((shoeLevel - 23) * 15);
        }

        gstLimitTextView.setText(String.valueOf(gstLimit));

        updatePoints();
    }

    private void chooseSocketType() {
        // TODO
    }

    // calculate gst earnings, durability lost, repair cost, and mb chance
    private void calcTotals() {
        int durabilityLost;
        float repairCost;
        float gstTotal = 0;

        float totalEff = Float.parseFloat(effTotalTextView.getText().toString());
        float totalLuck = Float.parseFloat(luckTotalTextView.getText().toString());
        float totalComf = Float.parseFloat(comfortTotalTextView.getText().toString());
        float totalRes = Float.parseFloat(resTotalTextView.getText().toString());

        switch (shoeType) {
            case JOGGER:
                gstTotal = (float) (Math.floor((energy * (Math.pow((totalEff + 60), 0.52) - 5.4)) * 10) / 10);
                break;
            case RUNNER:
                gstTotal = (float) (Math.floor((energy * (Math.pow((totalEff + 60), 0.525) - 5.45)) * 10) / 10);
                break;
            case TRAINER:
                gstTotal = (float) (Math.floor((energy * (Math.pow((totalEff + 60), 0.527) - 5.48)) * 10) / 10);
                break;
            default:
                gstTotal = (float) (Math.floor((energy * (Math.pow((totalEff + 60), 0.515) - 5.35)) * 10) / 10);
        }

        durabilityLost = (int) (energy * Math.ceil(80 * Math.pow((totalRes + 10), -1.2) + 0.25));

        repairCost = calcRepairCost(durabilityLost);

        gstEarnedTextView.setText(String.valueOf(gstTotal));
        durabilityLossTextView.setText(String.valueOf(durabilityLost));
        repairCostTextView.setText(String.format("%.1f", repairCost));
        gstIncomeTextView.setText(String.format("%.1f", gstTotal - repairCost));

        if (gstTotal > gstLimit) {
            gstEarnedTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.red));
        } else {
            gstEarnedTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
        }
    }

    // calculates repair cost in gst based on shoe rarity, level, and durability lost
    private float calcRepairCost(int durabilityLost) {
        float baseCost;

        if (shoeRarity == COMMON) {
            switch (shoeLevel) {
                case 1:
                    baseCost = 0.31f;
                    break;
                case 2:
                    baseCost = 0.32f;
                    break;
                case 3:
                    baseCost = 0.33f;
                    break;
                case 4:
                    baseCost = 0.35f;
                    break;
                case 5:
                    baseCost = 0.36f;
                    break;
                case 6:
                    baseCost = 0.37f;
                    break;
                case 7:
                    baseCost = 0.38f;
                    break;
                case 8:
                    baseCost = 0.4f;
                    break;
                case 9:
                    baseCost = 0.41f;
                    break;
                case 10:
                    baseCost = 0.42f;
                    break;
                case 11:
                    baseCost = 0.44f;
                    break;
                case 12:
                    baseCost = 0.46f;
                    break;
                case 13:
                    baseCost = 0.48f;
                    break;
                case 14:
                    baseCost = 0.5f;
                    break;
                case 15:
                    baseCost = 0.52f;
                    break;
                case 16:
                    baseCost = 0.54f;
                    break;
                case 17:
                    baseCost = 0.56f;
                    break;
                case 18:
                    baseCost = 0.58f;
                    break;
                case 19:
                    baseCost = 0.6f;
                    break;
                case 20:
                    baseCost = 0.62f;
                    break;
                case 21:
                    baseCost = 0.64f;
                    break;
                case 22:
                    baseCost = 0.67f;
                    break;
                case 23:
                    baseCost = 0.7f;
                    break;
                case 24:
                    baseCost = 0.72f;
                    break;
                case 25:
                    baseCost = 0.75f;
                    break;
                case 26:
                    baseCost = 0.78f;
                    break;
                case 27:
                    baseCost = 0.81f;
                    break;
                case 28:
                    baseCost = 0.83f;
                    break;
                case 29:
                    baseCost = 0.87f;
                    break;
                case 30:
                    baseCost = 0.9f;
                    break;
                default:
                    baseCost = 0;
            }
        } else if (shoeRarity == UNCOMMON) {
            switch (shoeLevel) {
                case 1:
                    baseCost = 0.41f;
                    break;
                case 2:
                    baseCost = 0.43f;
                    break;
                case 3:
                    baseCost = 0.45f;
                    break;
                case 4:
                    baseCost = 0.46f;
                    break;
                case 5:
                    baseCost = 0.48f;
                    break;
                case 6:
                    baseCost = 0.5f;
                    break;
                case 7:
                    baseCost = 0.51f;
                    break;
                case 8:
                    baseCost = 0.53f;
                    break;
                case 9:
                    baseCost = 0.55f;
                    break;
                case 10:
                    baseCost = 0.57f;
                    break;
                case 11:
                    baseCost = 0.6f;
                    break;
                case 12:
                    baseCost = 0.62f;
                    break;
                case 13:
                    baseCost = 0.64f;
                    break;
                case 14:
                    baseCost = 0.66f;
                    break;
                case 15:
                    baseCost = 0.69f;
                    break;
                case 16:
                    baseCost = 0.71f;
                    break;
                case 17:
                    baseCost = 0.74f;
                    break;
                case 18:
                    baseCost = 0.77f;
                    break;
                case 19:
                    baseCost = 0.8f;
                    break;
                case 20:
                    baseCost = 0.83f;
                    break;
                case 21:
                    baseCost = 0.86f;
                    break;
                case 22:
                    baseCost = 0.89f;
                    break;
                case 23:
                    baseCost = 0.92f;
                    break;
                case 24:
                    baseCost = 0.95f;
                    break;
                case 25:
                    baseCost = 1;
                    break;
                case 26:
                    baseCost = 1.03f;
                    break;
                case 27:
                    baseCost = 1.06f;
                    break;
                case 28:
                    baseCost = 1.11f;
                    break;
                case 29:
                    baseCost = 1.15f;
                    break;
                case 30:
                    baseCost = 1.2f;
                    break;
                default:
                    baseCost = 0;
            }
        } else if (shoeRarity == RARE) {
            switch (shoeLevel) {
                case 1:
                    baseCost = 0.51f;
                    break;
                case 2:
                    baseCost = 0.54f;
                    break;
                case 3:
                    baseCost = 0.57f;
                    break;
                case 4:
                    baseCost = 0.59f;
                    break;
                case 5:
                    baseCost = 0.61f;
                    break;
                case 6:
                    baseCost = 0.63f;
                    break;
                case 7:
                    baseCost = 0.65f;
                    break;
                case 8:
                    baseCost = 0.67f;
                    break;
                case 9:
                    baseCost = 0.69f;
                    break;
                case 10:
                    baseCost = 0.72f;
                    break;
                case 11:
                    baseCost = 0.75f;
                    break;
                case 12:
                    baseCost = 0.78f;
                    break;
                case 13:
                    baseCost = 0.81f;
                    break;
                case 14:
                    baseCost = 0.84f;
                    break;
                case 15:
                    baseCost = 0.87f;
                    break;
                case 16:
                    baseCost = 0.90f;
                    break;
                case 17:
                    baseCost = 0.94f;
                    break;
                case 18:
                    baseCost = 0.97f;
                    break;
                case 19:
                    baseCost = 1;
                    break;
                case 20:
                    baseCost = 1.04f;
                    break;
                case 21:
                    baseCost = 1.08f;
                    break;
                case 22:
                    baseCost = 1.12f;
                    break;
                case 23:
                    baseCost = 1.16f;
                    break;
                case 24:
                    baseCost = 1.2f;
                    break;
                case 25:
                    baseCost = 1.25f;
                    break;
                case 26:
                    baseCost = 1.3f;
                    break;
                case 27:
                    baseCost = 1.34f;
                    break;
                case 28:
                    baseCost = 1.39f;
                    break;
                case 29:
                    baseCost = 1.45f;
                    break;
                case 30:
                    baseCost = 1.5f;
                    break;
                default:
                    baseCost = 0;
            }
        } else {
            baseCost = 1;
        }

        return baseCost * durabilityLost;
    }

    private void updatePoints() {
        pointsAvailable = (shoeLevel * 2 * shoeRarity) - addedEff - addedLuck - addedComf - addedRes;

        if (pointsAvailable < 0) {
            pointsAvailable = (shoeLevel * 2 * shoeRarity);
            addedEff = 0;
            addedLuck = 0;
            addedComf = 0;
            addedRes = 0;
        }

        effTotalTextView.setText(String.valueOf(baseEff + gemEff + addedEff));
        luckTotalTextView.setText(String.valueOf(baseLuck + gemLuck + addedLuck));
        comfortTotalTextView.setText(String.valueOf(baseComf + gemComf + addedComf));
        resTotalTextView.setText(String.valueOf(baseRes + gemRes + addedRes));

        pointsAvailableTextView.setText(String.valueOf(pointsAvailable));

        if (pointsAvailable > 0) {
            effPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            addEffButton.setClickable(true);
            luckPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            addLuckButton.setClickable(true);
            comfPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            addComfButton.setClickable(true);
            resPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            addResButton.setClickable(true);
        } else {
            effPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            addEffButton.setClickable(false);
            luckPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            addLuckButton.setClickable(false);
            comfPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            addComfButton.setClickable(false);
            resPlusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            addResButton.setClickable(false);
        }

        if (addedEff > 0) {
            effMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            subEffButton.setClickable(true);
        } else {
            effMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            subEffButton.setClickable(false);
        }

        if (addedLuck > 0) {
            luckMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            subLuckButton.setClickable(true);
        } else {
            luckMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            subLuckButton.setClickable(false);
        }

        if (addedComf > 0) {
            comfMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            subComfButton.setClickable(true);
        } else {
            comfMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            subComfButton.setClickable(false);
        }

        if (addedRes > 0) {
            resMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            subResButton.setClickable(true);
        } else {
            resMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            subResButton.setClickable(false);
        }

        pointsAvailableTextView.setText(String.valueOf(pointsAvailable));
        calcTotals();
    }

    // load initial point values
    private void loadPoints() {
        effEditText.setText(String.valueOf(baseEff));
        effTotalTextView.setText(String.valueOf(baseEff + addedEff + gemEff));
        luckEditText.setText(String.valueOf(baseLuck));
        luckTotalTextView.setText(String.valueOf(baseLuck + addedLuck + gemLuck));
        comfortEditText.setText(String.valueOf(baseComf));
        comfortTotalTextView.setText(String.valueOf(baseComf + addedComf + gemComf));
        resEditText.setText(String.valueOf(baseRes));
        resTotalTextView.setText(String.valueOf(baseRes + addedRes + gemRes));

    }

    // clears focus from the input boxes by focusing on another hidden edittext
    private void clearFocus(View view) {
        energyEditText.clearFocus();
        effEditText.clearFocus();
        luckEditText.clearFocus();
        comfortEditText.clearFocus();
        resEditText.clearFocus();

        focusThief.requestFocus();

        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // to save prefs
    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // TODO gems...
        editor.putInt(SHOE_RARITY_PREF, shoeRarity);
        editor.putInt(SHOE_LEVEL_PREF, shoeLevel);
        editor.putFloat(BASE_EFF_PREF, baseEff);
        editor.putInt(ADDED_EFF_PREF, addedEff);
        editor.putFloat(BASE_LUCK_PREF, baseLuck);
        editor.putInt(ADDED_LUCK_PREF, addedLuck);
        editor.putFloat(BASE_COMF_PREF, baseComf);
        editor.putInt(ADDED_COMF_PREF, addedComf);
        editor.putFloat(BASE_RES_PREF, baseRes);
        editor.putInt(ADDED_RES_PREF, addedRes);
        editor.apply();

        super.onStop();
    }

    // to remove transition anim
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}