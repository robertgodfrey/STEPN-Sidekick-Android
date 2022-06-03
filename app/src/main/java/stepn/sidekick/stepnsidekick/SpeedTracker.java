package stepn.sidekick.stepnsidekick;

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

import java.util.concurrent.TimeUnit;

/**
 * Activity displaying current speed, time remaining, GPS signal strength. Receives all info from
 * the GpsWatchService class.
 *
 * @author Bob Godfrey
 * @version 1.0.6
 */

public class SpeedTracker extends AppCompatActivity {

    // to ensure the UI returns to menu if app is opened after service has stopped
    //     1 - running
    //     0 - paused
    //    -1 - stopped
    public static int serviceStatus;

    private double speedLowerLimit, speedUpperLimit, energy;
    private long millis;
    private String shoeType;
    private int numFeet;
    private boolean tenSecondTimer, tenSecondTimerDone, changedUI, voiceAlertsSpeed,
            voiceAlertsTime, voiceAlertsMinuteThirty;

    CountDownTimer initialCountDownTimer;

    ImageView stopWatchImageView;
    TextView initialCountDownTextView, energyAmountTextView, currentSpeedTextView,
            timeRemainingTextView, shoeTypeTextView, shoeSpeedTextView, currentSpeedLabelTextView,
            kmphLabelTextView, timeRemainingLabelTextView, minusTextView, plusTextView;
    ImageButton pauseImageButton, minusFiveImageButton, plusFiveImageButton, startImageButton,
            stopImageButton;
    ImageView leftGps, centerGps, rightGps, footLeft, footCenter, footRight;

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

        serviceStatus = 1;

        changedUI = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            speedLowerLimit = extras.getDouble(Finals.MIN_SPEED);
            speedUpperLimit = extras.getDouble(Finals.MAX_SPEED);
            energy = extras.getDouble(Finals.ENERGY);
            shoeType = extras.getString(Finals.SHOE_TYPE);
            numFeet = extras.getInt(Finals.NUM_FEET);
            tenSecondTimer = extras.getBoolean(Finals.TEN_SECOND_TIMER);
            voiceAlertsMinuteThirty = extras.getBoolean(Finals.VOICE_ALERTS_CD);
            voiceAlertsSpeed = extras.getBoolean(Finals.VOICE_ALERTS_SPEED);
            voiceAlertsTime = extras.getBoolean(Finals.VOICE_ALERTS_TIME);
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
                minusFiveImageButton.setVisibility(View.GONE);
                minusTextView.setVisibility(View.INVISIBLE);
                plusFiveImageButton.setVisibility(View.GONE);
                plusTextView.setVisibility(View.INVISIBLE);
                pauseImageButton.setVisibility(View.GONE);

                currentSpeedTextView.setText("-");

                sendNewTime(Finals.PAUSE);

                startImageButton.setVisibility(View.VISIBLE);
                stopImageButton.setVisibility(View.VISIBLE);
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
        double currentSpeed = 0.0;
        float gpsAccuracy = 0.0f;

