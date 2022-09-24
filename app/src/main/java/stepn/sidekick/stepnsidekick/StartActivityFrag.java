package stepn.sidekick.stepnsidekick;

import static android.content.Context.MODE_PRIVATE;
import static stepn.sidekick.stepnsidekick.Finals.*;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Activity fragment - Allows user to select a type of shoe from a list of default or enter their own
 * stats. User enters energy they want to spend and whether or not they want to enable voice
 * updates and the ten-second countdown timer. Starts the SpeedTracker activity.
 *
 * @author Rob Godfrey
 * @version 1.4.0 Added vibration option for alerts
 *
 */

// TODO: add update dialog, add instructions for vibrate stuff

public class StartActivityFrag extends Fragment {
    private final int PERMISSIONS_FINE_LOCATION = 99;

    // only use major version changes, eg 1.1, 1.2, not 1.1.2
    private final float CURRENT_APP_VERSION = 1.3f;

    // keys for shared prefs
    private final String TEN_SECOND_TIMER_PREF = "tenSecondTimer";
    private final String ALERTS_VIBRATION_AUDIBLE_PREF = "alertsVibrationAudible";
    // private final String VOICE_ALERTS_SPEED_PREF = "voiceAlertsSpeed";  OLD, DO NOT USE
    private final String VOICE_ALERTS_SPEED_PREF = "voiceAlertsSpeedType";
    private final String VOICE_ALERTS_TIME_PREF = "voiceAlertsTime";
    private final String VOICE_ALERTS_CD_PREF = "voiceCountdownAlerts";
    private final String CUSTOM_MIN_SPEED_PREF = "customMinSpeed";
    private final String CUSTOM_MAX_SPEED_PREF = "customMaxSpeed";
    private final String FIRST_TIME_PREF = "firstTime";

    Button leftButton, rightButton, backgroundButton;
    ImageButton startButton, countDownTimerButton, voiceAlertSpeedButton, voiceAlertTimeButton,
            voiceAlertCountdownButton, helpButton, alertsVibrateButton;
    ImageView countDownTimerButtonShadow, voiceAlertSpeedButtonShadow, voiceAlertTimeButtonShadow,
            voiceAlertCountdownButtonShadow, startButtonShadow, helpButtonShadow, shoeTypeImage,
            footOne, footTwo, footThree, energyBox, energyBoxShadow, maxSpeedBox, minSpeedBox,
            maxSpeedBoxShadow, minSpeedBoxShadow, leftLilHelper, rightLilHelper, alertsVibrateButtonShadow;
    TextView countDownTimerTextView, voiceAlertSpeedTextView, voiceAlertTimeTextView,
            voiceAlertCountdownTextView, startTextView, helpButtonTextView,
            countDownTimerTextViewShadow, voiceAlertSpeedTextViewShadow,
            voiceAlertTimeTextViewShadow, voiceAlertCountdownTextViewShadow, shoeTypeTextView,
            energyInMins, alertsVibrateTextView, alertsVibrateShadowTextView;
    EditText minSpeedEditText, maxSpeedEditText, energyEditText, focusThief;
    LinearLayout speedsLayout, countdownLayout, alertsLayout, energyLayout, voiceUpdatesLayout,
            minSpeedStack, maxSpeedStack;

    private float savedAppVersion, customMinSpeed, customMaxSpeed;
    private int shoeTypeIterator, alertsVibrationAudible, voiceAlertsSpeedType;
    private double energy;
    private boolean tenSecondTimer, voiceCountdownAlerts, voiceAlertsTime, voiceAlertsAvgSpeed,
            voiceAlertsCurrentSpeed, gpsPermissions, firstTime, alertsAudible, alertsVibration;

    ArrayList<Shoe> shoes;
    LocationManager manager;

