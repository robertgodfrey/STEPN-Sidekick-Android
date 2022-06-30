package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private final int COMMON = 2;
    private final int UNCOMMON = 3;
    private final int RARE = 4;
    private final int EPIC = 5;
    private final int WALKER = 0;
    private final int JOGGER = 1;
    private final int RUNNER = 2;
    private final int TRAINER = 3;

    ImageButton shoeRarityButton, shoeTypeButton, optimizeButton;
    Button gemSocketOneButton, gemSocketTwoButton, gemSocketThreeButton, gemSocketFourButton,
            subEffButton, addEffButton, subLuckButton, addLuckButton, subComfButton, addComfButton,
            subResButton, addResButton;
    SeekBar levelSeekbar;
    EditText energyEditText, effEditText, luckEditText, comfortEditText, resEditText;

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

    private int shoeRarity, shoeType, shoeLevel, pointsAvailable, gstLimit;

    private float baseMin, baseMax, baseEff, baseLuck, baseComf, baseRes, gemEff, gemLuck, gemComf, gemRes,
            totalEff, totalLuck, totalComf, totalRes;

    private double energy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoe_optimizer);

        SharedPreferences getSharedPrefs = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        energy = (double) getSharedPrefs.getInt(ENERGY_PREF, 0) / 10;
        shoeType = getSharedPrefs.getInt(SHOE_TYPE_ITERATOR_PREF, 0);

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

        gemSocketOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 5) {
                    Toast.makeText(ShoeOptimizer.this, "Socket available at level 5", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType();
                }
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
                            effEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(effEditText.getText().toString()) > baseMax) {
                            effEditText.setText(String.valueOf(baseMax));
                        }
                        effTotalTextView.setText(String.format("%.1f", Float.parseFloat(effEditText.getText().toString())));
                    } else {
                        effTotalTextView.setText("0");
                    }
                    baseEff = Float.parseFloat(effTotalTextView.getText().toString());
                    updatePoints();
                }
            }
        });

        subEffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalEff - gemEff > baseEff) {
                    totalEff--;
                    pointsAvailable++;

                    effTotalTextView.setText(String.valueOf(totalEff));
                     updatePoints();
                }
            }
        });

        addEffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsAvailable > 0) {
                    totalEff++;
                    pointsAvailable--;

                    effTotalTextView.setText(String.valueOf(totalEff));
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
                            luckEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(luckEditText.getText().toString()) > baseMax) {
                            luckEditText.setText(String.valueOf(baseMax));
                        }
                        luckTotalTextView.setText(String.format("%.1f", Float.parseFloat(luckEditText.getText().toString())));
                    } else {
                        luckTotalTextView.setText("0");
                    }
                    baseLuck = Float.parseFloat(luckTotalTextView.getText().toString());
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
                            comfortEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(comfortEditText.getText().toString()) > baseMax) {
                            comfortEditText.setText(String.valueOf(baseMax));
                        }
                        comfortTotalTextView.setText(String.format("%.1f", Float.parseFloat(effEditText.getText().toString())));
                    } else {
                        comfortTotalTextView.setText("0");
                    }
                    baseComf = Float.parseFloat(comfortTotalTextView.getText().toString());
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
                            resEditText.setText(String.valueOf(baseMin));
                        } else if (Float.parseFloat(resEditText.getText().toString()) > baseMax) {
                            resEditText.setText(String.valueOf(baseMax));
                        }
                        resTotalTextView.setText(String.format("%.1f", Float.parseFloat(effEditText.getText().toString())));
                    } else {
                        resTotalTextView.setText("0");
                    }
                    baseRes = Float.parseFloat(resTotalTextView.getText().toString());
                    updatePoints();
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
        updateLevel();
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

        pointsAvailable = shoeLevel * 2 * shoeRarity;
        pointsAvailableTextView.setText(String.valueOf(pointsAvailable));

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
        calcTotals();
    }

    // updates values depending on level
    private void updateLevel() {
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

    // calculate gst earnings & durability
    private void calcTotals() {
        float gstTotal = 0;

        totalEff = Float.parseFloat(effTotalTextView.getText().toString());
        totalLuck = Float.parseFloat(luckTotalTextView.getText().toString());
        totalComf = Float.parseFloat(comfortTotalTextView.getText().toString());
        totalRes = Float.parseFloat(resTotalTextView.getText().toString());

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

        gstEarnedTextView.setText(String.valueOf(gstTotal));

        if (gstTotal > gstLimit) {
            gstEarnedTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.red));
        } else {
            gstEarnedTextView.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
        }
    }

    private void updatePoints() {
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

        if (totalEff - gemEff > baseEff) {
            effMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.almost_black));
            subEffButton.setClickable(true);
        } else {
            effMinusTv.setTextColor(ContextCompat.getColor(ShoeOptimizer.this, R.color.gem_socket_shadow));
            subEffButton.setClickable(false);
        }

        pointsAvailableTextView.setText(String.valueOf(pointsAvailable));
        calcTotals();
    }

    // to save prefs
    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // editor.putInt(RARITY, 6); // TODO rarity, level, baseEff, baseLuck, baseComf, baseRes, gemSocket x 4, totalEff, totalLuck, totalComf, totalRes.... yikes
        editor.apply();

        super.onStop();
    }
}