        if (intent.getExtras() != null) {
            millis = intent.getLongExtra(Finals.COUNTDOWN_TIME, 0);
            currentSpeed = intent.getDoubleExtra(Finals.CURRENT_SPEED, 0);
            gpsAccuracy = intent.getFloatExtra(Finals.GPS_ACCURACY, 0);
            energy = intent.getDoubleExtra(Finals.ENERGY, 0);
            tenSecondTimerDone = intent.getBooleanExtra(Finals.TEN_SECOND_DONE, true);
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

        energyAmountTextView.setText(String.format("%.1f", energy));

        String time = "";

        if (TimeUnit.MILLISECONDS.toHours(millis) > 0) {
            time += TimeUnit.MILLISECONDS.toHours(millis) + ":";
        }
        time += String.format("%02d:%02d", (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
        if (intent.getExtras() != null && tenSecondTimerDone) {
            if (intent.getLongExtra(Finals.COUNTDOWN_TIME, 0) == -1) {
                time = "00:00";
                onBackPressed();
            }
        }

        timeRemainingTextView.setText(time);
    }

    // sends initial data to service
    public void startService() {
        Intent serviceIntent = new Intent(this, GpsWatchService.class);

        serviceIntent.putExtra(Finals.MIN_SPEED, speedLowerLimit);
        serviceIntent.putExtra(Finals.MAX_SPEED, speedUpperLimit);
        serviceIntent.putExtra(Finals.ENERGY, energy);
        serviceIntent.putExtra(Finals.TEN_SECOND_TIMER, tenSecondTimer);
        serviceIntent.putExtra(Finals.VOICE_ALERTS_CD, voiceAlertsMinuteThirty);
        serviceIntent.putExtra(Finals.VOICE_ALERTS_SPEED, voiceAlertsSpeed);
        serviceIntent.putExtra(Finals.VOICE_ALERTS_TIME, voiceAlertsTime);

        startService(serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, GpsWatchService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(secondsAndSpeedReceiver, new IntentFilter(Finals.COUNTDOWN_BR));
        if (serviceStatus == 0 ) {
            minusFiveImageButton.setVisibility(View.GONE);
            minusTextView.setVisibility(View.INVISIBLE);
            plusFiveImageButton.setVisibility(View.GONE);
            plusTextView.setVisibility(View.INVISIBLE);
            pauseImageButton.setVisibility(View.GONE);
            currentSpeedTextView.setText("-");
            startImageButton.setVisibility(View.VISIBLE);
            stopImageButton.setVisibility(View.VISIBLE);
        } else if (serviceStatus == -1) {
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

        timeRemainingLabelTextView = findViewById(R.id.timeRemainingLabelTextView);
        timeRemainingTextView = findViewById(R.id.timeRemainingTextView);

        pauseImageButton = findViewById(R.id.pauseImageButton);
        stopImageButton = findViewById(R.id.stopImageButton);
        startImageButton = findViewById(R.id.playImageButton);

        minusFiveImageButton = findViewById(R.id.minusFiveSecondsButton);
        plusFiveImageButton = findViewById(R.id.plusFiveSecondsButton);
        minusTextView = findViewById(R.id.minusTextView);
        plusTextView = findViewById(R.id.plusTextView);

        String shoeSpeed = speedLowerLimit + " - " + speedUpperLimit + " km/h";
        shoeSpeedTextView.setText(shoeSpeed);
        shoeTypeTextView.setText(shoeType);

        energyAmountTextView.setText(String.valueOf(energy));

        switch (numFeet) {
            case 1:
                footRight.setImageResource(R.mipmap.footprint);
                footCenter.setVisibility(View.GONE);
                break;
            case 2:
                footCenter.setVisibility(View.VISIBLE);
                footLeft.setVisibility(View.GONE);
                break;
            case 3:
                footRight.setImageResource(R.mipmap.footprint);
                footCenter.setVisibility(View.VISIBLE);
                footLeft.setVisibility(View.VISIBLE);
                break;
            case 4:
                footRight.setImageResource(R.mipmap.trainer_t);
                footCenter.setVisibility(View.GONE);
                footLeft.setVisibility(View.GONE);
                break;
            default:
                footRight.setImageResource(R.mipmap.bolt);
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
                startImageButton.setVisibility(View.GONE);
                stopImageButton.setVisibility(View.GONE);

                sendNewTime(Finals.START);

                minusFiveImageButton.setVisibility(View.VISIBLE);
                minusTextView.setVisibility(View.VISIBLE);
                plusFiveImageButton.setVisibility(View.VISIBLE);
                plusTextView.setVisibility(View.VISIBLE);
                pauseImageButton.setVisibility(View.VISIBLE);
            }
        });

        buttonPushAnim(startImageButton);

        stopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SpeedTracker.this, "STOPN", Toast.LENGTH_LONG).show();
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
     * Broadcast to change the timer in GpsWatchService.
     *
     * @param option defines whether to pause/play, subtract, or add five seconds to the timer
     *               (uses Finals.class for options)
     */
    private void sendNewTime(int option) {
        Intent sendTime = new Intent(Finals.MODIFY_TIME_BR);
        sendTime.putExtra(Finals.TIME_MODIFIER, option);
        sendBroadcast(sendTime);
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