package stepn.sidekick.stepnsidekick;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.IBinder;

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
 * Foreground service to track GPS speed. Takes user's location every second and plays alerts if
 * the speed is outside the specified range. Sends info back to the SpeedTracker class if the
 * user has the app open.
 *
 * @author Bob Godfrey
 * @version 1.0.6
 */

public class GpsWatchService extends Service {

    public static boolean serviceRunning;

    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallBack;
    LocationRequest locationRequest;
    Location currentLocation;

    private double speedLowerLimit, speedUpperLimit, currentSpeed, energy;
    private float gpsAccuracy;
    private boolean voiceAlertsMinuteThirty, voiceAlertsSpeed, voiceAlertsTime;
    private boolean tenSecondTimer, tenSecondTimerDone, justPlayed;

    private boolean killThread;

    private int softAlert, spicyAlert, startSound;
    private SoundPool alertSoundPool;
    private SoundPool voiceSoundPool;

    CountDownTimer initialCountDownTimer, countDownTimer;

    long millisRemaining;

    Notification notification;
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public static final String COUNTDOWN_BR = "stepnsidekick.countdown_br";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceRunning = true;
        killThread = false;

        Intent notificationIntent = new Intent(this, SpeedTracker.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
               0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("STEPN Sidekick")
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSilent(true)
                .setContentIntent(pendingIntent);
        notification = notificationBuilder.build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        startForeground(1, notification);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        speedLowerLimit = intent.getDoubleExtra("min", 0);
        speedUpperLimit = intent.getDoubleExtra("max", 0);
        voiceAlertsMinuteThirty = intent.getBooleanExtra("voiceCountdownAlerts", true);
        voiceAlertsSpeed = intent.getBooleanExtra("voiceAlertsSpeed", true);
        voiceAlertsTime = intent.getBooleanExtra("voiceAlertsTime", true);
        energy = (intent.getDoubleExtra("energy", 0));
        tenSecondTimer = intent.getBooleanExtra("tenSecTimer", false);

        millisRemaining = (long) (energy * 5 * 60 * 1000) + 31000;

        tenSecondTimerDone =! tenSecondTimer;
        justPlayed = false;

        AudioAttributes alertAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();

        alertSoundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(alertAttributes)
                .build();

        AudioAttributes voiceAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();

        voiceSoundPool = new SoundPool.Builder()
                .setMaxStreams(15)
                .setAudioAttributes(voiceAttributes)
                .build();

        softAlert = alertSoundPool.load(this, R.raw.soft_alert_sound, 1);
        spicyAlert = alertSoundPool.load(this, R.raw.alert_sound, 1);
        startSound = alertSoundPool.load(this, R.raw.start_sound, 1);

        countDownTimer = new CountDownTimer(millisRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

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

                    int voiceTimeRemaining, voiceMinutes;

                    voiceTimeRemaining = voiceSoundPool.load(GpsWatchService.this, R.raw.time_remaining, 1);
                    voiceMinutes = voiceSoundPool.load(GpsWatchService.this, R.raw.minutes, 1);

                    new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (voiceAlertsTime) {

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
                                if (voiceAlertsSpeed) {
                                    if (!voiceAlertsTime) {
                                        voiceSpeed();
                                    }
                                }
                            }
                            void voiceSpeed() {
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
                        }).start();
                }

                // voice alerts for one minute and thirty seconds
                if (voiceAlertsMinuteThirty &&
                        (((millisUntilFinished / 1000) - 1) == 60 || ((millisUntilFinished / 1000) - 1) == 30) &&
                        millisRemaining != ((long) (energy * 5 * 60 * 1000) + 1000)) {

                    int voiceThirtySecondsRemaining, voiceOneMinuteRemaining;

                    voiceThirtySecondsRemaining = voiceSoundPool.load(GpsWatchService.this, R.raw.thirty_seconds_remaining, 1);
                    voiceOneMinuteRemaining = voiceSoundPool.load(GpsWatchService.this, R.raw.one_minute_remaining, 1);

                    // 60 second alert
                    if ((millisUntilFinished / 1000) - 1 == 60) {
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

                    // 30 second alert
                    if ((millisUntilFinished / 1000) - 1 == 30) {
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
                }

                // five second countdown
                if ((millisUntilFinished / 1000) == 3) {
                    new Thread (new Runnable() {
                        @Override
                        public void run() {
                            alertSoundPool.play(softAlert, 1, 1, 0, 0, 1);
                            for (int i = 0; i < 2; i++) {
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
                            alertSoundPool.play(startSound, 1, 1, 0, 0, 1);
                        }
                    }).start();
                }
            }

            @Override
            public void onFinish() {
                millisRemaining = -1;
                broadcastInfo();
                stopSelf();
            }
        };

        if (tenSecondTimer) {
            // initial ten second countdown timer

            initialCountDownTimer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long l) {

                    millisRemaining = l;

                    broadcastInfo();

                    if (l / 1000 == 2) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                alertSoundPool.play(softAlert, 1, 1, 0, 0, 1);
                                for (int i = 3; i > 1; i--) {
                                    try {
                                        Thread.sleep(1000);
                                        if (killThread) {
                                            return;
                                        }
                                        alertSoundPool.play(softAlert, 1, 1, 0, 0, 1);
                                    } catch (InterruptedException ex) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }
                        }).start();
                    }
                }

                @Override
                public void onFinish() {
                    alertSoundPool.play(startSound, 1, 1, 0, 0, 1);
                    countDownTimer.start();
                    tenSecondTimerDone = true;
                    tenSecondTimer = false;
                }
            };
            initialCountDownTimer.start();

        } else {
            alertSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    alertSoundPool.play(startSound, 1, 1, 0, 0, 1);
                }
            });
            countDownTimer.start();
        }

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // save the location
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        startLocationUpdates();

        return START_NOT_STICKY;
    }

    // sends broadcast to SpeedTracker class
    private void broadcastInfo() {
        if (tenSecondTimerDone) {
            String time = "";

            if (TimeUnit.MILLISECONDS.toHours(millisRemaining) > 0) {
                time += TimeUnit.MILLISECONDS.toHours(millisRemaining) + ":";
            }
            notificationBuilder.setContentText("Time Remaining: " + time +
                    String.format("%02d:%02d", (TimeUnit.MILLISECONDS.toMinutes(millisRemaining) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisRemaining))),
                            (TimeUnit.MILLISECONDS.toSeconds(millisRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisRemaining)))) +
                    "\nCurrent Speed: " + String.format("%.1f", currentSpeed) + " km/h");
        } else {
            notificationBuilder.setContentText("Starting in: " + TimeUnit.MILLISECONDS.toSeconds(millisRemaining));
        }
        notificationManager.notify(1, notificationBuilder.build());
        Intent sendInfo = new Intent(COUNTDOWN_BR);
        sendInfo.putExtra("countdown", millisRemaining);
        sendInfo.putExtra("speed", currentSpeed);
        sendInfo.putExtra("gpsAccuracy", gpsAccuracy);
        sendInfo.putExtra("energy", energy);
        sendInfo.putExtra("tenSecondTimerDone", tenSecondTimerDone);
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

    @Override
    public void onDestroy() {
        killThread = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
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

        serviceRunning = false;
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
