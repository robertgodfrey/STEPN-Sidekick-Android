package stepn.sidekick.stepnsidekick;

import static android.content.Context.MODE_PRIVATE;
import static stepn.sidekick.stepnsidekick.Finals.COMF;
import static stepn.sidekick.stepnsidekick.Finals.EFF;
import static stepn.sidekick.stepnsidekick.Finals.LUCK;
import static stepn.sidekick.stepnsidekick.Finals.PREFERENCES_ID;
import static stepn.sidekick.stepnsidekick.Finals.RES;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Shoe optimizer fragment. Uses community data to determine best points for GST earning and mystery
 * box chance.
 *
 * @author Bob Godfrey
 * @version 1.3.6 Added luck optimizer, mb fixes, other bug fixes
 *
 */

public class OptimizerFrag extends Fragment {

    // holy preferences batman
    private final String OPT_SHOE_TYPE_PREF = "optShoe";
    private final String OPT_ENERGY_PERF = "optEnergy";
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
    private final String GEM_ONE_TYPE_PREF = "gemOneType";
    private final String GEM_ONE_RARITY_PREF = "gemOneRarity";
    private final String GEM_ONE_MOUNTED_PREF = "gemOneGem";
    private final String GEM_TWO_TYPE_PREF = "gemTwoType";
    private final String GEM_TWO_RARITY_PREF = "gemTwoRarity";
    private final String GEM_TWO_MOUNTED_PREF = "gemTwoGem";
    private final String GEM_THREE_TYPE_PREF = "gemThreeType";
    private final String GEM_THREE_RARITY_PREF = "gemThreeRarity";
    private final String GEM_THREE_MOUNTED_PREF = "gemThreeGem";
    private final String GEM_FOUR_TYPE_PREF = "gemFourType";
    private final String GEM_FOUR_RARITY_PREF = "gemFourRarity";
    private final String GEM_FOUR_MOUNTED_PREF = "gemFourGem";
    private final String COMF_GEM_HP_REPAIR = "hpRepairGem";
    private final String UPDATE_PREF = "optimizerUpdate";

    private final int COMMON = 2;
    private final int UNCOMMON = 3;
    private final int RARE = 4;
    private final int EPIC = 5;
    private final int WALKER = 0;
    private final int JOGGER = 1;
    private final int RUNNER = 2;
    private final int TRAINER = 3;

    ImageButton shoeRarityButton, shoeTypeButton, optimizeGstButton, optimizeLuckButton, backgroundButton;
    Button gemSocketOneButton, gemSocketTwoButton, gemSocketThreeButton, gemSocketFourButton,
            subEffButton, addEffButton, subLuckButton, addLuckButton, subComfButton, addComfButton,
            subResButton, addResButton, changeComfGemButton;
    SeekBar levelSeekbar;
    EditText energyEditText, effEditText, luckEditText, comfortEditText, resEditText, focusThief;

    TextView shoeRarityTextView, shoeTypeTextView, levelTextView, effTotalTextView, luckTotalTextView,
            comfortTotalTextView, resTotalTextView, pointsAvailableTextView, gstEarnedTextView,
            gstLimitTextView, durabilityLossTextView, repairCostDurTextView, gstIncomeTextView,
            effMinusTv, effPlusTv, luckMinusTv, luckPlusTv, comfMinusTv, comfPlusTv, resMinusTv,
            resPlusTv, optimizeGstTextView, shoeRarityShadowTextView, shoeTypeShadowTextView, lvl10Shrug,
            hpLossTextView, repairCostHpTextView, gemMultipleTextView, gemMultipleTotalTextView,
            optimizeLuckTextView;

    ImageView gemSocketOne, gemSocketOneShadow, gemSocketOneLockPlus, gemSocketTwo,
            gemSocketTwoShadow, gemSocketTwoLockPlus, gemSocketThree, gemSocketThreeShadow,
            gemSocketThreeLockPlus, gemSocketFour, gemSocketFourShadow, gemSocketFourLockPlus,
            shoeTypeImageView, shoeCircles, shoeRarityButtonShadow, shoeTypeButtonShadow, minLevelImageView,
            optimizeGstButtonShadow, mysteryBox1, mysteryBox2, mysteryBox3, mysteryBox4, mysteryBox5,
            mysteryBox6, mysteryBox7, mysteryBox8, mysteryBox9, footOne, footTwo, footThree, energyBox,
            comfGemHpRepairImageView, comfGemHpRepairTotalImageView, optimizeLuckButtonShadow;

    private int shoeRarity, shoeType, shoeLevel, pointsAvailable, gstLimit, addedEff, addedLuck,
            addedComf, addedRes, comfGemLvlForRepair, gstCostBasedOnGem;
    private float baseMin, baseMax, baseEff, baseLuck, baseComf, baseRes, gemEff, gemLuck, gemComf,
            gemRes, dpScale, energy, hpPercentRestored;
    private boolean saveNewGem, update;
    private double hpLoss;

    ArrayList<Gem> gems;
    public OptimizerFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences getSharedPrefs = requireActivity().getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        energy = getSharedPrefs.getFloat(OPT_ENERGY_PERF, 0);
        shoeType = getSharedPrefs.getInt(OPT_SHOE_TYPE_PREF, 0);
        shoeRarity = getSharedPrefs.getInt(SHOE_RARITY_PREF, COMMON);
        shoeLevel = getSharedPrefs.getInt(SHOE_LEVEL_PREF, 1);
        baseEff = getSharedPrefs.getFloat(BASE_EFF_PREF, 0);
        addedEff = getSharedPrefs.getInt(ADDED_EFF_PREF, 0);
        baseLuck = getSharedPrefs.getFloat(BASE_LUCK_PREF, 0);
        addedLuck = getSharedPrefs.getInt(ADDED_LUCK_PREF, 0);
        baseComf = getSharedPrefs.getFloat(BASE_COMF_PREF, 0);
        addedComf = getSharedPrefs.getInt(ADDED_COMF_PREF, 0);
        baseRes = getSharedPrefs.getFloat(BASE_RES_PREF, 0);
        addedRes = getSharedPrefs.getInt(ADDED_RES_PREF, 0);
        comfGemLvlForRepair = getSharedPrefs.getInt(COMF_GEM_HP_REPAIR, 1);
        update = getSharedPrefs.getBoolean(UPDATE_PREF, true);

        dpScale = getResources().getDisplayMetrics().density;

        gems = new ArrayList<>();

