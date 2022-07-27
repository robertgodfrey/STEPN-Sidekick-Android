package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.concurrent.TimeUnit;

/**
 * Activity displaying current speed, time remaining, GPS signal strength. Receives all info from
 * the GpsAndClockService class.
 *
 * @author Bob Godfrey
 * @version 1.3.8 Fixed ads, updated layouts to look better on small and big phones, fixed comf gem bug, updated hp loss formulas
 */

public class SpeedTracker extends AppCompatActivity {

    public static int serviceStatus;
    private int localStatus;

    private double speedLowerLimit, speedUpperLimit, energy;
    private long millis;
    private String shoeType;
    private int numFeet;
    private boolean tenSecondTimer, tenSecondTimerDone, changedUI, voiceAlertsTime, voiceAlertsAvgSpeed,
            voiceAlertsCurrentSpeed, voiceAlertsMinuteThirty;

    CountDownTimer initialCountDownTimer;

    ImageView stopWatchImageView;
    TextView initialCountDownTextView, energyAmountTextView, currentSpeedTextView,
            timeRemainingTextView, shoeTypeTextView, shoeSpeedTextView, currentSpeedLabelTextView,
            kmphLabelTextView, timeRemainingLabelTextView, minusTextView, plusTextView,
            avgSpeedLabelTextView, avgSpeedTextView, kmphAvgLabelTextView;
    ImageButton pauseImageButton, minusFiveImageButton, plusFiveImageButton, startImageButton,
            stopImageButton;
    ImageView leftGps, centerGps, rightGps, footLeft, footCenter, footRight;

    AdView bannerAd;
    private boolean ads;

