package stepn.sidekick.stepnsidekick;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

/**
 * Foreground service to track speed using GPS. Takes user's location every second and plays alarm
 * sound if the speed is outside the specified range. Broadcasts info back to the SpeedTracker class.
 *
 * @author Bob Godfrey
 * @version 1.0.6
 */

public class GpsWatchService extends Service {

    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallBack;
    LocationRequest locationRequest;
    Location currentLocation;

    Notification notification;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    CountDownTimer initialCountDownTimer, mainCountDownTimer;

    private SoundPool alertSoundPool, voiceSoundPool;
    private int softAlert, spicyAlert, startSound;

    private double speedLowerLimit, speedUpperLimit, currentSpeed, energy;
    private float gpsAccuracy;
    private boolean voiceAlertsMinuteThirty, voiceAlertsSpeed, voiceAlertsTime,
            tenSecondTimer, tenSecondTimerDone, justPlayed, killThread;

    long millisRemaining;

    private final BroadcastReceiver updatedTimeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("GpsWatchService", "broadcast received");
            if (intent.getExtras() != null) {
                int timeMod = intent.getIntExtra(Finals.TIME_MODIFIER, Finals.STOP);
                Log.d("GpsWatchService", "timeMod: " + timeMod);
                updateTimer(timeMod);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        killThread = false;

        Intent notificationMainButtonIntent = new Intent(this, SpeedTracker.class);
        PendingIntent pendingOpenActivityIntent = PendingIntent.getActivity(this,
               0, notificationMainButtonIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent notificationPauseButtonIntent = new Intent(Finals.MODIFY_TIME_BR);
        notificationPauseButtonIntent.putExtra(Finals.TIME_MODIFIER, 0);
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, Finals.PAUSE,
                notificationPauseButtonIntent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("STEPN Sidekick")
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSilent(true)
                .addAction(0, getString(R.string.pause), pendingPauseIntent)
                .setContentIntent(pendingOpenActivityIntent);
        notification = notificationBuilder.build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        startForeground(1, notification);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        speedLowerLimit = intent.getDoubleExtra(Finals.MIN_SPEED, 0);
        speedUpperLimit = intent.getDoubleExtra(Finals.MAX_SPEED, 0);
        energy = (intent.getDoubleExtra(Finals.ENERGY, 0));
        tenSecondTimer = intent.getBooleanExtra(Finals.TEN_SECOND_TIMER, false);
        voiceAlertsMinuteThirty = intent.getBooleanExtra(Finals.VOICE_ALERTS_CD, true);
        voiceAlertsSpeed = intent.getBooleanExtra(Finals.VOICE_ALERTS_SPEED, true);
        voiceAlertsTime = intent.getBooleanExtra(Finals.VOICE_ALERTS_TIME, true);

        millisRemaining = (long) (energy * 5 * 60 * 1000) + 31000;

        tenSecondTimerDone =! tenSecondTimer;
        justPlayed = false;

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();

        alertSoundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(attributes)
                .build();

        voiceSoundPool = new SoundPool.Builder()
                .setMaxStreams(15)
                .setAudioAttributes(attributes)
                .build();

        softAlert = alertSoundPool.load(this, R.raw.soft_alert_sound, 1);
        spicyAlert = alertSoundPool.load(this, R.raw.alert_sound, 1);
        startSound = alertSoundPool.load(this, R.raw.start_sound, 1);

        initMainCountDownTimer();

        // initial ten second countdown timer
        if (tenSecondTimer) {
            initTenSecCountDownTimer();
            initialCountDownTimer.start();
        } else {
            alertSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    alertSoundPool.play(startSound, 1, 1, 0, 0, 1);
                }
            });
            mainCountDownTimer.start();
        }

        initLocationCallback();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        startLocationUpdates();

        registerReceiver(updatedTimeReceiver, new IntentFilter(Finals.MODIFY_TIME_BR));

        return START_NOT_STICKY;
    }

    // initializes locationCallBack
    private void initLocationCallback() {
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                currentLocation = locationResult.getLastLocation();

                if (currentLocation.hasAccuracy()) {
                    gpsAccuracy = currentLocation.getAccuracy();
                } else {
                    gpsAccuracy = 0;
                }

                if (currentLocation.hasSpeed()) {
                    currentSpeed = currentLocation.getSpeed() * 3.6;
                    if (currentSpeed < 1.1) {
                        currentSpeed = 0;
                    }
                }
                // speed alerts
                if ((currentSpeed <= speedLowerLimit || currentSpeed >= speedUpperLimit) && tenSecondTimerDone) {
                    if (!justPlayed) {
                        alertSoundPool.play(spicyAlert, 1, 1, 0, 0, 1);
                        justPlayed = true;
                    } else {
                        justPlayed = false;
                    }
                }
            }
        };
    }
    // initializes the main countdown timer for the activity
    private void initMainCountDownTimer() {
        mainCountDownTimer = new CountDownTimer(millisRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.d("MAIN COUNTDOWN TIMER", "TICK: " + (millisRemaining / 1000));

                millisRemaining = millisUntilFinished;

                broadcastInfo();

                // keep track of energy
                if (((millisUntilFinished / 1000) - 1) % 60 == 0 && millisRemaining != ((long) (energy * 5 * 60 * 1000) + 1000)) {
                    energy = Math.round((millisRemaining / 300000.0) * 10) / 10.0;
                }

                // voice alerts for speed and time, every 5 mins
                if ((voiceAlertsSpeed || voiceAlertsTime) &&
                        ((millisUntilFinished / 1000) - 1) % 300 == 0 && ((millisUntilFinished / 1000) - 1) != 0 &&
                        millisRemaining != ((long) (energy * 5 * 60 * 1000) + 1000)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (voiceAlertsTime) {
                                voiceTime(millisUntilFinished);
                            } else if (voiceAlertsSpeed) {
                                voiceSpeed();
                            }
                        }
                    }).start();
                }

                // voice alerts for one minute and thirty seconds
                if (voiceAlertsMinuteThirty &&
                        (((millisUntilFinished / 1000) - 1) == 60 || ((millisUntilFinished / 1000) - 1) == 30) &&
                        millisRemaining != ((long) (energy * 5 * 60 * 1000) + 1000)) {

                    // 60 second alert
                    if ((millisUntilFinished / 1000) - 1 == 60) {
                        oneMinuteRemaining();
                    }

                    // 30 second alert
                    if ((millisUntilFinished / 1000) - 1 == 30) {
                        thirtySecondsRemaining();
                    }
                }

                // three second countdown
                if ((millisUntilFinished / 1000) == 3) {
                    threeSecondCountdown(true);
                }
            }

            @Override
            public void onFinish() {
                millisRemaining = -1;
                broadcastInfo();
                stopSelf();
            }
        };
    }

    // initializes the ten-second countdown timer (before starting the activity)
    private void initTenSecCountDownTimer() {
        initialCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {

                millisRemaining = l;

                broadcastInfo();

                if (l / 1000 == 2) {
                    threeSecondCountdown(false);
                }
            }

            @Override
            public void onFinish() {
                alertSoundPool.play(startSound, 1, 1, 0, 0, 1);
                mainCountDownTimer.start();
                tenSecondTimerDone = true;
                tenSecondTimer = false;
            }
        };
    }

    private void threeSecondCountdown(boolean playStartSound) {
        new Thread (new Runnable() {
            @Override
            public void run() {
                alertSoundPool.play(softAlert, 1, 1, 0, 0, 1);
                for (int i = 3; i > 1; i--) {
                    if (killThread) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                        alertSoundPool.play(softAlert, 1, 1, 0, 0, 1);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                if (playStartSound) {
                    alertSoundPool.play(startSound, 1, 1, 0, 0, 1);
                }
            }
        }).start();
    }

    // plays voice alerts for how much time is remaining in activity
    private void voiceTime(long millisUntilFinished) {
        int voiceTimeRemaining, voiceMinutes;

        voiceTimeRemaining = voiceSoundPool.load(GpsWatchService.this, R.raw.time_remaining, 1);
        voiceMinutes = voiceSoundPool.load(GpsWatchService.this, R.raw.minutes, 1);

        if (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) < 180) {

            int minConversion = (int) TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);

            // greater than 3 hours
            if (minConversion > 180) {
                return;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            voiceSoundPool.play(voiceTimeRemaining, 1, 1, 0, 0, 1);
            try {
                Thread.sleep(1150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // two hour range
            int voiceHours;
            if (minConversion >= 120) {
                long hourMillis;

                if (minConversion == 120) {
                    voiceHours = voiceSoundPool.load(GpsWatchService.this, R.raw.two_hours_end, 1);
                    hourMillis = 800;
                } else {
                    voiceHours = voiceSoundPool.load(GpsWatchService.this, R.raw.two_hours, 1);
                    hourMillis = 517;
                }

                minConversion -= 120;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                voiceSoundPool.play(voiceHours, 1, 1, 0, 0, 1);

                try {
                    Thread.sleep(hourMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // one hour range
            if (minConversion >= 60) {
                if (minConversion == 60) {
                    voiceHours = voiceSoundPool.load(GpsWatchService.this, R.raw.one_hour_end, 1);
                } else {
                    voiceHours = voiceSoundPool.load(GpsWatchService.this, R.raw.one_hour, 1);
                }

                minConversion -= 60;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                voiceSoundPool.play(voiceHours, 1, 1, 0, 0, 1);

                try {
                    Thread.sleep(580);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (killThread) {
                return;
            }

            int timeMinsStart = -1;
            int timeMinsEnd = -1;
            long millisToSleep = 290;
            switch (minConversion) {
                case 5:
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.five, 1);
                    millisToSleep += 100;
                    break;
                case 10:
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.ten, 1);
                    break;
                case 15:
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.fifteen, 1);
                    millisToSleep += 200;
                    break;
                case 20:
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.twenty, 1);
                    millisToSleep += 35;
                    break;
                case 25:
                    timeMinsStart = voiceSoundPool.load(GpsWatchService.this, R.raw.twenty, 1);
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.five_mid, 1);
                    break;
                case 30:
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.thirty, 1);
                    millisToSleep += 50;
                    break;
                case 35:
                    timeMinsStart = voiceSoundPool.load(GpsWatchService.this, R.raw.thirty, 1);
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.five_mid, 1);
                    break;
                case 40:
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.forty, 1);
                    millisToSleep += 35;
                    break;
                case 45:
                    timeMinsStart = voiceSoundPool.load(GpsWatchService.this, R.raw.forty, 1);
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.five_mid, 1);
                    break;
                case 50:
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.fifty, 1);
                    millisToSleep += 35;
                    break;
                case 55:
                    timeMinsStart = voiceSoundPool.load(GpsWatchService.this, R.raw.fifty, 1);
                    timeMinsEnd = voiceSoundPool.load(GpsWatchService.this, R.raw.five_mid, 1);
                    break;
                default:
                    break;
            }
            if (timeMinsStart != -1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                voiceSoundPool.play(timeMinsStart, 1, 1, 0, 0, 1);
                try {
                    Thread.sleep(289);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (killThread) {
                return;
            }
            if (timeMinsEnd != -1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                voiceSoundPool.play(timeMinsEnd, 1, 1, 0, 0, 1);
                try {
                    Thread.sleep(millisToSleep);
                    voiceSoundPool.play(voiceMinutes, 1, 1, 0, 0, 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (killThread) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (voiceAlertsSpeed) {
                voiceSpeed();
            }
        }
    }

    // plays voice alerts for current speed
    private void voiceSpeed() {
        if (killThread) {
            return;
        }
        // voice speed alerts
        int voiceCurrentSpeed, voiceCurrentSpeedNum1, voicePoint, voiceCurrentSpeedNum2, voiceKiloPerHour;

        voiceCurrentSpeed = voiceSoundPool.load(GpsWatchService.this, R.raw.current_speed, 1);
        voicePoint = voiceSoundPool.load(GpsWatchService.this, R.raw.point, 1);
        voiceKiloPerHour = voiceSoundPool.load(GpsWatchService.this, R.raw.kilo_per_hour, 1);

        long millisBreak = 200;
        long millisBreak2 = 200;

        switch ((int) ((currentSpeed - Math.floor(currentSpeed)) * 10)) {
            case 1:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.one, 1);
                millisBreak2 += 70;
                break;
            case 2:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.two, 1);
                millisBreak2 += 70;
                break;
            case 3:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.three, 1);
                millisBreak2 += 40;
                break;
            case 4:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.four, 1);
                millisBreak2 += 50;
                break;
            case 5:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.five, 1);
                millisBreak2 += 100;
                break;
            case 6:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.six, 1);
                millisBreak2 += 120;
                break;
            case 7:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.sevn, 1);
                millisBreak2 += 180;
                break;
            case 8:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.eight, 1);
                break;
            case 9:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.nine, 1);
                millisBreak2 += 120;
                break;
            default:
                voiceCurrentSpeedNum2 = voiceSoundPool.load(GpsWatchService.this, R.raw.zero, 1);
                millisBreak2 += 230;
                break;
        }

        switch ((int) Math.floor(currentSpeed)) {
            case 0:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.zero, 1);
                millisBreak += 230;
                break;
            case 1:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.one, 1);
                millisBreak += 70;
                break;
            case 2:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.two, 1);
                millisBreak += 70;
                break;
            case 3:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.three, 1);
                millisBreak += 40;
                break;
            case 4:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.four, 1);
                millisBreak += 50;
                break;
            case 5:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.five, 1);
                millisBreak += 100;
                break;
            case 6:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.six, 1);
                millisBreak += 120;
                break;
            case 7:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.sevn, 1);
                millisBreak += 180;
                break;
            case 8:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.eight, 1);
                break;
            case 9:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.nine, 1);
                millisBreak += 120;
                break;
            case 10:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.ten, 1);
                millisBreak += 60;
                break;
            case 11:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.eleven, 1);
                millisBreak += 180;
                break;
            case 12:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.twelve, 1);
                millisBreak += 175;
                break;
            case 13:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.thirteen, 1);
                millisBreak += 265;
                break;
            case 14:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.fourteen, 1);
                millisBreak += 290;
                break;
            case 15:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.fifteen, 1);
                millisBreak += 440;
                break;
            case 16:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.sixteen, 1);
                millisBreak += 370;
                break;
            case 17:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.seventeen, 1);
                millisBreak += 400;
                break;
            case 18:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.eighteen, 1);
                millisBreak += 235;
                break;
            case 19:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.nineteen, 1);
                millisBreak += 280;
                break;
            case 20:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.twenty, 1);
                break;
            default:
                voiceCurrentSpeedNum1 = voiceSoundPool.load(GpsWatchService.this, R.raw.over_twenty, 1);
                millisBreak += 430;
                break;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        voiceSoundPool.play(voiceCurrentSpeed, 1, 1, 0, 0, 1);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (killThread) {
            return;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        voiceSoundPool.play(voiceCurrentSpeedNum1, 1, 1, 0, 0, 1);

        try {
            Thread.sleep(millisBreak);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (killThread) {
            return;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        voiceSoundPool.play(voicePoint, 1, 1, 0, 0, 1);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (killThread) {
            return;
        }


        voiceSoundPool.play(voiceCurrentSpeedNum2, 1, 1, 0, 0, 1);

        try {
            Thread.sleep(millisBreak2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (killThread) {
            return;
        }

        voiceSoundPool.play(voiceKiloPerHour, 1, 1, 0, 0, 1);
    }

    // one minute remaining voice alert
    private void oneMinuteRemaining() {
        int voiceOneMinuteRemaining = voiceSoundPool.load(GpsWatchService.this, R.raw.one_minute_remaining, 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                voiceSoundPool.play(voiceOneMinuteRemaining, 1, 1, 0, 0, 1);
            }
        }).start();
    }

    // thirty seconds remaining voice alert
    private void thirtySecondsRemaining() {
        int voiceThirtySecondsRemaining = voiceSoundPool.load(GpsWatchService.this, R.raw.thirty_seconds_remaining, 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                voiceSoundPool.play(voiceThirtySecondsRemaining, 1, 1, 0, 0, 1);
            }
        }).start();
    }

    // sends broadcast to SpeedTracker class, updates notification info
    private void broadcastInfo() {
        if (tenSecondTimerDone) {
            String time = "";

            if (TimeUnit.MILLISECONDS.toHours(millisRemaining) > 0) {
                time += TimeUnit.MILLISECONDS.toHours(millisRemaining) + ":";
            }
            notificationBuilder.setContentText(getString(R.string.time_remaining) + " " + time +
                    String.format("%02d:%02d", (TimeUnit.MILLISECONDS.toMinutes(millisRemaining) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisRemaining))),
                            (TimeUnit.MILLISECONDS.toSeconds(millisRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisRemaining)))) +
                    "\n" + getString(R.string.current_speed) + " " + String.format("%.1f", currentSpeed) + " km/h");
        } else {
            notificationBuilder.setContentText(getString(R.string.starting_in) + TimeUnit.MILLISECONDS.toSeconds(millisRemaining));
        }
        notificationManager.notify(1, notificationBuilder.build());
        Intent sendInfo = new Intent(Finals.COUNTDOWN_BR);
        sendInfo.putExtra(Finals.COUNTDOWN_TIME, millisRemaining);
        sendInfo.putExtra(Finals.CURRENT_SPEED, currentSpeed);
        sendInfo.putExtra(Finals.GPS_ACCURACY, gpsAccuracy);
        sendInfo.putExtra(Finals.ENERGY, energy);
        sendInfo.putExtra(Finals.TEN_SECOND_DONE, tenSecondTimerDone);
        sendBroadcast(sendInfo);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
    }

    @SuppressLint("RestrictedApi")
    private void updateTimer(int timeMod) {
        if (timeMod == -5) {
            // sub five
            Log.d("GpsWatchService", "sub 5");
            mainCountDownTimer.cancel();
            millisRemaining -= 5000;
            initMainCountDownTimer();
            mainCountDownTimer.start();
        } else if (timeMod == 5) {
            // add 5
            Log.d("GpsWatchService", "add 5");
            mainCountDownTimer.cancel();
            millisRemaining += 5000;
            initMainCountDownTimer();
            mainCountDownTimer.start();
        } else if (timeMod == 1){
            // play
            initMainCountDownTimer();
            mainCountDownTimer.start();
            startLocationUpdates();

            SpeedTracker.serviceStatus = 1;

            Intent notificationPauseButtonIntent = new Intent(Finals.MODIFY_TIME_BR);
            notificationPauseButtonIntent.putExtra(Finals.TIME_MODIFIER, 0);
            PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, Finals.PAUSE,
                    notificationPauseButtonIntent, PendingIntent.FLAG_IMMUTABLE);

            notificationBuilder.mActions.clear();
            notificationBuilder.addAction(0, getString(R.string.pause), pendingPauseIntent);
            notificationManager.notify(1, notificationBuilder.build());

        } else if (timeMod == 0){
            // pause
            mainCountDownTimer.cancel();
            stopLocationUpdates();

            SpeedTracker.serviceStatus = 0;

            Intent notificationStartButtonIntent = new Intent(Finals.MODIFY_TIME_BR);
            notificationStartButtonIntent.putExtra(Finals.TIME_MODIFIER, 1);
            PendingIntent pendingStartIntent = PendingIntent.getBroadcast(this, Finals.START,
                    notificationStartButtonIntent, PendingIntent.FLAG_IMMUTABLE);

            Intent notificationStopButtonIntent = new Intent(Finals.MODIFY_TIME_BR);
            notificationStopButtonIntent.putExtra(Finals.TIME_MODIFIER, -1);
            PendingIntent pendingStopIntent = PendingIntent.getBroadcast(this, Finals.STOP,
                    notificationStopButtonIntent, PendingIntent.FLAG_IMMUTABLE);

            notificationBuilder.setContentText(getString(R.string.paused));
            notificationBuilder.mActions.clear();
            notificationBuilder.addAction(0, getString(R.string.stop), pendingStopIntent);
            notificationBuilder.addAction(0, getString(R.string.start), pendingStartIntent);
            notificationManager.notify(1, notificationBuilder.build());

        } else {
            // stop
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        killThread = true;
        if (mainCountDownTimer != null) {
            mainCountDownTimer.cancel();
        }
        if (initialCountDownTimer != null) {
            initialCountDownTimer.cancel();
        }
        stopLocationUpdates();

        if (alertSoundPool != null) {
            alertSoundPool.release();
            alertSoundPool = null;
        }

        if (voiceSoundPool != null) {
            voiceSoundPool.release();
            voiceSoundPool = null;
        }

        SpeedTracker.serviceStatus = -1;
        unregisterReceiver(updatedTimeReceiver);

        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