    public StartActivityFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getSharedPrefs = requireActivity().getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);

                tenSecondTimer = getSharedPrefs.getBoolean(TEN_SECOND_TIMER_PREF, true);
                alertsVibrationAudible = getSharedPrefs.getInt(ALERTS_VIBRATION_AUDIBLE_PREF, 1);
                voiceAlertsSpeedType = getSharedPrefs.getInt(VOICE_ALERTS_SPEED_PREF, 3);
                voiceAlertsTime = getSharedPrefs.getBoolean(VOICE_ALERTS_TIME_PREF, true);
                voiceCountdownAlerts = getSharedPrefs.getBoolean(VOICE_ALERTS_CD_PREF, true);
                energy = (double) getSharedPrefs.getInt(ENERGY_PREF, 0) / 10;
                shoeTypeIterator = getSharedPrefs.getInt(SHOE_TYPE_ITERATOR_PREF, 0);
                customMinSpeed = getSharedPrefs.getFloat(CUSTOM_MIN_SPEED_PREF, 0);
                customMaxSpeed = getSharedPrefs.getFloat(CUSTOM_MAX_SPEED_PREF, 0);
                firstTime = getSharedPrefs.getBoolean(FIRST_TIME_PREF, true);
                savedAppVersion = getSharedPrefs.getFloat(APP_VERSION_PREF, 1f);

                shoes = new ArrayList<>();

                shoes.add(new Shoe("Walker", R.mipmap.shoe_walker, 1.0f, 6.0f, 1));
                shoes.add(new Shoe("Jogger", R.mipmap.shoe_jogger, 4.0f, 10.0f, 2));
                shoes.add(new Shoe("Runner", R.mipmap.shoe_runner, 8.0f, 20.0f, 3));
                shoes.add(new Shoe("Trainer", R.mipmap.shoe_trainer, 1.0f, 20.0f, 4));
                shoes.add(new Shoe(getString(R.string.custom), R.mipmap.shoe_custom, customMinSpeed, customMaxSpeed, 0));
            }
        }).start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start_activity, container, false);

        speedsLayout = view.findViewById(R.id.speedRow);
        countdownLayout = view.findViewById(R.id.countdownStack);
        alertsLayout  = view.findViewById(R.id.alertsStack);
        energyLayout = view.findViewById(R.id.energyStack);
        voiceUpdatesLayout = view.findViewById(R.id.voiceUpdatesRow);
        minSpeedStack = view.findViewById(R.id.minSpeedStack);
        maxSpeedStack = view.findViewById(R.id.maxSpeedStack);

        startButton = view.findViewById(R.id.startImageButton);
        startButtonShadow = view.findViewById(R.id.startButtonShadow);

        helpButton = view.findViewById(R.id.helpButton);
        helpButtonShadow = view.findViewById(R.id.helpButtonShadow);
        helpButtonTextView = view.findViewById(R.id.helpTextView);

        leftButton = view.findViewById(R.id.leftArrowButton);
        rightButton = view.findViewById(R.id.rightArrowButton);
        backgroundButton = view.findViewById(R.id.backgroundButton);
        shoeTypeImage = view.findViewById(R.id.shoeTypeImageView);
        shoeTypeTextView = view.findViewById(R.id.shoeTypeTextView);

        countDownTimerButton = view.findViewById(R.id.countdownTimerButton);
        alertsVibrateButton = view.findViewById(R.id.alertsTimerButton);
        voiceAlertSpeedButton = view.findViewById(R.id.voiceAlertSpeedButton);
        voiceAlertTimeButton = view.findViewById(R.id.voiceTimeButton);
        voiceAlertCountdownButton = view.findViewById(R.id.voiceOneMinThirtySecButton);

        countDownTimerButtonShadow = view.findViewById(R.id.countdownTimerButtonShadow);
        alertsVibrateButtonShadow = view.findViewById(R.id.alertsButtonShadow);
        voiceAlertSpeedButtonShadow = view.findViewById(R.id.voiceAlertSpeedButtonShadow);
        voiceAlertTimeButtonShadow = view.findViewById(R.id.voiceTimeButtonShadow);
        voiceAlertCountdownButtonShadow = view.findViewById(R.id.voiceOneMinThirtySecButtonShadow);

        countDownTimerTextView = view.findViewById(R.id.countdownTimerTextView);
        alertsVibrateTextView = view.findViewById(R.id.alertsTextView);
        voiceAlertSpeedTextView = view.findViewById(R.id.voiceAlertSpeedTextView);
        voiceAlertTimeTextView = view.findViewById(R.id.voiceAlertTimeTextView);
        voiceAlertCountdownTextView = view.findViewById(R.id.voiceOneMinThirtySecTextView);
        startTextView = view.findViewById(R.id.startTextView);

        countDownTimerTextViewShadow = view.findViewById(R.id.countdownTimerShadowTextView);
        alertsVibrateShadowTextView = view.findViewById(R.id.alertsShadowTextView);
        voiceAlertSpeedTextViewShadow = view.findViewById(R.id.voiceAlertSpeedShadowTextView);
        voiceAlertTimeTextViewShadow = view.findViewById(R.id.voiceAlertTimeShadowTextView);
        voiceAlertCountdownTextViewShadow = view.findViewById(R.id.voiceOneMinThirtySecShadowTextView);

        footOne = view.findViewById(R.id.footprint1ImageView);
        footTwo = view.findViewById(R.id.footprint2ImageView);
        footThree = view.findViewById(R.id.footprint3ImageView);

        energyBox = view.findViewById(R.id.energyBox);
        energyBoxShadow = view.findViewById(R.id.energyBoxShadow);
        maxSpeedBox = view.findViewById(R.id.maxSpeedBox);
        minSpeedBox = view.findViewById(R.id.minSpeedBox);
        maxSpeedBoxShadow = view.findViewById(R.id.maxSpeedBoxShadow);
        minSpeedBoxShadow = view.findViewById(R.id.minSpeedBoxShadow);

        minSpeedEditText = view.findViewById(R.id.minSpeedEditText);
        maxSpeedEditText = view.findViewById(R.id.maxSpeedEditText);
        energyEditText = view.findViewById(R.id.energyToSpendEditText);
        focusThief = view.findViewById(R.id.focusThief);
        energyInMins = view.findViewById(R.id.energyInMinsTextView);

        leftLilHelper = view.findViewById(R.id.helperCircleLeft);
        rightLilHelper = view.findViewById(R.id.helperCircleRight);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (firstTime) {
            welcome();
            firstTime = false;
        } else if (savedAppVersion < CURRENT_APP_VERSION) {
            appUpdateDialog();
            updateUI();
        } else {
            updateUI();
        }

        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus(view);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                howTo(view);
                clearFocus(view);
            }
        });

        helpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, helpButton, helpButtonShadow, helpButtonTextView,
                        R.drawable.roundy_button, R.drawable.roundy_button_shadow);
                return false;
            }
        });

        countDownTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenSecondTimer = !tenSecondTimer;
                buttonClickSwitch(countDownTimerButton, countDownTimerTextView, tenSecondTimer);
                clearFocus(view);
            }
        });

        countDownTimerButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dynamicButtonTouchAnim(motionEvent, countDownTimerButton, countDownTimerButtonShadow,
                        countDownTimerTextView, countDownTimerTextViewShadow, tenSecondTimer);
                return false;
            }
        });

        alertsVibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertsVibrationAudible == 3){
                    alertsVibrationAudible = 0;
                } else {
                    alertsVibrationAudible++;
                }
                updateAlertsButton();
                clearFocus(view);
            }
        });

        alertsVibrateButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alertsVibrateButton.setVisibility(View.INVISIBLE);
                        if (alertsVibrationAudible != 0) {
                            alertsVibrateButtonShadow.setImageResource(R.drawable.main_buttons);
                        } else {
                            alertsVibrateButtonShadow.setImageResource(R.drawable.main_buttons_disabled);
                        }
                        alertsVibrateShadowTextView.setText(alertsVibrateTextView.getText().toString());
                        alertsVibrateTextView.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        alertsVibrateButton.setVisibility(View.VISIBLE);
                        alertsVibrateButtonShadow.setImageResource(R.drawable.main_button_shadow);
                        alertsVibrateTextView.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });

        // voiceAlertsSpeedType: 0 = disabled, 1 = current, 2 = average, 3 = both
        voiceAlertSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voiceAlertsSpeedType == 3){
                    voiceAlertsSpeedType = 0;
                } else {
                    voiceAlertsSpeedType++;
                }
                updateVoiceSpeedButton();
                clearFocus(view);
            }
        });

        voiceAlertSpeedButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        voiceAlertSpeedButton.setVisibility(View.INVISIBLE);
                        if (voiceAlertsSpeedType != 0) {
                            voiceAlertSpeedButtonShadow.setImageResource(R.drawable.main_buttons);
                        } else {
                            voiceAlertSpeedButtonShadow.setImageResource(R.drawable.main_buttons_disabled);
                        }
                        voiceAlertSpeedTextViewShadow.setText(voiceAlertSpeedTextView.getText().toString());
                        voiceAlertSpeedTextView.setVisibility(View.INVISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        voiceAlertSpeedButton.setVisibility(View.VISIBLE);
                        voiceAlertSpeedButtonShadow.setImageResource(R.drawable.main_button_shadow);
                        voiceAlertSpeedTextView.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });

        voiceAlertTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceAlertsTime = !voiceAlertsTime;
                buttonClickSwitch(voiceAlertTimeButton, voiceAlertTimeTextView, voiceAlertsTime);
                clearFocus(view);
            }
        });

        voiceAlertTimeButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dynamicButtonTouchAnim(motionEvent, voiceAlertTimeButton, voiceAlertTimeButtonShadow,
                        voiceAlertTimeTextView, voiceAlertTimeTextViewShadow, voiceAlertsTime);
                return false;
            }
        });

        voiceAlertCountdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceCountdownAlerts = !voiceCountdownAlerts;
                buttonClickSwitch(voiceAlertCountdownButton, voiceAlertCountdownTextView, voiceCountdownAlerts);
                clearFocus(view);
            }
        });

        voiceAlertCountdownButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dynamicButtonTouchAnim(motionEvent, voiceAlertCountdownButton, voiceAlertCountdownButtonShadow,
                        voiceAlertCountdownTextView, voiceAlertCountdownTextViewShadow, voiceCountdownAlerts);
                return false;
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startButtonClick();
                clearFocus(view);
            }
        });

        startButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, startButton, startButtonShadow, startTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
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

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeTypeIterator == 0) {
                    shoeTypeIterator = 4;
                } else {
                    shoeTypeIterator--;
                }
                updatePage();
                clearFocus(view);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shoeTypeIterator == 4) {
                    shoeTypeIterator = 0;
                } else {
                    shoeTypeIterator++;
                }
                updatePage();
                clearFocus(view);
            }
        });

        minSpeedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!minSpeedEditText.getText().toString().isEmpty() && !minSpeedEditText.getText().toString().equals(".")) {
                    shoes.get(shoeTypeIterator).setMinSpeed(Float.parseFloat(minSpeedEditText.getText().toString() + "f"));
                }
            }
        });

        maxSpeedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!maxSpeedEditText.getText().toString().equals("") && !maxSpeedEditText.getText().toString().equals(".")) {
                    shoes.get(shoeTypeIterator).setMaxSpeed(Float.parseFloat(maxSpeedEditText.getText().toString() + "f"));
                }
            }
        });

        energyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateEnergy();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    // dialog message if the device's GPS is turned off
    private void buildAlertMessageNoGps() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_layout_gps);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton noButton = dialog.findViewById(R.id.noButton);
        ImageButton yesButton = dialog.findViewById(R.id.yesButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.plz_turn_on_gps), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    // checks for location permissions
    private void checkForLocationPermissions() {
        // get permissions from the user to track GPS
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsPermissions = true;

        } else {
            // permissions not yet granted
            buildAlertMessagePermissions();
            gpsPermissions = false;
        }
    }

    // dialog message if the app does not have precise location permissions
    private void buildAlertMessagePermissions() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_layout_permissions);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton okayButton = dialog.findViewById(R.id.okayButton);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        });
        dialog.show();
    }

    // if the user has not granted precise permissions after viewing the system's permissions page, a toast alert appears
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.gps_permissions, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Animation for button presses with static text.
     *
     * @param motionEvent the MotionEvent
     * @param button the button being 'pressed'
     * @param buttonShadow the button's shadow
     * @param buttonText the button's textView
     * @param buttonBackground the button's image resource
     * @param buttonShadowBackground the button's background image resource
     */
    private void staticButtonTouchAnim(MotionEvent motionEvent, ImageView button, ImageView buttonShadow, TextView buttonText,
                                       int buttonBackground, int buttonShadowBackground) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                button.setVisibility(View.INVISIBLE);
                buttonText.setVisibility(View.INVISIBLE);
                buttonShadow.setImageResource(buttonBackground);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                button.setVisibility(View.VISIBLE);
                buttonText.setVisibility(View.VISIBLE);
                buttonShadow.setImageResource(buttonShadowBackground);
                break;
        }
    }

    /**
     * Animation for button presses with dynamic text and backgrounds.
     *
     * @param motionEvent the MotionEvent
     * @param button button being pressed
     * @param buttonShadow button's shadow
     * @param buttonText button's text
     * @param buttonShadowText button shadow's text
     * @param enabled whether or not the button is enabled
     */
    private void dynamicButtonTouchAnim(MotionEvent motionEvent, ImageView button, ImageView buttonShadow, TextView buttonText,
                                        TextView buttonShadowText, boolean enabled) {
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                button.setVisibility(View.INVISIBLE);
                if (enabled) {
                    buttonShadow.setImageResource(R.drawable.main_buttons);
                    buttonShadowText.setText(R.string.enabled);
                } else {
                    buttonShadow.setImageResource(R.drawable.main_buttons_disabled);
                    buttonShadowText.setText(R.string.disabled);
                }
                buttonText.setVisibility(View.INVISIBLE);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                button.setVisibility(View.VISIBLE);
                buttonShadow.setImageResource(R.drawable.main_button_shadow);
                buttonText.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Changes button text and background for buttons with option to enable/disable.
     *
     * @param button which button to change
     * @param buttonText button's text to change
     * @param enabled whether or not the button is enabled
     */
    private void buttonClickSwitch(ImageView button, TextView buttonText, boolean enabled) {
        if (enabled) {
            buttonText.setText(R.string.enabled);
            button.setImageResource(R.drawable.main_buttons);
        } else {
            buttonText.setText(R.string.disabled);
            button.setImageResource(R.drawable.main_buttons_disabled);
        }
    }

    // updates alert button: 0 = disabled, 1 = audible, 2 = vibration, 3 = both
    private void updateAlertsButton() {
        switch (alertsVibrationAudible) {
            case 0:
                alertsAudible = false;
                alertsVibration = false;
                alertsVibrateTextView.setText(R.string.disabled);
                alertsVibrateButton.setImageResource(R.drawable.main_buttons_disabled);
                break;
            case 1:
                alertsAudible = true;
                alertsVibration = false;
                alertsVibrateTextView.setText(R.string.audible);
                alertsVibrateButton.setImageResource(R.drawable.main_buttons);
                break;
            case 2:
                alertsAudible = false;
                alertsVibration = true;
                alertsVibrateTextView.setText(R.string.vibration);
                break;
            default:
                alertsAudible = true;
                alertsVibration = true;
                alertsVibrateTextView.setText(R.string.both);
                break;
        }
    }

    // updates speed button for voice alerts
    private void updateVoiceSpeedButton() {
        switch (voiceAlertsSpeedType) {
            case 0:
                voiceAlertsAvgSpeed = false;
                voiceAlertsCurrentSpeed = false;
                voiceAlertSpeedTextView.setText(R.string.disabled);
                voiceAlertSpeedButton.setImageResource(R.drawable.main_buttons_disabled);
                break;
            case 1:
                voiceAlertsAvgSpeed = false;
                voiceAlertsCurrentSpeed = true;
                voiceAlertSpeedTextView.setText(R.string.current);
                voiceAlertSpeedButton.setImageResource(R.drawable.main_buttons);
                break;
            case 2:
                voiceAlertsAvgSpeed = true;
                voiceAlertsCurrentSpeed = false;
                voiceAlertSpeedTextView.setText(R.string.average);
                break;
            default:
                voiceAlertsAvgSpeed = true;
                voiceAlertsCurrentSpeed = true;
                voiceAlertSpeedTextView.setText(R.string.all);
                break;
        }
    }

    // updates page when user changes shoe selection
    private void updatePage() {
        shoeTypeImage.setImageResource(shoes.get(shoeTypeIterator).getImageSource());
        shoeTypeTextView.setText(shoes.get(shoeTypeIterator).getTitle());
        updateFeet(shoes.get(shoeTypeIterator).getNumFeet());

        minSpeedEditText.setText(String.valueOf(shoes.get(shoeTypeIterator).getMinSpeed()));
        maxSpeedEditText.setText(String.valueOf(shoes.get(shoeTypeIterator).getMaxSpeed()));

        if (shoeTypeIterator != 4) {
            minSpeedEditText.setFocusable(false);
            maxSpeedEditText.setFocusable(false);
            minSpeedBox.setImageResource(R.drawable.main_buttons_disabled);
            maxSpeedBox.setImageResource(R.drawable.main_buttons_disabled);
        } else {
            minSpeedEditText.setFocusableInTouchMode(true);
            maxSpeedEditText.setFocusableInTouchMode(true);
            minSpeedBox.setImageResource(R.drawable.input_box_speeds);
            maxSpeedBox.setImageResource(R.drawable.input_box_speeds);

            if (minSpeedEditText.getText().toString().equals("0.0")) {
                minSpeedEditText.setText("");
            }
            if (maxSpeedEditText.getText().toString().equals("0.0")) {
                maxSpeedEditText.setText("");
            }
        }

    }

    // checks input and starts SpeedTracker
    private void startButtonClick() {
        String minString, maxString, energyString;
        double min, max;

        manager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // check for gps enabled
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return;
        } else {
            checkForLocationPermissions();
        }

        minString = minSpeedEditText.getText().toString();
        maxString = maxSpeedEditText.getText().toString();
        energyString = energyEditText.getText().toString();

        min = Double.parseDouble((minString.equals(".") || minString.isEmpty()) ? "0" : minString);
        max = Double.parseDouble((maxString.equals(".") || maxString.isEmpty()) ? "0" : maxString);
        energy = Double.parseDouble((energyString.equals(".") || energyString.isEmpty()) ? "0" : energyString);

        if (!gpsPermissions ||
                min < 1.0 ||
                max < min + 1.0 ||
                energy < 0.2 ||
                energy * 10 % 2 != 0) {
            String toastMessage = "";
            if (!gpsPermissions) {
                toastMessage = getString(R.string.plz_allow_precise_location);
            } else if (min < 1.0) {
                toastMessage = getString(R.string.minimum_speed_too_low);
                shakeyShake(minSpeedStack, minSpeedEditText);
            } else if (max < min + 1.0) {
                toastMessage = getString(R.string.maximum_speed_too_low);
                shakeyShake(maxSpeedStack, maxSpeedEditText);
            } else if (energy < 0.2) {
                toastMessage = getString(R.string.energy_too_low);
                shakeyShake(energyLayout, energyEditText);
            } else if (energy * 10 % 2 != 0) {
                toastMessage = getString(R.string.not_a_multiple_of_2);
                shakeyShake(energyLayout, energyEditText);
            }

            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();

        } else if (energy == 0) {
            shakeyShake(energyLayout, energyEditText);
            Toast.makeText(getActivity(), getString(R.string.energy_too_low), Toast.LENGTH_SHORT).show();
        } else {
            Intent startGPSActivity = new Intent(getContext(), SpeedTracker.class);

            startGPSActivity.putExtra(MIN_SPEED, min);
            startGPSActivity.putExtra(MAX_SPEED, max);
            startGPSActivity.putExtra(ENERGY, energy);
            startGPSActivity.putExtra(SHOE_TYPE, shoes.get(shoeTypeIterator).getTitle());
            startGPSActivity.putExtra(NUM_FEET, shoes.get(shoeTypeIterator).getNumFeet());
            startGPSActivity.putExtra(TEN_SECOND_TIMER, tenSecondTimer);
            startGPSActivity.putExtra(ALERTS_VIBRATION, alertsVibration);
            startGPSActivity.putExtra(ALERTS_AUDIBLE, alertsAudible);
            startGPSActivity.putExtra(VOICE_ALERTS_CD, voiceCountdownAlerts);
            startGPSActivity.putExtra(VOICE_ALERTS_AVG_SPEED, voiceAlertsAvgSpeed);
            startGPSActivity.putExtra(VOICE_ALERTS_CURRENT_SPEED, voiceAlertsCurrentSpeed);
            startGPSActivity.putExtra(VOICE_ALERTS_TIME, voiceAlertsTime);
            startGPSActivity.putExtra(AD_PREF, ((MainActivity) requireActivity()).ads);

            startActivity(startGPSActivity);
        }
    }

    // changes the UI if the user has saved prefs (default: all enabled, energy 0, walker shoe type)
    private void updateUI() {
        if (!tenSecondTimer) {
            countDownTimerButton.setImageResource(R.drawable.main_buttons_disabled);
            countDownTimerTextView.setText(R.string.disabled);
        }
        if (!voiceAlertsTime) {
            voiceAlertTimeButton.setImageResource(R.drawable.main_buttons_disabled);
            voiceAlertTimeTextView.setText(R.string.disabled);
        }
        if (!voiceCountdownAlerts) {
            voiceAlertCountdownButton.setImageResource(R.drawable.main_buttons_disabled);
            voiceAlertCountdownTextView.setText(R.string.disabled);
        }
        if (energy != 0) {
            energyEditText.setText(String.valueOf(energy));
            updateEnergy();
        }
        updatePage();
        updateVoiceSpeedButton();
        updateAlertsButton();
    }

    // updates the minutes shown when the user enters amount of energy
    private void updateEnergy() {
        double mins;
        if (!energyEditText.getText().toString().isEmpty()
                && !energyEditText.getText().toString().equals(".")
                && !(Double.parseDouble(energyEditText.getText().toString()) < 0.2)) {
            mins = 5 * Double.parseDouble(energyEditText.getText().toString());
            String minsString;
            if (mins < 60) {
                minsString = "(" + (int) mins + (mins < 2 ? getString(R.string.min) : getString(R.string.min_plural));
            } else {
                int hours = (int) Math.floor(mins / 60);
                minsString = "(" + hours +  (hours < 2 ? getString(R.string.hour) : getString(R.string.hours_plural));
                mins = mins - hours * 60;
                if (mins > 0) {
                    minsString += " " + (int) mins + (mins < 2 ? getString(R.string.min) : getString(R.string.min_plural));
                } else {
                    minsString += getString(R.string.thirty_sec);
                }
            }
            energyInMins.setText(minsString);
        } else {
            energyInMins.setText(R.string.zero_mins);
        }
    }

    /**
     * Shakes input box, shadow. Focuses on EditText.
     *
     * @param layout the layout to shake
     * @param textToFocus the text to focus on
     */
    private void shakeyShake(LinearLayout layout, EditText textToFocus) {
        textToFocus.requestFocus();
        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                layout,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f));
        scaler.setDuration(80);
        scaler.setRepeatCount(3);
        scaler.setRepeatMode(ValueAnimator.REVERSE);
        scaler.start();
    }

    /**
     * Grows box to highlight field (for the how-to)
     *
     * @param layout the layout to grow
     */
    private void growLayout(LinearLayout layout) {
        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                layout,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f));
        scaler.setDuration(500);
        scaler.start();
    }

    /**
     * Shrinks box of highlighted field (tutorial)
     *
     * @param layout the layout to shrink
     */
    private void shrinkLayout(LinearLayout layout) {
        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                layout,
                PropertyValuesHolder.ofFloat("scaleX", 1),
                PropertyValuesHolder.ofFloat("scaleY", 1));
        scaler.setDuration(500);
        scaler.start();
    }

    // updates number of footprints depending on the type of shoe selected
    private void updateFeet(int numFeet) {
        switch (numFeet) {
            case 1:
                footOne.setImageResource(R.mipmap.footprint);
                footTwo.setVisibility(View.GONE);
                break;
            case 2:
                footTwo.setVisibility(View.VISIBLE);
                footThree.setVisibility(View.GONE);
                break;
            case 3:
                footOne.setImageResource(R.mipmap.footprint);
                footTwo.setVisibility(View.VISIBLE);
                footThree.setVisibility(View.VISIBLE);
                break;
            case 4:
                footOne.setImageResource(R.mipmap.trainer_t);
                footTwo.setVisibility(View.GONE);
                footThree.setVisibility(View.GONE);
                break;
            default:
                footOne.setImageResource(R.mipmap.bolt);
        }
    }

    // initial welcome dialog
    @SuppressLint("ClickableViewAccessibility")
    private void welcome() {
        Dialog welcome = new Dialog(requireActivity());

        welcome.requestWindowFeature(Window.FEATURE_NO_TITLE);
        welcome.setCancelable(false);
        welcome.setContentView(R.layout.welcome);
        welcome.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        welcome.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        welcome.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ImageButton nextButton = welcome.findViewById(R.id.nextButton);
        ImageView nextButtonShadow = welcome.findViewById(R.id.nextButtonShadow);
        TextView nextButtonTextView = welcome.findViewById(R.id.nextButtonTextView);
        Button skipButton = welcome.findViewById(R.id.skipButton);

        welcome.show();

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcome.dismiss();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcome.dismiss();
                howTo(view);
            }
        });

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, nextButton, nextButtonShadow, nextButtonTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
                return false;
            }
        });
    }

    // how-to dialog #1 of 5
    @SuppressLint("ClickableViewAccessibility")
    private void howTo(View view) {
        Dialog instructionsOne = new Dialog(getActivity());

        instructionsOne.requestWindowFeature(Window.FEATURE_NO_TITLE);
        instructionsOne.setCancelable(false);
        instructionsOne.setContentView(R.layout.instructions_1);
        instructionsOne.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionsOne.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        instructionsOne.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ImageButton nextButton = instructionsOne.findViewById(R.id.nextButton);
        ImageView nextButtonShadow = instructionsOne.findViewById(R.id.nextButtonShadow);
        TextView nextButtonTextView = instructionsOne.findViewById(R.id.nextButtonTextView);

        Button skipButton = instructionsOne.findViewById(R.id.skipButton);

        instructionsOne.show();

        leftLilHelper.animate().alpha(0.5f).setDuration(500);
        rightLilHelper.animate().alpha(0.5f).setDuration(500);

        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                leftLilHelper,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaler.setDuration(650);
        scaler.setRepeatMode(ValueAnimator.REVERSE);
        scaler.setRepeatCount(20);
        scaler.start();

        ObjectAnimator scaler2 = ObjectAnimator.ofPropertyValuesHolder(
                rightLilHelper,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaler2.setDuration(650);
        scaler2.setRepeatMode(ValueAnimator.REVERSE);
        scaler2.setRepeatCount(20);
        scaler2.start();

        instructionsOne.show();

        growLayout(speedsLayout);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsOne.dismiss();
                leftLilHelper.animate().alpha(0.0f).setDuration(500);
                rightLilHelper.animate().alpha(0.0f).setDuration(500);
                shrinkLayout(speedsLayout);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsOne.dismiss();
                showInstructionsTwo();
                leftLilHelper.animate().alpha(0.0f).setDuration(500);
                rightLilHelper.animate().alpha(0.0f).setDuration(500);
                shrinkLayout(speedsLayout);
            }
        });

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, nextButton, nextButtonShadow, nextButtonTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
                return false;
            }
        });

    }

    // how-to dialog #2 of 5
    @SuppressLint("ClickableViewAccessibility")
    private void showInstructionsTwo() {
        Dialog instructionsTwo = new Dialog(requireActivity());

        instructionsTwo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        instructionsTwo.setCancelable(false);
        instructionsTwo.setContentView(R.layout.instructions_2);
        instructionsTwo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionsTwo.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        instructionsTwo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ImageButton nextButton = instructionsTwo.findViewById(R.id.nextButton);
        ImageView nextButtonShadow = instructionsTwo.findViewById(R.id.nextButtonShadow);
        TextView nextButtonTextView = instructionsTwo.findViewById(R.id.nextButtonTextView);

        Button skipButton = instructionsTwo.findViewById(R.id.skipButton);

        instructionsTwo.show();

        growLayout(energyLayout);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsTwo.dismiss();
                shrinkLayout(energyLayout);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsTwo.dismiss();
                shrinkLayout(energyLayout);
                showInstructionsThree();
            }
        });

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, nextButton, nextButtonShadow, nextButtonTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
                return false;
            }
        });
    }
    // how-to dialog #3 of 5
    @SuppressLint("ClickableViewAccessibility")
    private void showInstructionsThree() {
        Dialog instructionsThree = new Dialog(requireActivity());

        instructionsThree.requestWindowFeature(Window.FEATURE_NO_TITLE);
        instructionsThree.setCancelable(false);
        instructionsThree.setContentView(R.layout.instructions_3);
        instructionsThree.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionsThree.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        instructionsThree.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ImageButton nextButton = instructionsThree.findViewById(R.id.nextButton);
        ImageView nextButtonShadow = instructionsThree.findViewById(R.id.nextButtonShadow);
        TextView nextButtonTextView = instructionsThree.findViewById(R.id.nextButtonTextView);

        Button skipButton = instructionsThree.findViewById(R.id.skipButton);

        instructionsThree.show();

        growLayout(countdownLayout);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsThree.dismiss();
                shrinkLayout(countdownLayout);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsThree.dismiss();
                shrinkLayout(countdownLayout);
                showInstructionsFour();
            }
        });

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, nextButton, nextButtonShadow, nextButtonTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
                return false;
            }
        });

    }
    // how-to dialog #4 of 5
    @SuppressLint("ClickableViewAccessibility")
    private void showInstructionsFour() {
        Dialog instructionsFour = new Dialog(requireActivity());

        instructionsFour.requestWindowFeature(Window.FEATURE_NO_TITLE);
        instructionsFour.setCancelable(false);
        instructionsFour.setContentView(R.layout.instructions_4);
        instructionsFour.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionsFour.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        instructionsFour.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ImageButton nextButton = instructionsFour.findViewById(R.id.nextButton);
        ImageView nextButtonShadow = instructionsFour.findViewById(R.id.nextButtonShadow);
        TextView nextButtonTextView = instructionsFour.findViewById(R.id.nextButtonTextView);

        Button skipButton = instructionsFour.findViewById(R.id.skipButton);

        instructionsFour.show();

        growLayout(voiceUpdatesLayout);


        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsFour.dismiss();
                shrinkLayout(voiceUpdatesLayout);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsFour.dismiss();
                shrinkLayout(voiceUpdatesLayout);
                showInstructionsFive();
            }
        });

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, nextButton, nextButtonShadow, nextButtonTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
                return false;
            }
        });

    }

    // how-to dialog #5 of 5
    @SuppressLint("ClickableViewAccessibility")
    private void showInstructionsFive() {
        Dialog instructionsFive = new Dialog(requireActivity());

        instructionsFive.requestWindowFeature(Window.FEATURE_NO_TITLE);
        instructionsFive.setCancelable(true);
        instructionsFive.setContentView(R.layout.instructions_5);
        instructionsFive.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        instructionsFive.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        instructionsFive.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        ImageButton nextButton = instructionsFive.findViewById(R.id.goButton);
        ImageView nextButtonShadow = instructionsFive.findViewById(R.id.goButtonShadow);
        TextView nextButtonTextView = instructionsFive.findViewById(R.id.goButtonTextView);

        instructionsFive.show();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsFive.dismiss();
            }
        });

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, nextButton, nextButtonShadow, nextButtonTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void appUpdateDialog() {
        // TODO remove in further major updates...
        ((MainActivity) requireActivity()).ads = false;

        Dialog updateDialog = new Dialog(getActivity());

        updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateDialog.setCancelable(true);
        updateDialog.setContentView(R.layout.update_dialog);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        ImageButton nextButton = updateDialog.findViewById(R.id.nextButton);
        ImageView nextButtonShadow = updateDialog.findViewById(R.id.nextButtonShadow);
        TextView nextButtonTextView = updateDialog.findViewById(R.id.nextButtonTextView);

        updateDialog.show();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismiss();
            }
        });

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                staticButtonTouchAnim(motionEvent, nextButton, nextButtonShadow, nextButtonTextView,
                        R.drawable.start_button, R.drawable.start_button_shadow);
                return false;
            }
        });
    }

    // clears focus from the input boxes by focusing on another hidden edittext
    private void clearFocus(View view) {
        energyEditText.clearFocus();
        minSpeedEditText.clearFocus();
        maxSpeedEditText.clearFocus();

        focusThief.requestFocus();

        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStop() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(TEN_SECOND_TIMER_PREF, tenSecondTimer);
        editor.putInt(ALERTS_VIBRATION_AUDIBLE_PREF, alertsVibrationAudible);
        editor.putInt(VOICE_ALERTS_SPEED_PREF, voiceAlertsSpeedType);
        editor.putBoolean(VOICE_ALERTS_TIME_PREF, voiceAlertsTime);
        editor.putBoolean(VOICE_ALERTS_CD_PREF, voiceCountdownAlerts);
        editor.putInt(ENERGY_PREF, (int) (energy * 10));
        editor.putInt(SHOE_TYPE_ITERATOR_PREF, shoeTypeIterator);
        editor.putFloat(CUSTOM_MIN_SPEED_PREF, shoes.get(4).getMinSpeed());
        editor.putFloat(CUSTOM_MAX_SPEED_PREF, shoes.get(4).getMaxSpeed());
        editor.putBoolean(FIRST_TIME_PREF, firstTime);
        editor.putFloat(APP_VERSION_PREF, CURRENT_APP_VERSION);
        editor.apply();

        super.onStop();super.onStop();
    }

}