package stepn.sidekick.stepnsidekick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
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
            kmphLabelTextView, timeRemainingLabelTextView;
    ImageButton stopImageButton;
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

        GpsWatchService.serviceRunning = true;

        changedUI = false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            speedLowerLimit = extras.getDouble("min");
            speedUpperLimit = extras.getDouble("max");
            energy = extras.getDouble("energy");
            shoeType = extras.getString("shoeType");
            numFeet = extras.getInt("numFeet");
            tenSecondTimer = extras.getBoolean("tenSecondTimer");
            voiceAlertsMinuteThirty = extras.getBoolean("voiceCountdownAlerts");
            voiceAlertsSpeed = extras.getBoolean("voiceAlertsSpeed");
            voiceAlertsTime = extras.getBoolean("voiceAlertsTime");
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
            millis = intent.getLongExtra("countdown", 0);
            currentSpeed = intent.getDoubleExtra("speed", 0);
            gpsAccuracy = intent.getFloatExtra("gpsAccuracy", 0);
            energy = intent.getDoubleExtra("energy", 0);
            tenSecondTimerDone = intent.getBooleanExtra("tenSecondTimerDone", true);
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
            if (intent.getLongExtra("countdown", 0) == -1) {
                time = "00:00";
                onBackPressed();
            }
        }

        timeRemainingTextView.setText(time);
    }

    // sends initial data to service
    public void startService() {
        Intent serviceIntent = new Intent(this, GpsWatchService.class);

        serviceIntent.putExtra("energy", energy);
        serviceIntent.putExtra("min", speedLowerLimit);
        serviceIntent.putExtra("max", speedUpperLimit);
        serviceIntent.putExtra("voiceCountdownAlerts", voiceAlertsMinuteThirty);
        serviceIntent.putExtra("voiceAlertsSpeed", voiceAlertsSpeed);
        serviceIntent.putExtra("voiceAlertsTime", voiceAlertsTime);
        serviceIntent.putExtra("tenSecTimer", tenSecondTimer);

        startService(serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, GpsWatchService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(secondsAndSpeedReceiver, new IntentFilter(GpsWatchService.COUNTDOWN_BR));

        if (!GpsWatchService.serviceRunning) {
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

        stopImageButton = findViewById(R.id.stopImageButton);

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

        stopImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SpeedTracker.this, "STOPN", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

    }
}