        gems.add(new Gem(
                getSharedPrefs.getInt(GEM_ONE_TYPE_PREF, -1),
                getSharedPrefs.getInt(GEM_ONE_RARITY_PREF, 0),
                getSharedPrefs.getInt(GEM_ONE_MOUNTED_PREF, 0)));
        gems.add(new Gem(
                getSharedPrefs.getInt(GEM_TWO_TYPE_PREF, -1),
                getSharedPrefs.getInt(GEM_TWO_RARITY_PREF, 0),
                getSharedPrefs.getInt(GEM_TWO_MOUNTED_PREF, 0)));
        gems.add(new Gem(
                getSharedPrefs.getInt(GEM_THREE_TYPE_PREF, -1),
                getSharedPrefs.getInt(GEM_THREE_RARITY_PREF, 0),
                getSharedPrefs.getInt(GEM_THREE_MOUNTED_PREF, 0)));
        gems.add(new Gem(
                getSharedPrefs.getInt(GEM_FOUR_TYPE_PREF, -1),
                getSharedPrefs.getInt(GEM_FOUR_RARITY_PREF, 0),
                getSharedPrefs.getInt(GEM_FOUR_MOUNTED_PREF, 0)));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_optimizer, container, false);

        shoeRarityButton = view.findViewById(R.id.shoeRarityButton);
        shoeTypeButton = view.findViewById(R.id.shoeTypeButton);
        optimizeGstButton = view.findViewById(R.id.optimizeGstButton);
        optimizeLuckButton = view.findViewById(R.id.optimizeLuckButton);

        gemSocketOneButton = view.findViewById(R.id.gemSocketOneButton);
        gemSocketTwoButton = view.findViewById(R.id.gemSocketTwoButton);
        gemSocketThreeButton = view.findViewById(R.id.gemSocketThreeButton);
        gemSocketFourButton = view.findViewById(R.id.gemSocketFourButton);

        comfGemHpRepairImageView = view.findViewById(R.id.comfGemHpRepair);
        changeComfGemButton = view.findViewById(R.id.changeComfGemButton);
        gemMultipleTextView = view.findViewById(R.id.gemMultipleTextView);
        gemMultipleTotalTextView = view.findViewById(R.id.gemMultipleTotalTextView);
        comfGemHpRepairTotalImageView = view.findViewById(R.id.comfGemHpTotalRepair);

        subEffButton = view.findViewById(R.id.subEffButton);
        addEffButton = view.findViewById(R.id.addEffButton);
        subLuckButton = view.findViewById(R.id.subLuckButton);
        addLuckButton = view.findViewById(R.id.addLuckButton);
        subComfButton = view.findViewById(R.id.subComfButton);
        addComfButton = view.findViewById(R.id.addComfButton);
        subResButton = view.findViewById(R.id.subResButton);
        addResButton = view.findViewById(R.id.addResButton);

        backgroundButton = view.findViewById(R.id.backgroundThingButton);
        levelSeekbar = view.findViewById(R.id.levelSeekBar);

        energyEditText = view.findViewById(R.id.energyToSpendOptimizerEditText);
        effEditText = view.findViewById(R.id.baseEffEditText);
        luckEditText = view.findViewById(R.id.baseLuckEditText);
        comfortEditText = view.findViewById(R.id.baseComfortEditText);
        resEditText = view.findViewById(R.id.baseResEditText);
        focusThief = view.findViewById(R.id.focusThief);

        shoeRarityTextView = view.findViewById(R.id.shoeRarityTextView);
        shoeRarityShadowTextView = view.findViewById(R.id.shoeRarityShadowTextView);
        shoeTypeTextView = view.findViewById(R.id.shoeTypeTextView);
        shoeTypeShadowTextView = view.findViewById(R.id.shoeTypeShadowTextView);
        levelTextView = view.findViewById(R.id.levelTextView);
        effTotalTextView = view.findViewById(R.id.totalEffTextView);
        luckTotalTextView = view.findViewById(R.id.totalLuckTextView);
        comfortTotalTextView = view.findViewById(R.id.totalComfTextView);
        resTotalTextView = view.findViewById(R.id.totalResTextView);
        pointsAvailableTextView = view.findViewById(R.id.pointsTextView);
        gstEarnedTextView = view.findViewById(R.id.gstPerDayTextView);
        gstLimitTextView = view.findViewById(R.id.gstLimitPerDayTextView);
        durabilityLossTextView = view.findViewById(R.id.durabilityLossTextView);
        repairCostDurTextView = view.findViewById(R.id.repairCostDurTextView);
        gstIncomeTextView = view.findViewById(R.id.gstIncomeTextView);
        optimizeGstTextView = view.findViewById(R.id.optimizeGstTextView);
        optimizeLuckTextView = view.findViewById(R.id.optimizeLuckTextView);
        lvl10Shrug = view.findViewById(R.id.lvl10shrug);
        hpLossTextView = view.findViewById(R.id.hpLossTextView);
        repairCostHpTextView = view.findViewById(R.id.repairCostHpTextView);

        effMinusTv = view.findViewById(R.id.subEffTextView);
        effPlusTv = view.findViewById(R.id.addEffTextView);
        luckMinusTv = view.findViewById(R.id.subLuckTextView);
        luckPlusTv = view.findViewById(R.id.addLuckTextView);
        comfMinusTv = view.findViewById(R.id.subComfTextView);
        comfPlusTv = view.findViewById(R.id.addComfTextView);
        resMinusTv = view.findViewById(R.id.subResTextView);
        resPlusTv = view.findViewById(R.id.addResTextView);

        gemSocketOne = view.findViewById(R.id.gemSocketOne);
        gemSocketOneShadow = view.findViewById(R.id.gemSocketOneShadow);
        gemSocketOneLockPlus = view.findViewById(R.id.gemSocketOneLockPlus);
        gemSocketTwo = view.findViewById(R.id.gemSocketTwo);
        gemSocketTwoShadow = view.findViewById(R.id.gemSocketTwoShadow);
        gemSocketTwoLockPlus = view.findViewById(R.id.gemSocketTwoLockPlus);
        gemSocketThree = view.findViewById(R.id.gemSocketThree);
        gemSocketThreeShadow = view.findViewById(R.id.gemSocketThreeShadow);
        gemSocketThreeLockPlus = view.findViewById(R.id.gemSocketThreeLockPlus);
        gemSocketFour = view.findViewById(R.id.gemSocketFour);
        gemSocketFourShadow = view.findViewById(R.id.gemSocketFourShadow);
        gemSocketFourLockPlus = view.findViewById(R.id.gemSocketFourLockPlus);

        shoeTypeImageView = view.findViewById(R.id.shoeTypeImageView);
        shoeCircles = view.findViewById(R.id.shoeBackground);
        shoeRarityButtonShadow = view.findViewById(R.id.shoeRarityBoxShadow);
        shoeTypeButtonShadow = view.findViewById(R.id.shoeTypeBoxShadow);
        energyBox = view.findViewById(R.id.energyBoxOptimizer);
        minLevelImageView = view.findViewById(R.id.seekbarMinLevel);
        optimizeGstButtonShadow = view.findViewById(R.id.optimizeGstButtonShadow);
        optimizeLuckButtonShadow = view.findViewById(R.id.optimizeLuckButtonShadow);

        mysteryBox1 = view.findViewById(R.id.mysteryBoxLvl1);
        mysteryBox2 = view.findViewById(R.id.mysteryBoxLvl2);
        mysteryBox3 = view.findViewById(R.id.mysteryBoxLvl3);
        mysteryBox4 = view.findViewById(R.id.mysteryBoxLvl4);
        mysteryBox5 = view.findViewById(R.id.mysteryBoxLvl5);
        mysteryBox6 = view.findViewById(R.id.mysteryBoxLvl6);
        mysteryBox7 = view.findViewById(R.id.mysteryBoxLvl7);
        mysteryBox8 = view.findViewById(R.id.mysteryBoxLvl8);
        mysteryBox9 = view.findViewById(R.id.mysteryBoxLvl9);

        footOne = view.findViewById(R.id.footprint1ImageView);
        footTwo = view.findViewById(R.id.footprint2ImageView);
        footThree = view.findViewById(R.id.footprint3ImageView);

        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus(view);
            }
        });

        gemSocketOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 5) {
                    Toast.makeText(getContext(), "Socket available at level 5", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType(0);
                }
                clearFocus(view);
            }
        });

        gemSocketTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 10) {
                    Toast.makeText(getContext(), "Socket available at level 10", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType(1);
                }
                clearFocus(view);
            }
        });

        gemSocketThreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 15) {
                    Toast.makeText(getContext(), "Socket available at level 15", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType(2);
                }
                clearFocus(view);
            }
        });

        gemSocketFourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeLevel < 20) {
                    Toast.makeText(getContext(), "Socket available at level 20", Toast.LENGTH_SHORT).show();
                } else {
                    chooseSocketType(3);
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

                shoeTypeImageView.setScaleX(1.1f);
                shoeTypeImageView.setScaleY(1.1f);
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

        optimizeGstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (energy > 0) {
                    optimizeForGst();
                } else if (baseEff == 0 || baseLuck == 0 || baseComf == 0 || baseRes == 0) {
                    Toast.makeText(getContext(), "Base values must be greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Energy must be greater than 0", Toast.LENGTH_SHORT).show();
                }
                clearFocus(view);
            }
        });

        optimizeGstButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        optimizeGstButton.setVisibility(View.INVISIBLE);
                        optimizeGstTextView.setVisibility(View.INVISIBLE);
                        optimizeGstButtonShadow.setImageResource(R.drawable.start_button);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        optimizeGstButton.setVisibility(View.VISIBLE);
                        optimizeGstTextView.setVisibility(View.VISIBLE);
                        optimizeGstButtonShadow.setImageResource(R.drawable.start_button_shadow);
                        break;
                }
                return false;
            }
        });

        optimizeLuckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (energy > 0) {
                    optimizeForLuck();
                } else if (baseEff == 0 || baseLuck == 0 || baseComf == 0 || baseRes == 0) {
                    Toast.makeText(getContext(), "Base values must be greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Energy must be greater than 0", Toast.LENGTH_SHORT).show();
                }
                clearFocus(view);
            }
        });

        optimizeLuckButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        optimizeLuckButton.setVisibility(View.INVISIBLE);
                        optimizeLuckTextView.setVisibility(View.INVISIBLE);
                        optimizeLuckButtonShadow.setImageResource(R.drawable.start_button);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        optimizeLuckButton.setVisibility(View.VISIBLE);
                        optimizeLuckTextView.setVisibility(View.VISIBLE);
                        optimizeLuckButtonShadow.setImageResource(R.drawable.start_button_shadow);
                        break;
                }
                return false;
            }
        });

        changeComfGemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comfGemLvlForRepair == 1) {
                    comfGemLvlForRepair = 2;
                    comfGemHpRepairImageView.setImageResource(R.drawable.gem_comf_level2);
                    comfGemHpRepairImageView.setPadding(0, 0, 0, 0);
                    comfGemHpRepairTotalImageView.setImageResource(R.drawable.gem_comf_level2);
                    comfGemHpRepairTotalImageView.setPadding(0, 0, 0, 0);
                } else if (comfGemLvlForRepair == 2) {
                    comfGemLvlForRepair = 3;
                    comfGemHpRepairImageView.setImageResource(R.drawable.gem_comf_level3);
                    comfGemHpRepairImageView.setPadding(0, 0, 0,0);
                    comfGemHpRepairTotalImageView.setImageResource(R.drawable.gem_comf_level3);
                    comfGemHpRepairTotalImageView.setPadding(0, 0, 0,0);
                } else {
                    comfGemLvlForRepair = 1;
                    comfGemHpRepairImageView.setImageResource(R.drawable.gem_comf_level1);
                    comfGemHpRepairImageView.setPadding(0, (int) (4 * dpScale + 0.5f),0,0);
                    comfGemHpRepairTotalImageView.setImageResource(R.drawable.gem_comf_level1);
                    comfGemHpRepairTotalImageView.setPadding(0, (int) (4 * dpScale + 0.5f),0,0);
                }
                calcTotals();
            }
        });

        levelSeekbar.setProgress(shoeLevel - 1);

        loadPoints();
        updateType();
        updateRarity();

        if (update) {
            update = false;

            Dialog updateDialog = new Dialog(getActivity());

            updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            updateDialog.setCancelable(true);
            updateDialog.setContentView(R.layout.update_dialog_optimizer);
            updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            updateDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            ImageButton nextButton = updateDialog.findViewById(R.id.nextButton);

            updateDialog.show();

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateDialog.dismiss();
                }
            });
        }
        return view;
    }

    // updates UI depending on shoe rarity
    private void updateRarity() {
        switch (shoeRarity) {
            case UNCOMMON:
                shoeCircles.setImageResource(R.drawable.circles_uncommon);
                shoeRarityTextView.setText("Uncommon");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_uncommon);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_uncommon);
                footOne.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                effEditText.setHint("8 - 21.6");
                luckEditText.setHint("8 - 21.6");
                comfortEditText.setHint("8 - 21.6");
                resEditText.setHint("8 - 21.6");
                baseMin = 8;
                baseMax = 21.6f;
                for (int i = 0; i < 4; i++) {
                    if (gems.get(i).getSocketRarity() > 2) {
                        gems.get(i).setSocketRarity(2);
                    }
                }
                break;
            case RARE:
                shoeCircles.setImageResource(R.drawable.circles_rare);
                shoeRarityTextView.setText("Rare");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_rare);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_rare);
                footOne.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                effEditText.setHint("15 - 42");
                luckEditText.setHint("15 - 42");
                comfortEditText.setHint("15 - 42");
                resEditText.setHint("15 - 42");
                baseMin = 15;
                baseMax = 42f;
                for (int i = 0; i < 4; i++) {
                    if (gems.get(i).getSocketRarity() > 3) {
                        gems.get(i).setSocketRarity(3);
                    }
                }
                break;
            case EPIC:
                shoeCircles.setImageResource(R.drawable.circles_epic);
                shoeRarityTextView.setText("Epic");
                shoeRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeRarityButton.setImageResource(R.drawable.box_epic);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                shoeTypeButton.setImageResource(R.drawable.box_epic);
                footOne.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                footTwo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
                footThree.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white));
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
                shoeRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
                shoeRarityShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
                shoeRarityButton.setImageResource(R.drawable.box_common);
                shoeTypeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
                shoeTypeShadowTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
                shoeTypeButton.setImageResource(R.drawable.box_common);
                footOne.setColorFilter(ContextCompat.getColor(requireContext(), R.color.almost_black));
                footTwo.setColorFilter(ContextCompat.getColor(requireContext(), R.color.almost_black));
                footThree.setColorFilter(ContextCompat.getColor(requireContext(), R.color.almost_black));
                effEditText.setHint("1 - 10");
                luckEditText.setHint("1 - 10");
                comfortEditText.setHint("1 - 10");
                resEditText.setHint("1 - 10");
                baseMin = 1;
                baseMax = 10;
                for (int i = 0; i < 4; i++) {
                    if (gems.get(i).getSocketRarity() > 1) {
                        gems.get(i).setSocketRarity(1);
                    }
                }
        }

        // make sure bases are correct for shoe rarity
        if (baseEff < baseMin && baseEff != 0) {
            baseEff = baseMin;
            effEditText.setText(String.valueOf(baseEff));
        } else if (baseEff > baseMax) {
            baseEff = baseMax;
            effEditText.setText(String.valueOf(baseEff));
        }
        if (baseLuck < baseMin && baseLuck != 0) {
            baseLuck = baseMin;
            luckEditText.setText(String.valueOf(baseLuck));
        } else if (baseLuck > baseMax) {
            baseLuck = baseMax;
            luckEditText.setText(String.valueOf(baseLuck));
        }
        if (baseComf < baseMin && baseComf != 0) {
            baseComf = baseMin;
            comfortEditText.setText(String.valueOf(baseComf));
        } else if (baseComf > baseMax) {
            baseComf = baseMax;
            comfortEditText.setText(String.valueOf(baseComf));
        }
        if (baseRes < baseMin && baseRes != 0) {
            baseRes = baseMin;
            resEditText.setText(String.valueOf(baseRes));
        } else if (baseRes > baseMax) {
            baseRes = baseMax;
            resEditText.setText(String.valueOf(baseRes));
        }

        updateLevel();
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
    }

    // updates values depending on level
    private void updateLevel() {

        if (shoeLevel >= 5) {
            gemSocketOneLockPlus.setImageResource(gems.get(0).getGemImageSource());
            gemSocketOne.setImageResource(gems.get(0).getSocketImageSource());
            gemSocketOneLockPlus.setPadding(0, (int) (gems.get(0).getTopPadding() * dpScale + 0.5f), 0,
                    (int) (gems.get(0).getBottomPadding() * dpScale + 0.5f));
        } else {
            gemSocketOneLockPlus.setImageResource(R.drawable.gem_socket_lock);
            gemSocketOne.setImageResource(R.drawable.gem_socket_gray_0);
            gemSocketOneLockPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, (int) (2 * dpScale + 0.5f));
        }

        if (shoeLevel >= 10) {
            gemSocketTwoLockPlus.setImageResource(gems.get(1).getGemImageSource());
            gemSocketTwo.setImageResource(gems.get(1).getSocketImageSource());
            gemSocketTwoLockPlus.setPadding(0, (int) (gems.get(1).getTopPadding() * dpScale + 0.5f), 0,
                    (int) (gems.get(1).getBottomPadding() * dpScale + 0.5f));
        } else {
            gemSocketTwoLockPlus.setImageResource(R.drawable.gem_socket_lock);
            gemSocketTwo.setImageResource(R.drawable.gem_socket_gray_0);
            gemSocketTwoLockPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, (int) (2 * dpScale + 0.5f));
        }

        if (shoeLevel >= 15) {
            gemSocketThreeLockPlus.setImageResource(gems.get(2).getGemImageSource());
            gemSocketThree.setImageResource(gems.get(2).getSocketImageSource());
            gemSocketThreeLockPlus.setPadding(0, (int) (gems.get(2).getTopPadding() * dpScale + 0.5f), 0,
                    (int) (gems.get(2).getBottomPadding() * dpScale + 0.5f));
        } else {
            gemSocketThreeLockPlus.setImageResource(R.drawable.gem_socket_lock);
            gemSocketThree.setImageResource(R.drawable.gem_socket_gray_0);
            gemSocketThreeLockPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, (int) (2 * dpScale + 0.5f));
        }

        if (shoeLevel >= 20) {
            gemSocketFourLockPlus.setImageResource(gems.get(3).getGemImageSource());
            gemSocketFour.setImageResource(gems.get(3).getSocketImageSource());
            gemSocketFourLockPlus.setPadding(0, (int) (gems.get(3).getTopPadding() * dpScale + 0.5f), 0,
                    (int) (gems.get(3).getBottomPadding() * dpScale + 0.5f));
        } else {
            gemSocketFourLockPlus.setImageResource(R.drawable.gem_socket_lock);
            gemSocketFour.setImageResource(R.drawable.gem_socket_gray_0);
            gemSocketFourLockPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, (int) (2 * dpScale + 0.5f));
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

    // dialog for choosing socket and gem
    private void chooseSocketType(final int socketNum) {
        final int tempSocketType, tempSocketRarity, tempGemMounted;
        final float tempBasePoints;

        saveNewGem = false;

        tempSocketType = gems.get(socketNum).getSocketType();
        tempSocketRarity = gems.get(socketNum).getSocketRarity();
        tempGemMounted = gems.get(socketNum).getMountedGem();
        tempBasePoints = gems.get(socketNum).getBasePoints();

        Dialog choseGem = new Dialog(requireActivity());

        choseGem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        choseGem.setCancelable(true);
        choseGem.setContentView(R.layout.choose_gem_dialog);
        choseGem.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        choseGem.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        choseGem.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!saveNewGem) {
                    gems.get(socketNum).setSocketType(tempSocketType);
                    gems.get(socketNum).setSocketRarity(tempSocketRarity);
                    gems.get(socketNum).setMountedGem(tempGemMounted);
                    gems.get(socketNum).setBasePoints(tempBasePoints);
                }
            }
        });

        ImageButton effTypeButton = choseGem.findViewById(R.id.effType);
        ImageButton luckTypeButton = choseGem.findViewById(R.id.luckType);
        ImageButton comfTypeButton = choseGem.findViewById(R.id.comfType);
        ImageButton resTypeButton = choseGem.findViewById(R.id.resType);

        ImageView effTypeSelected = choseGem.findViewById(R.id.effTypeSelected);
        ImageView luckTypeSelected = choseGem.findViewById(R.id.luckTypeSelected);
        ImageView comfTypeSelected = choseGem.findViewById(R.id.comfTypeSelected);
        ImageView resTypeSelected = choseGem.findViewById(R.id.resTypeSelected);

        ImageButton lvl1SelectedButton = choseGem.findViewById(R.id.gemLevel1Selected);
        ImageButton lvl2SelectedButton = choseGem.findViewById(R.id.gemLevel2Selected);
        ImageButton lvl3SelectedButton = choseGem.findViewById(R.id.gemLevel3Selected);
        ImageButton lvl4SelectedButton = choseGem.findViewById(R.id.gemLevel4Selected);
        ImageButton lvl5SelectedButton = choseGem.findViewById(R.id.gemLevel5Selected);
        ImageButton lvl6SelectedButton = choseGem.findViewById(R.id.gemLevel6Selected);

        ImageView lvl1Gem = choseGem.findViewById(R.id.gemLevel1);
        ImageView lvl2Gem = choseGem.findViewById(R.id.gemLevel2);
        ImageView lvl3Gem = choseGem.findViewById(R.id.gemLevel3);
        ImageView lvl4Gem = choseGem.findViewById(R.id.gemLevel4);
        ImageView lvl5Gem = choseGem.findViewById(R.id.gemLevel5);
        ImageView lvl6Gem = choseGem.findViewById(R.id.gemLevel6);

        ImageButton gemSocket = choseGem.findViewById(R.id.gemSocket);
        ImageView gemSocketPlus = choseGem.findViewById(R.id.socketPlus);

        TextView decreaseRarityTextView = choseGem.findViewById(R.id.minusTextView);
        TextView increaseRarityTextView = choseGem.findViewById(R.id.plusTextView);
        Button decreaseRarityButton = choseGem.findViewById(R.id.decreaseRarityButton);
        Button increaseRarityButton = choseGem.findViewById(R.id.increaseRarityButton);
        Button showCalcsButton = choseGem.findViewById(R.id.seeCalcsButton);

        TextView gemDetailsTextView = choseGem.findViewById(R.id.gemDetailsTextView);
        TextView socketDetailsTextView = choseGem.findViewById(R.id.socketDetailsTextView);
        TextView totalGemPointsTextView = choseGem.findViewById(R.id.totalPointsTextView);

        ImageButton saveButton = choseGem.findViewById(R.id.saveGemButton);

        ArrayList<ImageButton> selectedButtons = new ArrayList<>();

        selectedButtons.add(lvl1SelectedButton);
        selectedButtons.add(lvl2SelectedButton);
        selectedButtons.add(lvl3SelectedButton);
        selectedButtons.add(lvl4SelectedButton);
        selectedButtons.add(lvl5SelectedButton);
        selectedButtons.add(lvl6SelectedButton);

        gemSocket.setImageResource(gems.get(socketNum).getSocketImageSource());

        switch (tempSocketType) {
            case EFF:
                gems.get(socketNum).setBasePoints(baseEff);

                effTypeSelected.setVisibility(View.VISIBLE);
                luckTypeSelected.setVisibility(View.INVISIBLE);
                comfTypeSelected.setVisibility(View.INVISIBLE);
                resTypeSelected.setVisibility(View.INVISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_eff_level1);
                lvl2Gem.setImageResource(R.drawable.gem_eff_level2);
                lvl3Gem.setImageResource(R.drawable.gem_eff_level3);
                lvl4Gem.setImageResource(R.drawable.gem_eff_level4);
                lvl5Gem.setImageResource(R.drawable.gem_eff_level5);
                lvl6Gem.setImageResource(R.drawable.gem_eff_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_eff);
                }
                break;
            case LUCK:
                gems.get(socketNum).setBasePoints(baseLuck);

                effTypeSelected.setVisibility(View.INVISIBLE);
                luckTypeSelected.setVisibility(View.VISIBLE);
                comfTypeSelected.setVisibility(View.INVISIBLE);
                resTypeSelected.setVisibility(View.INVISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_luck_level1);
                lvl2Gem.setImageResource(R.drawable.gem_luck_level2);
                lvl3Gem.setImageResource(R.drawable.gem_luck_level3);
                lvl4Gem.setImageResource(R.drawable.gem_luck_level4);
                lvl5Gem.setImageResource(R.drawable.gem_luck_level5);
                lvl6Gem.setImageResource(R.drawable.gem_luck_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_luck);
                }
                break;
            case COMF:
                gems.get(socketNum).setBasePoints(baseComf);

                effTypeSelected.setVisibility(View.INVISIBLE);
                luckTypeSelected.setVisibility(View.INVISIBLE);
                comfTypeSelected.setVisibility(View.VISIBLE);
                resTypeSelected.setVisibility(View.INVISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_comf_level1);
                lvl2Gem.setImageResource(R.drawable.gem_comf_level2);
                lvl3Gem.setImageResource(R.drawable.gem_comf_level3);
                lvl4Gem.setImageResource(R.drawable.gem_comf_level4);
                lvl5Gem.setImageResource(R.drawable.gem_comf_level5);
                lvl6Gem.setImageResource(R.drawable.gem_comf_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_comf);
                }
                break;
            case RES:
                gems.get(socketNum).setBasePoints(baseRes);

                effTypeSelected.setVisibility(View.INVISIBLE);
                luckTypeSelected.setVisibility(View.INVISIBLE);
                comfTypeSelected.setVisibility(View.INVISIBLE);
                resTypeSelected.setVisibility(View.VISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_res_level1);
                lvl2Gem.setImageResource(R.drawable.gem_res_level2);
                lvl3Gem.setImageResource(R.drawable.gem_res_level3);
                lvl4Gem.setImageResource(R.drawable.gem_res_level4);
                lvl5Gem.setImageResource(R.drawable.gem_res_level5);
                lvl6Gem.setImageResource(R.drawable.gem_res_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_res);
                }
                break;
            default:
                gems.get(socketNum).setBasePoints(0);

                effTypeSelected.setVisibility(View.INVISIBLE);
                luckTypeSelected.setVisibility(View.INVISIBLE);
                comfTypeSelected.setVisibility(View.INVISIBLE);
                resTypeSelected.setVisibility(View.INVISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_grey_level1);
                lvl2Gem.setImageResource(R.drawable.gem_grey_level2);
                lvl3Gem.setImageResource(R.drawable.gem_grey_level3);
                lvl4Gem.setImageResource(R.drawable.gem_grey_level4);
                lvl5Gem.setImageResource(R.drawable.gem_grey_level5);
                lvl6Gem.setImageResource(R.drawable.gem_grey_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_common);
                }
        }

        switch (tempGemMounted) {
            case 1:
                lvl1SelectedButton.setAlpha(1.0f);
                lvl2SelectedButton.setAlpha(0.0f);
                lvl3SelectedButton.setAlpha(0.0f);
                lvl4SelectedButton.setAlpha(0.0f);
                lvl5SelectedButton.setAlpha(0.0f);
                lvl6SelectedButton.setAlpha(0.0f);
                gemSocketPlus.setPadding(0, (int) (3 * dpScale + 0.5f), 0, (int) (3 * dpScale + 0.5f));
                break;
            case 2:
                lvl1SelectedButton.setAlpha(0.0f);
                lvl2SelectedButton.setAlpha(1.0f);
                lvl3SelectedButton.setAlpha(0.0f);
                lvl4SelectedButton.setAlpha(0.0f);
                lvl5SelectedButton.setAlpha(0.0f);
                lvl6SelectedButton.setAlpha(0.0f);
                break;
            case 3:
                lvl1SelectedButton.setAlpha(0.0f);
                lvl2SelectedButton.setAlpha(0.0f);
                lvl3SelectedButton.setAlpha(1.0f);
                lvl4SelectedButton.setAlpha(0.0f);
                lvl5SelectedButton.setAlpha(0.0f);
                lvl6SelectedButton.setAlpha(0.0f);
                break;
            case 4:
                lvl1SelectedButton.setAlpha(0.0f);
                lvl2SelectedButton.setAlpha(0.0f);
                lvl3SelectedButton.setAlpha(0.0f);
                lvl4SelectedButton.setAlpha(1.0f);
                lvl5SelectedButton.setAlpha(0.0f);
                lvl6SelectedButton.setAlpha(0.0f);
                gemSocketPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, 0);
                break;
            case 5:
                lvl1SelectedButton.setAlpha(0.0f);
                lvl2SelectedButton.setAlpha(0.0f);
                lvl3SelectedButton.setAlpha(0.0f);
                lvl4SelectedButton.setAlpha(0.0f);
                lvl5SelectedButton.setAlpha(1.0f);
                lvl6SelectedButton.setAlpha(0.0f);
                gemSocketPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, 0);
                break;
            case 6:
                lvl1SelectedButton.setAlpha(0.0f);
                lvl2SelectedButton.setAlpha(0.0f);
                lvl3SelectedButton.setAlpha(0.0f);
                lvl4SelectedButton.setAlpha(0.0f);
                lvl5SelectedButton.setAlpha(0.0f);
                lvl6SelectedButton.setAlpha(1.0f);
                break;
            default:
                lvl1SelectedButton.setAlpha(0.0f);
                lvl2SelectedButton.setAlpha(0.0f);
                lvl3SelectedButton.setAlpha(0.0f);
                lvl4SelectedButton.setAlpha(0.0f);
                lvl5SelectedButton.setAlpha(0.0f);
                lvl6SelectedButton.setAlpha(0.0f);
                break;
        }

        gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());

        if (tempSocketRarity == 0) {
            decreaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
        } else if ((tempSocketRarity == 1 && shoeRarity <= COMMON)
                || (tempSocketRarity == 2 && shoeRarity <= UNCOMMON)
                || (tempSocketRarity == 3 && shoeRarity <= RARE)
                || tempSocketRarity == 4) {
            increaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
        }

        gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
        socketDetailsTextView.setText(gems.get(socketNum).getSocketParamsString());
        totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());

        choseGem.show();

        effTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effTypeSelected.setVisibility(View.VISIBLE);
                luckTypeSelected.setVisibility(View.INVISIBLE);
                comfTypeSelected.setVisibility(View.INVISIBLE);
                resTypeSelected.setVisibility(View.INVISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_eff_level1);
                lvl2Gem.setImageResource(R.drawable.gem_eff_level2);
                lvl3Gem.setImageResource(R.drawable.gem_eff_level3);
                lvl4Gem.setImageResource(R.drawable.gem_eff_level4);
                lvl5Gem.setImageResource(R.drawable.gem_eff_level5);
                lvl6Gem.setImageResource(R.drawable.gem_eff_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_eff);
                }

                gems.get(socketNum).setSocketType(EFF);
                gems.get(socketNum).setBasePoints(baseEff);
                gemSocket.setImageResource(gems.get(socketNum).getSocketImageSource());
                gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                socketDetailsTextView.setText(gems.get(socketNum).getSocketParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        luckTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effTypeSelected.setVisibility(View.INVISIBLE);
                luckTypeSelected.setVisibility(View.VISIBLE);
                comfTypeSelected.setVisibility(View.INVISIBLE);
                resTypeSelected.setVisibility(View.INVISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_luck_level1);
                lvl2Gem.setImageResource(R.drawable.gem_luck_level2);
                lvl3Gem.setImageResource(R.drawable.gem_luck_level3);
                lvl4Gem.setImageResource(R.drawable.gem_luck_level4);
                lvl5Gem.setImageResource(R.drawable.gem_luck_level5);
                lvl6Gem.setImageResource(R.drawable.gem_luck_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_luck);
                }

                gems.get(socketNum).setSocketType(LUCK);
                gems.get(socketNum).setBasePoints(baseLuck);
                gemSocket.setImageResource(gems.get(socketNum).getSocketImageSource());
                gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                socketDetailsTextView.setText(gems.get(socketNum).getSocketParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        comfTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effTypeSelected.setVisibility(View.INVISIBLE);
                luckTypeSelected.setVisibility(View.INVISIBLE);
                comfTypeSelected.setVisibility(View.VISIBLE);
                resTypeSelected.setVisibility(View.INVISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_comf_level1);
                lvl2Gem.setImageResource(R.drawable.gem_comf_level2);
                lvl3Gem.setImageResource(R.drawable.gem_comf_level3);
                lvl4Gem.setImageResource(R.drawable.gem_comf_level4);
                lvl5Gem.setImageResource(R.drawable.gem_comf_level5);
                lvl6Gem.setImageResource(R.drawable.gem_comf_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_comf);
                }

                gems.get(socketNum).setSocketType(COMF);
                gems.get(socketNum).setBasePoints(baseComf);
                gemSocket.setImageResource(gems.get(socketNum).getSocketImageSource());
                gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                socketDetailsTextView.setText(gems.get(socketNum).getSocketParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        resTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effTypeSelected.setVisibility(View.INVISIBLE);
                luckTypeSelected.setVisibility(View.INVISIBLE);
                comfTypeSelected.setVisibility(View.INVISIBLE);
                resTypeSelected.setVisibility(View.VISIBLE);

                lvl1Gem.setImageResource(R.drawable.gem_res_level1);
                lvl2Gem.setImageResource(R.drawable.gem_res_level2);
                lvl3Gem.setImageResource(R.drawable.gem_res_level3);
                lvl4Gem.setImageResource(R.drawable.gem_res_level4);
                lvl5Gem.setImageResource(R.drawable.gem_res_level5);
                lvl6Gem.setImageResource(R.drawable.gem_res_level6);

                for (int i = 0; i < 6; i++) {
                    selectedButtons.get(i).setImageResource(R.drawable.circles_res);
                }

                gems.get(socketNum).setSocketType(RES);
                gems.get(socketNum).setBasePoints(baseRes);
                gemSocket.setImageResource(gems.get(socketNum).getSocketImageSource());
                gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                socketDetailsTextView.setText(gems.get(socketNum).getSocketParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        decreaseRarityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getSocketRarity() > 0) {
                    gems.get(socketNum).setSocketRarity(gems.get(socketNum).getSocketRarity() - 1);
                    gemSocket.setImageResource(gems.get(socketNum).getSocketImageSource());
                    increaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
                }
                if (gems.get(socketNum).getSocketRarity() == 0) {
                    decreaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
                }
                socketDetailsTextView.setText(gems.get(socketNum).getSocketParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        increaseRarityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getSocketRarity() == 0) {
                    gems.get(socketNum).setSocketRarity(1);
                    if (shoeRarity == COMMON) {
                        increaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
                    }
                } else if (gems.get(socketNum).getSocketRarity() == 1 && shoeRarity > COMMON) {
                    gems.get(socketNum).setSocketRarity(2);
                    if (shoeRarity <= UNCOMMON) {
                        increaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
                    }
                } else if (gems.get(socketNum).getSocketRarity() == 2 && shoeRarity > UNCOMMON) {
                    gems.get(socketNum).setSocketRarity(3);
                    if (shoeRarity <= RARE) {
                        increaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
                    }
                } else if (gems.get(socketNum).getSocketRarity() == 3 && shoeRarity > RARE) {
                    gems.get(socketNum).setSocketRarity(4);
                    increaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
                }

                if (gems.get(socketNum).getSocketRarity() == 0) {
                    decreaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
                } else {
                    decreaseRarityTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
                }

                gemSocket.setImageResource(gems.get(socketNum).getSocketImageSource());
                socketDetailsTextView.setText(gems.get(socketNum).getSocketParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewGem = true;
                choseGem.dismiss();
                updateLevel();
            }
        });

        lvl1SelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getMountedGem() == 1) {
                    gems.get(socketNum).setMountedGem(0);
                    gemSocketPlus.setImageResource(R.drawable.gem_socket_plus);
                    gemSocketPlus.setPadding((int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f));
                    lvl1SelectedButton.setAlpha(0.0f);
                } else {
                    gems.get(socketNum).setMountedGem(1);
                    gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());
                    gemSocketPlus.setPadding(0, (int) (3 * dpScale + 0.5f), 0, (int) (3 * dpScale + 0.5f));

                    lvl1SelectedButton.setAlpha(1.0f);
                    lvl2SelectedButton.setAlpha(0.0f);
                    lvl3SelectedButton.setAlpha(0.0f);
                    lvl4SelectedButton.setAlpha(0.0f);
                    lvl5SelectedButton.setAlpha(0.0f);
                    lvl6SelectedButton.setAlpha(0.0f);
                }

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        lvl2SelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getMountedGem() == 2) {
                    gems.get(socketNum).setMountedGem(0);
                    gemSocketPlus.setImageResource(R.drawable.gem_socket_plus);
                    gemSocketPlus.setPadding((int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f));
                    lvl2SelectedButton.setAlpha(0.0f);
                } else {
                    gems.get(socketNum).setMountedGem(2);
                    gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());
                    gemSocketPlus.setPadding(0,0,0,0);

                    lvl1SelectedButton.setAlpha(0.0f);
                    lvl2SelectedButton.setAlpha(1.0f);
                    lvl3SelectedButton.setAlpha(0.0f);
                    lvl4SelectedButton.setAlpha(0.0f);
                    lvl5SelectedButton.setAlpha(0.0f);
                    lvl6SelectedButton.setAlpha(0.0f);
                }

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        lvl3SelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getMountedGem() == 3) {
                    gems.get(socketNum).setMountedGem(0);
                    gemSocketPlus.setImageResource(R.drawable.gem_socket_plus);
                    gemSocketPlus.setPadding((int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f));
                    lvl3SelectedButton.setAlpha(0.0f);
                } else {
                    gems.get(socketNum).setMountedGem(3);
                    gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());
                    gemSocketPlus.setPadding(0,0,0,0);

                    lvl1SelectedButton.setAlpha(0.0f);
                    lvl2SelectedButton.setAlpha(0.0f);
                    lvl3SelectedButton.setAlpha(1.0f);
                    lvl4SelectedButton.setAlpha(0.0f);
                    lvl5SelectedButton.setAlpha(0.0f);
                    lvl6SelectedButton.setAlpha(0.0f);
                }

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        lvl4SelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getMountedGem() == 4) {
                    gems.get(socketNum).setMountedGem(0);
                    gemSocketPlus.setImageResource(R.drawable.gem_socket_plus);
                    gemSocketPlus.setPadding((int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f));
                    lvl4SelectedButton.setAlpha(0.0f);
                } else {
                    gems.get(socketNum).setMountedGem(4);
                    gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());
                    gemSocketPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, 0);

                    lvl1SelectedButton.setAlpha(0.0f);
                    lvl2SelectedButton.setAlpha(0.0f);
                    lvl3SelectedButton.setAlpha(0.0f);
                    lvl4SelectedButton.setAlpha(1.0f);
                    lvl5SelectedButton.setAlpha(0.0f);
                    lvl6SelectedButton.setAlpha(0.0f);
                }

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        lvl5SelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getMountedGem() == 5) {
                    gems.get(socketNum).setMountedGem(0);
                    gemSocketPlus.setImageResource(R.drawable.gem_socket_plus);
                    gemSocketPlus.setPadding((int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f));
                    lvl5SelectedButton.setAlpha(0.0f);
                } else {
                    gems.get(socketNum).setMountedGem(5);
                    gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());
                    gemSocketPlus.setPadding(0, (int) (2 * dpScale + 0.5f), 0, 0);

                    lvl1SelectedButton.setAlpha(0.0f);
                    lvl2SelectedButton.setAlpha(0.0f);
                    lvl3SelectedButton.setAlpha(0.0f);
                    lvl4SelectedButton.setAlpha(0.0f);
                    lvl5SelectedButton.setAlpha(1.0f);
                    lvl6SelectedButton.setAlpha(0.0f);
                }

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        lvl6SelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getMountedGem() == 6) {
                    gems.get(socketNum).setMountedGem(0);
                    gemSocketPlus.setImageResource(R.drawable.gem_socket_plus);
                    gemSocketPlus.setPadding((int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f));
                    lvl6SelectedButton.setAlpha(0.0f);
                } else {
                    gems.get(socketNum).setMountedGem(6);
                    gemSocketPlus.setImageResource(gems.get(socketNum).getGemImageSource());
                    gemSocketPlus.setPadding(0,0,0,0);

                    lvl1SelectedButton.setAlpha(0.0f);
                    lvl2SelectedButton.setAlpha(0.0f);
                    lvl3SelectedButton.setAlpha(0.0f);
                    lvl4SelectedButton.setAlpha(0.0f);
                    lvl5SelectedButton.setAlpha(0.0f);
                    lvl6SelectedButton.setAlpha(1.0f);
                }

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        gemSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gems.get(socketNum).getMountedGem() == 0) {
                    Toast.makeText(getContext(), "Select a gem to mount", Toast.LENGTH_SHORT).show();
                } else {
                    gems.get(socketNum).setMountedGem(0);
                    gemSocketPlus.setImageResource(R.drawable.gem_socket_plus);
                    gemSocketPlus.setPadding((int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f), (int) (2 * dpScale + 0.5f));

                    lvl1SelectedButton.setAlpha(0.0f);
                    lvl2SelectedButton.setAlpha(0.0f);
                    lvl3SelectedButton.setAlpha(0.0f);
                    lvl4SelectedButton.setAlpha(0.0f);
                    lvl5SelectedButton.setAlpha(0.0f);
                    lvl6SelectedButton.setAlpha(0.0f);
                }

                gemDetailsTextView.setText(gems.get(socketNum).getGemParamsString());
                totalGemPointsTextView.setText(gems.get(socketNum).getTotalPointsString());
            }
        });

        showCalcsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGemCalcs(socketNum);
            }
        });
    }

    // shows detailed gem calculations
    private void showGemCalcs(int socketNum) {
        int points, percent;
        String socketRarity;

        Dialog showGemCalcDetails = new Dialog(requireActivity());

        showGemCalcDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showGemCalcDetails.setCancelable(true);
        showGemCalcDetails.setContentView(R.layout.gem_calcs_dialog);
        showGemCalcDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        showGemCalcDetails.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView calcsSocket = showGemCalcDetails.findViewById(R.id.seeCalcsSocketImageView);
        ImageView calcsGem = showGemCalcDetails.findViewById(R.id.seeCalcsGemImageView);

        TextView calcsGemLvl = showGemCalcDetails.findViewById(R.id.seeCalcsTitleGemTextView);
        TextView calcsGemInfo = showGemCalcDetails.findViewById(R.id.seeCalcsGemAddedPointsTextView);
        TextView calcsGemCalcs = showGemCalcDetails.findViewById(R.id.seeCalcsGemTotalCalcTextView);

        TextView calcsSocketRarity = showGemCalcDetails.findViewById(R.id.seeCalcsSocketTextView);
        TextView calcsSocketInfo = showGemCalcDetails.findViewById(R.id.seeCalcsSocketMultiplierTextView);
        TextView calcsSocketCalcs = showGemCalcDetails.findViewById(R.id.seeCalcsSocketTotalCalcTextView);
        TextView calcsTotalPoints = showGemCalcDetails.findViewById(R.id.seeCalcsTotalTextView);

        showGemCalcDetails.show();

        switch (gems.get(socketNum).getMountedGem()) {
            case 1:
                points = 2;
                percent = 5;
                break;
            case 2:
                points = 8;
                percent = 70;
                break;
            case 3:
                points = 25;
                percent = 220;
                break;
            case 4:
                points = 72;
                percent = 600;
                break;
            case 5:
                points = 200;
                percent = 1400;
                break;
            case 6:
                points = 400;
                percent = 4300;
                break;
            default:
                points = 0;
                percent = 0;
                break;
        }

        switch (gems.get(socketNum).getSocketRarity()) {
            case 1:
                socketRarity = "Uncommon Socket";
                break;
            case 2:
                socketRarity = "Rare Socket";
                break;
            case 3:
                socketRarity = "Epic Socket";
                break;
            case 4:
                socketRarity = "Legendary Socket";
                break;
            default:
                socketRarity = "Common Socket";
        }

        String gemLevel = "Level " + gems.get(socketNum).getMountedGem() + " Gem";
        String gemInfo = "+ " + percent + "% base\n+ " + points + " points";
        String gemCalcs = "(" + gems.get(socketNum).getBasePoints() + " × " + percent + "%) + "
                + points + " = " + gems.get(socketNum).getGemParams();
        String socketInfo = "Gem points " + gems.get(socketNum).getSocketParamsString();
        String socketCalcs = gems.get(socketNum).getGemParams() + " × " + gems.get(socketNum).getSocketParams()
                + " = " + gems.get(socketNum).getTotalPoints();

        calcsSocket.setImageResource(gems.get(socketNum).getSocketImageSource());
        calcsGem.setImageResource(gems.get(socketNum).getGemImageSource());
        calcsGem.setPadding(0, (int) (gems.get(socketNum).getTopPadding() * dpScale + 0.5f), 0,
                (int) (gems.get(socketNum).getBottomPadding() * dpScale + 0.5f));

        calcsGemLvl.setText(gemLevel);
        calcsGemInfo.setText(gemInfo);
        calcsGemCalcs.setText(gemCalcs);

        calcsSocketRarity.setText(socketRarity);
        calcsSocketInfo.setText(socketInfo);
        calcsSocketCalcs.setText(socketCalcs);
        calcsTotalPoints.setText(gems.get(socketNum).getTotalPointsString());

    }

    // calculate gst earnings, durability lost, repair cost, and mb chance
    private void calcTotals() {

        if (baseEff == 0 || baseLuck == 0 || baseComf == 0 || baseRes == 0) {
            return;
        }

        int durabilityLost;
        float repairCostDurability, gstTotal, gstIncome;
        double hpRatio, repairCostHp;


        float totalEff = Float.parseFloat(effTotalTextView.getText().toString());
        float totalComf = Float.parseFloat(comfortTotalTextView.getText().toString());
        float totalRes = Float.parseFloat(resTotalTextView.getText().toString());

        switch (shoeType) {
            case JOGGER:
                gstTotal = (float) (Math.floor(energy * Math.pow(totalEff, 0.48) * 10) / 10);
                break;
            case RUNNER:
                gstTotal = (float) (Math.floor(energy * Math.pow(totalEff, 0.49) * 10) / 10);
                break;
            case TRAINER:
                gstTotal = (float) (Math.floor(energy * Math.pow(totalEff, 0.492) * 10) / 10);
                break;
            default:
                gstTotal = (float) (Math.floor(energy * Math.pow(totalEff, 0.47) * 10) / 10);
        }

        durabilityLost = (int) Math.round(energy * ((2.22 * Math.exp(-totalRes / 30.9)) + (2.8 * Math.exp(-totalRes / 6.2)) + 0.4));

        if (durabilityLost < 1) {
            durabilityLost = 1;
        }

        repairCostDurability = getRepairCost() * durabilityLost;

        hpCalcs(totalComf);

        hpRatio = hpLoss / hpPercentRestored;
        hpLoss = Math.round(hpLoss * 100.0) / 100.0;
        repairCostHp = Math.round(gstCostBasedOnGem * hpRatio * 10.0) / 10.0;
        repairCostDurability = (float) (Math.round(repairCostDurability * 10.0) / 10.0);
        gstIncome = (float) (Math.round((gstTotal - repairCostDurability - repairCostHp) * 10.0) / 10.0);

        calcMbChances();

        if (hpLoss == 0) {
            hpLossTextView.setText("UNK");
            repairCostHpTextView.setText("UNK");
            gemMultipleTextView.setText("0");
            gemMultipleTotalTextView.setText("-  0");
        } else {
            String multiplier = String.valueOf(Math.round(hpRatio * 100.0) / 100.0);
            hpLossTextView.setText(String.valueOf(hpLoss));
            repairCostHpTextView.setText(String.valueOf(repairCostHp));
            gemMultipleTextView.setText(multiplier);
            multiplier = "-  " + multiplier;
            gemMultipleTotalTextView.setText(multiplier);
        }

        gstEarnedTextView.setText(String.valueOf(gstTotal));
        durabilityLossTextView.setText(String.valueOf(durabilityLost));
        repairCostDurTextView.setText(String.valueOf(repairCostDurability));
        gstIncomeTextView.setText(String.valueOf(gstIncome));

        if (gstTotal > gstLimit) {
            gstEarnedTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
        } else {
            gstEarnedTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
        }
    }

    // finds the point allocation that is most profitable
    private void optimizeForGst() {
        double gstProfit, energyCo;
        int optimalAddedEff = 0;
        int optimalAddedRes = 0;
        int optimalAddedComf = 0;

        final int localPoints = shoeLevel * 2 * shoeRarity;
        double maxProfit = 0;

        float localEff = baseEff + gemEff;
        float localComf = baseComf + gemComf;
        float localRes = baseRes + gemRes;
        int localAddedEff = 0;
        int localAddedComf = 0;
        int localAddedRes;

        switch (shoeType) {
            case JOGGER:
                energyCo = 0.48f;
                break;
            case RUNNER:
                energyCo = 0.49f;
                break;
            case TRAINER:
                energyCo = 0.492f;
                break;
            default:
                energyCo = 0.47f;
        }

        // O(n^2) w/ max 45,150 calcs... yikes
        while (localAddedEff <= localPoints) {
            while (localAddedComf <= localPoints - localAddedEff) {
                localAddedRes = localPoints - localAddedComf - localAddedEff;

                hpCalcs(localComf + localAddedComf);
                hpLoss = Math.round(hpLoss * 100.0) / 100.0;

                gstProfit = (Math.floor(energy * Math.pow((localEff + localAddedEff), energyCo) * 10) / 10) -
                        (getRepairCost() * (int) Math.round(energy * ((2.22 * Math.exp(-(localAddedRes + localRes) / 30.9)) + (2.8 * Math.exp(-(localAddedRes + localRes) / 6.2)) + 0.4))) -
                        (Math.round(gstCostBasedOnGem * (hpLoss / hpPercentRestored) * 10.0) / 10.0);

                if (gstProfit > maxProfit) {
                    optimalAddedEff = localAddedEff;
                    optimalAddedComf = localAddedComf;
                    optimalAddedRes = localAddedRes;
                    maxProfit = gstProfit;
                }

                localAddedComf ++;
            }
            localAddedComf = 0;
            localAddedEff++;
        }

        addedEff = optimalAddedEff;
        addedRes = optimalAddedRes;
        addedLuck = 0;
        addedComf = optimalAddedComf;
        updatePoints();
    }

    // optimizes for most luck with no GST loss
    private void optimizeForLuck() {
        final int localPoints = shoeLevel * 2 * shoeRarity;

        double energyCo, gstProfit;
        int localAddedEff, localAddedComf, localAddedRes;

        int optimalAddedEff = 0;
        int optimalAddedRes = 0;
        int optimalAddedComf = 0;
        int pointsSpent = 0;

        float localEff = baseEff + gemEff;
        float localComf = baseComf + gemComf;
        float localRes = baseRes + gemRes;

        switch (shoeType) {
            case JOGGER:
                energyCo = 0.48f;
                break;
            case RUNNER:
                energyCo = 0.49f;
                break;
            case TRAINER:
                energyCo = 0.492f;
                break;
            default:
                energyCo = 0.47f;
        }

        hpCalcs(localComf);
        gstProfit = (Math.floor(energy * Math.pow(localEff, energyCo) * 10) / 10) -
                (getRepairCost() * (int) Math.round(energy * ((2.22 * Math.exp(-localRes / 30.9)) + (2.8 * Math.exp(-localRes / 6.2)) + 0.4))) -
                (Math.round(gstCostBasedOnGem * ((Math.round(hpLoss * 100.0) / 100.0) / hpPercentRestored) * 10.0) / 10.0);

        // favors eff, but is efficient (see what i did there)
        while (gstProfit <= 0 && pointsSpent < localPoints) {
            pointsSpent++;
            localAddedEff = pointsSpent;
            localAddedComf = 0;
            localAddedRes = 0;

            hpCalcs(localComf + localAddedComf);
            gstProfit = (Math.floor(energy * Math.pow((localEff + localAddedEff), energyCo) * 10) / 10) -
                    (getRepairCost() * (int) Math.round(energy * ((2.22 * Math.exp(-(localAddedRes + localRes) / 30.9)) + (2.8 * Math.exp(-(localAddedRes + localRes) / 6.2)) + 0.4))) -
                    (Math.round(gstCostBasedOnGem * ((Math.round(hpLoss * 100.0) / 100.0) / hpPercentRestored) * 10.0) / 10.0);

            if (gstProfit > 0) {
                optimalAddedEff = localAddedEff;
                optimalAddedComf = localAddedComf;
                optimalAddedRes = localAddedRes;
                break;
            }

            while (localAddedEff > 0) {
                localAddedEff--;
                localAddedComf++;

                hpCalcs(localComf + localAddedComf);
                gstProfit = (Math.floor(energy * Math.pow((localEff + localAddedEff), energyCo) * 10) / 10) -
                        (getRepairCost() * (int) Math.round(energy * ((2.22 * Math.exp(-(localAddedRes + localRes) / 30.9)) + (2.8 * Math.exp(-(localAddedRes + localRes) / 6.2)) + 0.4))) -
                        (Math.round(gstCostBasedOnGem * ((Math.round(hpLoss * 100.0) / 100.0) / hpPercentRestored) * 10.0) / 10.0);

                if (gstProfit > 0) {
                    optimalAddedEff = localAddedEff;
                    optimalAddedComf = localAddedComf;
                    optimalAddedRes = localAddedRes;
                    break;
                }

                while (localAddedComf > 0) {
                    localAddedComf--;
                    localAddedRes++;

                    hpCalcs(localComf + localAddedComf);
                    gstProfit = (Math.floor(energy * Math.pow((localEff + localAddedEff), energyCo) * 10) / 10) -
                            (getRepairCost() * (int) Math.round(energy * ((2.22 * Math.exp(-(localAddedRes + localRes) / 30.9)) + (2.8 * Math.exp(-(localAddedRes + localRes) / 6.2)) + 0.4))) -
                            (Math.round(gstCostBasedOnGem * ((Math.round(hpLoss * 100.0) / 100.0) / hpPercentRestored) * 10.0) / 10.0);

                    if (gstProfit > 0) {
                        optimalAddedEff = localAddedEff;
                        optimalAddedComf = localAddedComf;
                        optimalAddedRes = localAddedRes;
                        break;
                    }
                }

                if (gstProfit > 0) {
                    optimalAddedEff = localAddedEff;
                    optimalAddedComf = localAddedComf;
                    optimalAddedRes = localAddedRes;
                    break;
                }

                localAddedComf += localAddedRes;
                localAddedRes = 0;
            }
        }

        addedEff = optimalAddedEff;
        addedRes = optimalAddedRes;
        addedLuck = localPoints - pointsSpent;
        addedComf = optimalAddedComf;
        updatePoints();
    }

    // sets up HP calcs based on gem level and shoe rarity
    private void hpCalcs(float totalComf) {
        switch (comfGemLvlForRepair) {
            case 2:
                gstCostBasedOnGem = 30;
                break;
            case 3:
                gstCostBasedOnGem = 100;
                break;
            default:
                gstCostBasedOnGem = 10;
        }

        switch (shoeRarity) {
            case COMMON:
                hpLoss = energy * 0.328 * Math.pow(totalComf, -0.322);
                switch (comfGemLvlForRepair) {
                    case 2:
                        hpPercentRestored = 39;
                        break;
                    case 3:
                        hpPercentRestored = 100;
                        break;
                    default:
                        hpPercentRestored = 3;
                }
                break;
            case UNCOMMON:
                hpLoss = energy * 0.435 * Math.pow(totalComf, -0.45);
                switch (comfGemLvlForRepair) {
                    case 2:
                        hpPercentRestored = 23;
                        break;
                    case 3:
                        hpPercentRestored = 100;
                        break;
                    default:
                        hpPercentRestored = 1.8f;
                }
                break;
            case RARE:
                hpLoss = energy * 0.51 * Math.pow(totalComf, -0.483);
                switch (comfGemLvlForRepair) {
                    case 2:
                        hpPercentRestored = 16;
                        break;
                    case 3:
                        hpPercentRestored = 92;
                        break;
                    default:
                        hpPercentRestored = 1.2f;
                }
                break;
            default:
                hpLoss = 0;
                hpPercentRestored = 1;
        }

        if (totalComf == 0 || energy == 0) {
            hpLoss = 0;
            hpPercentRestored = 1;
        }
    }

    // returns base repair cost in gst based on shoe rarity and level
    // consider making this a formula in the future...
    private float getRepairCost() {
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

        return baseCost;
    }

    // calculates mb chances
    private void calcMbChances() {
        float totalLuck = Float.parseFloat(luckTotalTextView.getText().toString());

        if (energy <= -0.04 * totalLuck + 6 && energy >= -0.05263 * totalLuck + 2 && energy >= 1 && totalLuck > 1) {
            // lvl 1 high chance range
            mysteryBox1.clearColorFilter();
            mysteryBox1.setImageTintMode(null);
            mysteryBox1.setAlpha(1.0f);
        } else if (energy > -0.04 * totalLuck + 6 && energy < -0.02 * totalLuck + 8 && totalLuck < 110 && totalLuck > 1) {
            // lvl 1 low chance range
            mysteryBox1.clearColorFilter();
            mysteryBox1.setImageTintMode(null);
            mysteryBox1.setAlpha(0.5f);
        } else {
            // outside lvl 1 range
            mysteryBox1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox1.setAlpha(0.5f);
        }

        if (energy <= -0.06897 * totalLuck + 10 && energy >= -1.3333 * totalLuck + 6 && energy >= 2 && totalLuck > 2) {
            // lvl 2 high chance range
            mysteryBox2.clearColorFilter();
            mysteryBox2.setImageTintMode(null);
            mysteryBox2.setAlpha(1.0f);
        } else if (energy > -0.068966 * totalLuck + 10 && energy < -0.04 * totalLuck + 13 && totalLuck > 2) {
            // lvl 2 low chance range
            mysteryBox2.clearColorFilter();
            mysteryBox2.setImageTintMode(null);
            mysteryBox2.setAlpha(0.5f);
        } else {
            // outside lvl 2 range
            mysteryBox2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox2.setAlpha(0.5f);
        }

        if (energy <= -0.09091 * totalLuck + 16 && energy >= 70 * Math.pow((totalLuck + 8), -1) + 2 && energy >= 3.1 && totalLuck > 3) {
            // lvl 3 high chance range
            mysteryBox3.clearColorFilter();
            mysteryBox3.setImageTintMode(null);
            mysteryBox3.setAlpha(1.0f);
        } else if ((energy > -0.09091 * totalLuck + 16 && energy < -0.08333 * totalLuck + 22) || (energy > 3.5 && energy < 12 && totalLuck > 100 && totalLuck < 500)) {
            // lvl 3 low chance range
            mysteryBox3.clearColorFilter();
            mysteryBox3.setImageTintMode(null);
            mysteryBox3.setAlpha(0.5f);
        } else {
            // outside lvl 3 range
            mysteryBox3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox3.setAlpha(0.5f);
        }

        if (energy <= -0.00001 * Math.pow((totalLuck + 150), 2) + 22 && energy >= 70 * Math.pow((totalLuck + 1), -1) + 3 && totalLuck > 4) {
            if (energy <= -0.0001 * Math.pow((totalLuck + 40), 2) + 17 && energy >= 50 * Math.pow((totalLuck + 30), -0.2) - 13.5) {
                // lvl 4 high chance range
                mysteryBox4.clearColorFilter();
                mysteryBox4.setImageTintMode(null);
                mysteryBox4.setAlpha(1.0f);
            } else {
                // lvl 4 low chance range
                mysteryBox4.clearColorFilter();
                mysteryBox4.setImageTintMode(null);
                mysteryBox4.setAlpha(0.5f);
            }
        } else {
            // outside lvl 4 range
            mysteryBox4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox4.setAlpha(0.5f);
        }

        if (energy <= -0.00001 * Math.pow((totalLuck + 150), 2) + 25.5 && energy >= 50 * Math.pow((totalLuck - 2), -1) + 7 && totalLuck > 5) {
            if (energy <= -0.00005 * Math.pow(totalLuck, 2) + 26.5 && energy >= 70 * Math.pow((totalLuck - 10), -0.1) - 32) {
                // lvl 5 high chance range
                mysteryBox5.clearColorFilter();
                mysteryBox5.setImageTintMode(null);
                mysteryBox5.setAlpha(1.0f);
            } else {
                // lvl 5 low chance range
                mysteryBox5.clearColorFilter();
                mysteryBox5.setImageTintMode(null);
                mysteryBox5.setAlpha(0.5f);
            }
        } else {
            // outside lvl 5 range
            mysteryBox5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox5.setAlpha(0.5f);
        }

        if (energy >= 70 * Math.pow((totalLuck + 20), -0.5) + 7 && totalLuck > 6) {
            if (energy <= -0.00002 * Math.pow((totalLuck - 150), 2) + 25.5 && energy >= 70 * Math.pow((totalLuck - 50), -0.1) - 28) {
                // lvl 6 high chance range
                mysteryBox6.clearColorFilter();
                mysteryBox6.setImageTintMode(null);
                mysteryBox6.setAlpha(1.0f);
            } else {
                // lvl 6 low chance range
                mysteryBox6.clearColorFilter();
                mysteryBox6.setImageTintMode(null);
                mysteryBox6.setAlpha(0.5f);
            }
        } else if (energy >= 70 * Math.pow((totalLuck - 50), -0.1) - 28 && totalLuck > 6) {
            // more lvl 6 low chance
            mysteryBox6.clearColorFilter();
            mysteryBox6.setImageTintMode(null);
            mysteryBox6.setAlpha(0.5f);
        } else {
            // outside lvl 6 range
            mysteryBox6.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox6.setAlpha(0.5f);
        }

        if (energy >= -2 * Math.log(totalLuck - 100) + 30.2 && totalLuck > 140) {
            // lvl 7 range
            mysteryBox7.clearColorFilter();
            mysteryBox7.setImageTintMode(null);
            mysteryBox7.setAlpha(1.0f);
        } else {
            // outside lvl 7 range
            mysteryBox7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox7.setAlpha(0.5f);
        }

        if (energy >= -totalLuck / 150 + 32) {
            // lvl 8 range
            mysteryBox8.clearColorFilter();
            mysteryBox8.setImageTintMode(null);
            mysteryBox8.setAlpha(1.0f);
        } else {
            // outside lvl 8 range
            mysteryBox8.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox8.setAlpha(0.5f);
        }

        if (energy >= 24 && totalLuck > 1600) {
            // lvl 9/10 range
            mysteryBox9.clearColorFilter();
            mysteryBox9.setImageTintMode(null);
            mysteryBox9.setAlpha(1.0f);
            lvl10Shrug.setVisibility(View.VISIBLE);
        } else {
            // outside lvl 9/10 range
            mysteryBox9.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gandalf));
            mysteryBox9.setAlpha(0.5f);
            lvl10Shrug.setVisibility(View.INVISIBLE);
        }
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

        int gemsUnlocked = 0;
        gemEff = 0;
        gemLuck = 0;
        gemComf = 0;
        gemRes = 0;

        if (shoeLevel >= 20) {
            gemsUnlocked = 4;
        } else if (shoeLevel >= 15) {
            gemsUnlocked = 3;
        } else if (shoeLevel >= 10) {
            gemsUnlocked = 2;
        } else if (shoeLevel >= 5) {
            gemsUnlocked = 1;
        }

        for (int i = 0; i < gemsUnlocked; i++) {
            switch (gems.get(i).getSocketType()) {
                case EFF:
                    gems.get(i).setBasePoints(baseEff);
                    gemEff += gems.get(i).getTotalPoints();
                    break;
                case LUCK:
                    gems.get(i).setBasePoints(baseLuck);
                    gemLuck += gems.get(i).getTotalPoints();
                    break;
                case COMF:
                    gems.get(i).setBasePoints(baseComf);
                    gemComf += gems.get(i).getTotalPoints();
                    break;
                case RES:
                    gems.get(i).setBasePoints(baseRes);
                    gemRes += gems.get(i).getTotalPoints();
                    break;
                default:
            }
        }

        effTotalTextView.setText(String.valueOf(Math.floor((baseEff + addedEff + gemEff) * 10) / 10.0));
        luckTotalTextView.setText(String.valueOf(Math.floor((baseLuck + addedLuck + gemLuck) * 10) / 10.0));
        comfortTotalTextView.setText(String.valueOf(Math.floor((baseComf + addedComf + gemComf) * 10) / 10.0));
        resTotalTextView.setText(String.valueOf(Math.floor((baseRes + addedRes + gemRes) * 10) / 10.0));

        pointsAvailableTextView.setText(String.valueOf(pointsAvailable));

        if (pointsAvailable > 0) {
            effPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
            addEffButton.setClickable(true);
            luckPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
            addLuckButton.setClickable(true);
            comfPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
            addComfButton.setClickable(true);
            resPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
            addResButton.setClickable(true);
        } else {
            effPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
            addEffButton.setClickable(false);
            luckPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
            addLuckButton.setClickable(false);
            comfPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
            addComfButton.setClickable(false);
            resPlusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
            addResButton.setClickable(false);
        }

        if (addedEff > 0) {
            effMinusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
            subEffButton.setClickable(true);
        } else {
            effMinusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
            subEffButton.setClickable(false);
        }

        if (addedLuck > 0) {
            luckMinusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
            subLuckButton.setClickable(true);
        } else {
            luckMinusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gem_socket_shadow));
            subLuckButton.setClickable(false);
        }

        if (addedComf > 0) {
            comfMinusTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.almost_black));
            subComfButton.setClickable(true);
        } else {
            comfMinusTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gem_socket_shadow));
            subComfButton.setClickable(false);
        }

        if (addedRes > 0) {
            resMinusTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.almost_black));
            subResButton.setClickable(true);
        } else {
            resMinusTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gem_socket_shadow));
            subResButton.setClickable(false);
        }

        pointsAvailableTextView.setText(String.valueOf(pointsAvailable));
        calcTotals();
    }

    // load initial point values
    private void loadPoints() {
        if (baseEff != 0) {
            effEditText.setText(String.valueOf(baseEff));
        }
        if (baseLuck != 0) {
            luckEditText.setText(String.valueOf(baseLuck));
        }
        if (baseComf != 0) {
            comfortEditText.setText(String.valueOf(baseComf));
        }
        if (baseRes != 0) {
            resEditText.setText(String.valueOf(baseRes));
        }
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
    public void onStop() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(OPT_SHOE_TYPE_PREF, shoeType);
        editor.putFloat(OPT_ENERGY_PERF, energy);
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
        editor.putInt(COMF_GEM_HP_REPAIR, comfGemLvlForRepair);
        editor.putBoolean(UPDATE_PREF, false);

        editor.putInt(GEM_ONE_TYPE_PREF, gems.get(0).getSocketType());
        editor.putInt(GEM_ONE_RARITY_PREF, gems.get(0).getSocketRarity());
        editor.putInt(GEM_ONE_MOUNTED_PREF, gems.get(0).getMountedGem());
        editor.putInt(GEM_TWO_TYPE_PREF, gems.get(1).getSocketType());
        editor.putInt(GEM_TWO_RARITY_PREF, gems.get(1).getSocketRarity());
        editor.putInt(GEM_TWO_MOUNTED_PREF, gems.get(1).getMountedGem());
        editor.putInt(GEM_THREE_TYPE_PREF, gems.get(2).getSocketType());
        editor.putInt(GEM_THREE_RARITY_PREF, gems.get(2).getSocketRarity());
        editor.putInt(GEM_THREE_MOUNTED_PREF, gems.get(2).getMountedGem());
        editor.putInt(GEM_FOUR_TYPE_PREF, gems.get(3).getSocketType());
        editor.putInt(GEM_FOUR_RARITY_PREF, gems.get(3).getSocketRarity());
        editor.putInt(GEM_FOUR_MOUNTED_PREF, gems.get(3).getMountedGem());

        editor.apply();

        super.onStop();
    }
}