    // receives broadcast from service to update UI
    private final BroadcastReceiver secondsAndSpeedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_tracker);

        serviceStatus = START_RUNNING;
        localStatus = START_RUNNING;

        changedUI = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            speedLowerLimit = extras.getDouble(MIN_SPEED);
            speedUpperLimit = extras.getDouble(MAX_SPEED);
            energy = extras.getDouble(ENERGY);
            shoeType = extras.getString(SHOE_TYPE);
            numFeet = extras.getInt(NUM_FEET);
            tenSecondTimer = extras.getBoolean(TEN_SECOND_TIMER);
            voiceAlertsMinuteThirty = extras.getBoolean(VOICE_ALERTS_CD);
            voiceAlertsCurrentSpeed = extras.getBoolean(VOICE_ALERTS_CURRENT_SPEED);
            voiceAlertsAvgSpeed = extras.getBoolean(VOICE_ALERTS_AVG_SPEED);
            voiceAlertsTime = extras.getBoolean(VOICE_ALERTS_TIME);
            ads = extras.getBoolean(AD_PREF);
        }

        tenSecondTimerDone = !tenSecondTimer;
        buildUI();
        startService();
    }

    // updates UI after ten-second countdown timer is complete
    private void changeUI() {
        changedUI = true;

        initialCountDownTextView.setVisibility(View.GONE);
        stopWatchImageView.setVisibility(View.GONE);
        currentSpeedLabelTextView.setVisibility(View.VISIBLE);
        currentSpeedTextView.setVisibility(View.VISIBLE);
        kmphLabelTextView.setVisibility(View.VISIBLE);
        avgSpeedLabelTextView.setVisibility(View.VISIBLE);
        avgSpeedTextView.setVisibility(View.VISIBLE);
        kmphAvgLabelTextView.setVisibility(View.VISIBLE);
        timeRemainingLabelTextView.setVisibility(View.VISIBLE);
        timeRemainingTextView.setVisibility(View.VISIBLE);

        minusTextView.setVisibility(View.VISIBLE);
        minusFiveImageButton.setVisibility(View.VISIBLE);
        plusTextView.setVisibility(View.VISIBLE);
        plusFiveImageButton.setVisibility(View.VISIBLE);

        pauseImageButton.setImageResource(R.mipmap.pause_button);

        pauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUiPausedState();
                sendNewTime(PAUSE_PAUSED);
            }
        });
    }

    // ensures that the service is stopped on exit
    @Override
    protected void onDestroy() {
        if (initialCountDownTimer != null) {
            initialCountDownTimer.cancel();
        }
        stopService();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        stopService();
        super.onBackPressed();
    }

    // when the user has the app open, the UI will get updated every time the service broadcasts
    private void updateUI(Intent intent) {
        double currentSpeed = 0;
        float gpsAccuracy = 0;
        float avgSpeed = 0;

        if (serviceStatus != localStatus) {
            if (serviceStatus == START_RUNNING) {
                changeUiRunningState();
                localStatus = START_RUNNING;
            } else if (serviceStatus == PAUSE_PAUSED) {
                changeUiPausedState();
                localStatus = PAUSE_PAUSED;
            } else {
                onBackPressed();
            }
        }

        if (intent.getExtras() != null) {
            millis = intent.getLongExtra(COUNTDOWN_TIME, 0);
            currentSpeed = intent.getDoubleExtra(CURRENT_SPEED, 0);
            avgSpeed = intent.getFloatExtra(AVERAGE_SPEED, 0);
            gpsAccuracy = intent.getFloatExtra(GPS_ACCURACY, 0);
            energy = intent.getDoubleExtra(ENERGY, 0);
            tenSecondTimerDone = intent.getBooleanExtra(TEN_SECOND_DONE, true);
        }

        // initial ten second countdown timer
        if (tenSecondTimer) {
            if (!tenSecondTimerDone) {
                initialCountDownTextView.setScaleX(1f);
                initialCountDownTextView.setScaleY(1f);
                ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                        initialCountDownTextView,
                        PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.8f));
                scaler.setDuration(650);

                initialCountDownTextView.setText(String.format("%02d", (TimeUnit.MILLISECONDS.toSeconds(millis + 1000))));
                scaler.start();
                if (tenSecondTimerDone) {
                    changeUI();
                }
            }
        }
        if (tenSecondTimerDone && !changedUI) {
            changeUI();
            if (localStatus == PAUSE_PAUSED) {
                changeUiPausedState();
            }
        }

        if (gpsAccuracy == 0) {
            leftGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.gandalf));
            centerGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.gandalf));
            rightGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.gandalf));
        } else if (gpsAccuracy < 15) {
            leftGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.green));
            centerGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.green));
            rightGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.green));
        } else if (gpsAccuracy >= 15 && gpsAccuracy < 25) {
            leftGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.yellow));
            centerGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.yellow));
            rightGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.gandalf));
        } else {
            leftGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.red));
            centerGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.gandalf));
            rightGps.setColorFilter(ContextCompat.getColor(SpeedTracker.this, R.color.gandalf));
        }

        currentSpeedTextView.setText(String.format("%.1f", currentSpeed));
        avgSpeedTextView.setText(String.format("%.1f", avgSpeed));

        energyAmountTextView.setText(String.format("%.1f", energy));

        String time = "";

        if (TimeUnit.MILLISECONDS.toHours(millis) > 0) {
            time += TimeUnit.MILLISECONDS.toHours(millis) + ":";
        }
        time += String.format("%02d:%02d", (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
        if (intent.getExtras() != null && tenSecondTimerDone) {
            if (intent.getLongExtra(COUNTDOWN_TIME, 0) == -1) {
                time = "00:00";
                onBackPressed();
            }
        }

        timeRemainingTextView.setText(time);
    }

    // sends initial data to service
    public void startService() {
        Intent serviceIntent = new Intent(this, GpsAndClockService.class);

        serviceIntent.putExtra(MIN_SPEED, speedLowerLimit);
        serviceIntent.putExtra(MAX_SPEED, speedUpperLimit);
        serviceIntent.putExtra(ENERGY, energy);
        serviceIntent.putExtra(TEN_SECOND_TIMER, tenSecondTimer);
        serviceIntent.putExtra(VOICE_ALERTS_CD, voiceAlertsMinuteThirty);
        serviceIntent.putExtra(VOICE_ALERTS_CURRENT_SPEED, voiceAlertsCurrentSpeed);
        serviceIntent.putExtra(VOICE_ALERTS_AVG_SPEED, voiceAlertsAvgSpeed);
        serviceIntent.putExtra(VOICE_ALERTS_TIME, voiceAlertsTime);

        startService(serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, GpsAndClockService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(secondsAndSpeedReceiver, new IntentFilter(COUNTDOWN_BR));
        if (serviceStatus == PAUSE_PAUSED) {
            pingServiceForTime();
            changeUiPausedState();
            localStatus = PAUSE_PAUSED;
        } else if (serviceStatus == STOP_STOPPED) {
            onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(secondsAndSpeedReceiver);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(secondsAndSpeedReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildUI() {
        energyAmountTextView = findViewById(R.id.energyAmountTextView);

        bannerAd = findViewById(R.id.bannerAd);

        shoeTypeTextView = findViewById(R.id.shoeTypeOnGpsTextView);
        shoeSpeedTextView = findViewById(R.id.shoeSpeedTextView);
        footLeft = findViewById(R.id.footprintLeftGpsImageView);
        footCenter = findViewById(R.id.footprintCenterGpsImageView);
        footRight = findViewById(R.id.footprintRightGpsImageView);

        leftGps = findViewById(R.id.gpsLeftBarImageView);
        centerGps = findViewById(R.id.gpsCenterBarImageView);
        rightGps = findViewById(R.id.gpsRightBarImageView);

        stopWatchImageView = findViewById(R.id.initialCountdownBackgroundImageView);
        initialCountDownTextView = findViewById(R.id.initialCountdownTextView);

        currentSpeedLabelTextView = findViewById(R.id.currentSpeedLabelTextView);
        currentSpeedTextView = findViewById(R.id.currentSpeedTextView);
        kmphLabelTextView = findViewById(R.id.kmphLabelTextView);

        avgSpeedLabelTextView = findViewById(R.id.avgSpeedLabelTextView);
        avgSpeedTextView = findViewById(R.id.avgSpeedTextView);
        kmphAvgLabelTextView = findViewById(R.id.avgKmphLabelTextView);

        timeRemainingLabelTextView = findViewById(R.id.timeRemainingLabelTextView);
        timeRemainingTextView = findViewById(R.id.timeRemainingTextView);

        pauseImageButton = findViewById(R.id.pauseImageButton);
        stopImageButton = findViewById(R.id.stopImageButton);
        startImageButton = findViewById(R.id.playImageButton);

        minusFiveImageButton = findViewById(R.id.minusFiveSecondsButton);
        plusFiveImageButton = findViewById(R.id.plusFiveSecondsButton);
        minusTextView = findViewById(R.id.minusTextView);
        plusTextView = findViewById(R.id.plusTextView);

        if (ads) {
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);
        } else {
            bannerAd.setVisibility(View.GONE);
        }


        String shoeSpeed = speedLowerLimit + " - " + speedUpperLimit + " km/h";
        shoeSpeedTextView.setText(shoeSpeed);
        shoeTypeTextView.setText(shoeType);

        energyAmountTextView.setText(String.valueOf(energy));

        switch (numFeet) {
            case 1:
                footRight.setImageResource(R.drawable.footprint);
                footCenter.setVisibility(View.GONE);
                break;
            case 2:
                footCenter.setVisibility(View.VISIBLE);
                footLeft.setVisibility(View.GONE);
                break;
            case 3:
                footRight.setImageResource(R.drawable.footprint);
                footCenter.setVisibility(View.VISIBLE);
                footLeft.setVisibility(View.VISIBLE);
                break;
            case 4:
                footRight.setImageResource(R.mipmap.trainer_t);
                footCenter.setVisibility(View.GONE);
                footLeft.setVisibility(View.GONE);
                break;
            default:
                footRight.setImageResource(R.drawable.bolt);
        }

        pauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        buttonPushAnim(pauseImageButton);

        startImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUiRunningState();
                sendNewTime(START_RUNNING);
            }
        });

        buttonPushAnim(startImageButton);

        stopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "STOPN", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

        buttonPushAnim(stopImageButton);

        minusFiveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewTime(Finals.SUB_FIVE);
            }
        });

        minusFiveImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        minusFiveImageButton.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                        minusFiveImageButton.setAlpha(0.0f);
                        break;
                }
                return false;
            }
        });

        plusFiveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewTime(Finals.ADD_FIVE);
            }
        });

        plusFiveImageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        plusFiveImageButton.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                        plusFiveImageButton.setAlpha(0.0f);
                        break;
                }
                return false;
            }
        });

    }

    /**
     * Broadcast to change the timer in GpsAndClockService.
     *
     * @param option defines whether to pause/play, subtract, or add five seconds to the timer
     *               (uses Finals.class for options)
     */
    private void sendNewTime(int option) {
        Intent sendTime = new Intent(MODIFY_TIME_BR);
        sendTime.putExtra(TIME_MODIFIER, option);
        sendBroadcast(sendTime);
    }

    // get an updated time from the service if the timer is paused
    // (service only broadcasts time while timer is running)
    private void pingServiceForTime() {
        Intent getTime = new Intent(GET_TIME_BR);
        sendBroadcast(getTime);
    }

    // update UI when user presses pause button (start and stop buttons appear)
    private void changeUiPausedState() {
        minusFiveImageButton.setVisibility(View.GONE);
        minusTextView.setVisibility(View.GONE);
        plusFiveImageButton.setVisibility(View.GONE);
        plusTextView.setVisibility(View.GONE);
        pauseImageButton.setVisibility(View.GONE);
        startImageButton.setVisibility(View.VISIBLE);
        stopImageButton.setVisibility(View.VISIBLE);
    }

    // update UI when user presses play button (start and stop buttons disappear)
    private void changeUiRunningState() {
        startImageButton.setVisibility(View.GONE);
        stopImageButton.setVisibility(View.GONE);
        minusFiveImageButton.setVisibility(View.VISIBLE);
        minusTextView.setVisibility(View.VISIBLE);
        plusFiveImageButton.setVisibility(View.VISIBLE);
        plusTextView.setVisibility(View.VISIBLE);
        pauseImageButton.setVisibility(View.VISIBLE);
    }

    // generic button push animation, makes button smaller on touch
    @SuppressLint("ClickableViewAccessibility")
    private void buttonPushAnim(ImageButton button) {
        ObjectAnimator scaler = ObjectAnimator.ofPropertyValuesHolder(
                button,
                PropertyValuesHolder.ofFloat("scaleX", 0.95f),
                PropertyValuesHolder.ofFloat("scaleY", 0.95f));
        scaler.setDuration(1);
        scaler.setRepeatMode(ValueAnimator.REVERSE);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scaler.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        scaler.reverse();
                        break;
                }
                return false;
            }
        });
    }
}