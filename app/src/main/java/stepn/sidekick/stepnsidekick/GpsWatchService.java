package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

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
 * @version 1.3.0 - Added shoe optimizer, changed layout, added ads
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
    private int softAlert, spicyAlert, startSound, avgSpeedCounter;

    private double speedLowerLimit, speedUpperLimit, currentSpeed, energy;
    private float gpsAccuracy, speedSum, currentAvgSpeed;
    private float[] avgSpeeds;
    private boolean voiceAlertsMinuteThirty, voiceAlertsTime, voiceAlertsCurrentSpeed, voiceAlertsAvgSpeed,
            tenSecondTimer, tenSecondTimerDone, justPlayed, killThread, firstFive;

    private long millisRemaining, millisBreak, millisBreak2;

    private final BroadcastReceiver updatedTimeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                int timeMod = intent.getIntExtra(TIME_MODIFIER, STOP_STOPPED);
                updateTimer(timeMod);
            }
        }
    };

    private final BroadcastReceiver respondToPing = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcastInfo();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        killThread = false;
        avgSpeeds = new float[300];
        avgSpeedCounter = 0;
        currentAvgSpeed = 0;
        firstFive = true;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        speedLowerLimit = intent.getDoubleExtra(MIN_SPEED, 0);
        speedUpperLimit = intent.getDoubleExtra(MAX_SPEED, 0);
        energy = (intent.getDoubleExtra(ENERGY, 0));
        tenSecondTimer = intent.getBooleanExtra(TEN_SECOND_TIMER, false);
        voiceAlertsMinuteThirty = intent.getBooleanExtra(VOICE_ALERTS_CD, true);
        voiceAlertsCurrentSpeed = intent.getBooleanExtra(VOICE_ALERTS_CURRENT_SPEED, true);
        voiceAlertsAvgSpeed = intent.getBooleanExtra(VOICE_ALERTS_AVG_SPEED, true);
        voiceAlertsTime = intent.getBooleanExtra(VOICE_ALERTS_TIME, true);

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
                .setMaxStreams(18)
                .setAudioAttributes(attributes)
                .build();

        softAlert = alertSoundPool.load(this, R.raw.soft_alert_sound, 1);
        spicyAlert = alertSoundPool.load(this, R.raw.alert_sound, 1);
        startSound = alertSoundPool.load(this, R.raw.start_sound, 1);

        // initial ten second countdown timer
        if (tenSecondTimer) {
            initTenSecCountDownTimer();
            initialCountDownTimer.start();
        } else {
            millisRemaining = (long) (energy * 5 * 60 * 1000) + 31000;
            initMainCountDownTimer();
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

        registerReceiver(updatedTimeReceiver, new IntentFilter(MODIFY_TIME_BR));
        registerReceiver(respondToPing, new IntentFilter(GET_TIME_BR));

        Intent notificationMainButtonIntent = new Intent(this, SpeedTracker.class);
        PendingIntent pendingOpenActivityIntent = PendingIntent.getActivity(this,
                0, notificationMainButtonIntent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("STEPN Sidekick")
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSilent(true)
                .setContentIntent(pendingOpenActivityIntent);

        if (tenSecondTimerDone) {
            Intent notificationPauseButtonIntent = new Intent(MODIFY_TIME_BR);
            notificationPauseButtonIntent.putExtra(TIME_MODIFIER, 0);
            PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, PAUSE_PAUSED,
                    notificationPauseButtonIntent, PendingIntent.FLAG_IMMUTABLE);

            notificationBuilder.addAction(0, getString(R.string.pause), pendingPauseIntent);
        }

        notification = notificationBuilder.build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        startForeground(1, notification);

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
                    if (currentSpeed < 1.0) {
                        currentSpeed = 0;
                    }
                }
                // speed alerts
                if ((currentSpeed <= speedLowerLimit || currentSpeed >= speedUpperLimit) && tenSecondTimerDone) {
                    if (!justPlayed) {
                        if (currentSpeed <= speedLowerLimit) {
                            // low-pitch alert
                            alertSoundPool.play(spicyAlert, 1, 1, 0, 0, 0.8f);
                        } else {
                            // high-pitch alert
                            alertSoundPool.play(spicyAlert, 1, 1, 0, 0, 1.2f);
                        }
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

                millisRemaining = millisUntilFinished;

                broadcastInfo();

                // keep track of energy
                if (((millisUntilFinished / 1000) - 1) % 60 == 0 && millisRemaining != ((long) (energy * 5 * 60 * 1000) + 1000)) {
                    energy = Math.round((millisRemaining / 300000.0) * 10) / 10.0;
                }

                // keep track of five min avg speed
                if (avgSpeedCounter < 60 * 5) {
                    if (firstFive) {
                        avgSpeeds[avgSpeedCounter] = (float) currentSpeed;
                        speedSum += avgSpeeds[avgSpeedCounter];
                        avgSpeedCounter++;
                        currentAvgSpeed = speedSum / avgSpeedCounter;
                    } else {
                        speedSum -= avgSpeeds[avgSpeedCounter];
                        avgSpeeds[avgSpeedCounter] = (float) currentSpeed;
                        speedSum += avgSpeeds[avgSpeedCounter];
                        currentAvgSpeed = speedSum / 300;
                        avgSpeedCounter++;
                    }
                } else {
                    speedSum -= avgSpeeds[0];
                    avgSpeeds[0] = (float) currentSpeed;
                    speedSum += avgSpeeds[0];
                    currentAvgSpeed = speedSum / 300;
                    avgSpeedCounter = 1;
                    firstFive = false;
                }

                // voice alerts for speed and time, every 5 mins
                if ((voiceAlertsCurrentSpeed || voiceAlertsAvgSpeed || voiceAlertsTime) &&
                        ((millisUntilFinished / 1000) - 1) % 300 == 0 && ((millisUntilFinished / 1000) - 1) != 0 &&
                        millisRemaining != ((long) (energy * 5 * 60 * 1000) + 1000)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (voiceAlertsTime) {
                                voiceTime(millisUntilFinished);
                            } else if (voiceAlertsCurrentSpeed) {
                                voiceCurrentSpeed();
                            } else if (voiceAlertsAvgSpeed) {
                                voiceAvgSpeed();
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
                tenSecondTimerDone = true;
                tenSecondTimer = false;
                millisRemaining = (long) (energy * 5 * 60 * 1000) + 31000;
                updateTimer(START_RUNNING);
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

            // aight check it out. i tried doing another setOnLoadComplete listener for all of these voice alerts,
            // but when i do that it plays the sound twice (even when i move voiceMinutes below this code). so...
            // it's staying like this for now
            try {
                Thread.sleep(150);
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
                    Thread.sleep(150);
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
                    Thread.sleep(150);
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
                    Thread.sleep(150);
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
            if (voiceAlertsCurrentSpeed) {
                voiceCurrentSpeed();
            } else if (voiceAlertsAvgSpeed) {
                voiceAvgSpeed();
            }
        }
    }

    // plays voice alerts for current speed
    private void voiceCurrentSpeed() {
        int voiceCurrentSpeed, voiceCurrentSpeedNum1, voiceCurrentSpeedNum2;

        voiceCurrentSpeed = voiceSoundPool.load(GpsWatchService.this, R.raw.current_speed, 1);

        voiceCurrentSpeedNum1 = speedOne((int) Math.floor(currentSpeed));
        voiceCurrentSpeedNum2 = speedTwo((int) ((currentSpeed - Math.floor(currentSpeed)) * 10));

        if (killThread) {
            return;
        }

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        playSpeedVoice(voiceCurrentSpeed, voiceCurrentSpeedNum1, voiceCurrentSpeedNum2, false);
        
        if (voiceAlertsAvgSpeed) {
            try {
                Thread.sleep(1800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            voiceAvgSpeed();
        }

    }

    private void voiceAvgSpeed() {
        int voiceAvgSpeed, voiceAvgSpeedNum1, voiceAvgSpeedNum2;

        voiceAvgSpeed = voiceSoundPool.load(GpsWatchService.this, R.raw.avg_speed, 1);

        voiceAvgSpeedNum1 = speedOne((int) Math.floor(currentAvgSpeed));
        voiceAvgSpeedNum2 = speedTwo((int) ((currentAvgSpeed - Math.floor(currentAvgSpeed)) * 10));

        if (killThread) {
            return;
        }

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        playSpeedVoice(voiceAvgSpeed, voiceAvgSpeedNum1, voiceAvgSpeedNum2, true);

    }

    // chooses which voice file to play depending on speed (first number)
    private int speedOne(int speed) {
        int speedVoice;

        millisBreak = 200;

        switch (speed) {
            case 0:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.zero, 1);
                millisBreak += 230;
                break;
            case 1:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.one, 1);
                millisBreak += 70;
                break;
            case 2:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.two, 1);
                millisBreak += 70;
                break;
            case 3:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.three, 1);
                millisBreak += 40;
                break;
            case 4:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.four, 1);
                millisBreak += 60;
                break;
            case 5:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.five, 1);
                millisBreak += 100;
                break;
            case 6:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.six, 1);
                millisBreak += 120;
                break;
            case 7:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.sevn, 1);
                millisBreak += 180;
                break;
            case 8:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.eight, 1);
                break;
            case 9:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.nine, 1);
                millisBreak += 120;
                break;
            case 10:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.ten, 1);
                millisBreak += 60;
                break;
            case 11:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.eleven, 1);
                millisBreak += 180;
                break;
            case 12:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.twelve, 1);
                millisBreak += 175;
                break;
            case 13:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.thirteen, 1);
                millisBreak += 265;
                break;
            case 14:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.fourteen, 1);
                millisBreak += 290;
                break;
            case 15:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.fifteen, 1);
                millisBreak += 440;
                break;
            case 16:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.sixteen, 1);
                millisBreak += 370;
                break;
            case 17:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.seventeen, 1);
                millisBreak += 400;
                break;
            case 18:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.eighteen, 1);
                millisBreak += 235;
                break;
            case 19:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.nineteen, 1);
                millisBreak += 280;
                break;
            case 20:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.twenty, 1);
                break;
            default:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.over_twenty, 1);
                millisBreak += 430;
                break;
        }

        return speedVoice;
    }

    // chooses which voice file to play depending on speed (second number)
    private int speedTwo(int speed) {
        int speedVoice;

        millisBreak2 = 200;

        switch (speed) {
            case 1:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.one, 1);
                millisBreak2 += 70;
                break;
            case 2:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.two, 1);
                millisBreak2 += 70;
                break;
            case 3:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.three, 1);
                millisBreak2 += 40;
                break;
            case 4:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.four, 1);
                millisBreak2 += 60;
                break;
            case 5:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.five, 1);
                millisBreak2 += 100;
                break;
            case 6:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.six, 1);
                millisBreak2 += 120;
                break;
            case 7:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.sevn, 1);
                millisBreak2 += 180;
                break;
            case 8:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.eight, 1);
                break;
            case 9:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.nine, 1);
                millisBreak2 += 120;
                break;
            default:
                speedVoice = voiceSoundPool.load(GpsWatchService.this, R.raw.zero, 1);
                millisBreak2 += 230;
                break;
        }

        return speedVoice;
    }

    // plays voice for speed alerts
    // extraTime is for "5 min average" (takes longer to say than "average speed")
    private void playSpeedVoice(int speedType, int speedOne, int speedTwo, boolean extraTime) {
        int voicePoint = voiceSoundPool.load(GpsWatchService.this, R.raw.point, 1);
        int voiceKiloPerHour = voiceSoundPool.load(GpsWatchService.this, R.raw.kilo_per_hour, 1);

        voiceSoundPool.play(speedType, 1, 1, 0, 0, 1);

        try {
            Thread.sleep(extraTime ? 1500 : 1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (killThread) {
            return;
        }

        voiceSoundPool.play(speedOne, 1, 1, 0, 0, 1);

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
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (killThread) {
            return;
        }

        voiceSoundPool.play(speedTwo, 1, 1, 0, 0, 1);

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
                    Thread.sleep(200);
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
                    Thread.sleep(200);
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
            String timeAndSpeed;

            if (TimeUnit.MILLISECONDS.toHours(millisRemaining) > 0) {
                time += TimeUnit.MILLISECONDS.toHours(millisRemaining) + ":";
            }
            timeAndSpeed = getString(R.string.time_remaining) + " " + time + String.format("%02d:%02d",
                    (TimeUnit.MILLISECONDS.toMinutes(millisRemaining) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisRemaining))),
                    (TimeUnit.MILLISECONDS.toSeconds(millisRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisRemaining)))) +
                    "\n" + getString(R.string.current_speed) + " " + String.format("%.1f", currentSpeed) + " km/h";
            notificationBuilder.setContentText(timeAndSpeed)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(timeAndSpeed + "\n" +
                            getString(R.string.five_min_average) + " " + String.format("%.1f", currentAvgSpeed) + " km/h"));
        } else {
            notificationBuilder.setContentText(getString(R.string.starting_in) + TimeUnit.MILLISECONDS.toSeconds(millisRemaining));
        }
        notificationManager.notify(1, notificationBuilder.build());
        Intent sendInfo = new Intent(COUNTDOWN_BR);
        sendInfo.putExtra(COUNTDOWN_TIME, millisRemaining);
        sendInfo.putExtra(CURRENT_SPEED, currentSpeed);
        sendInfo.putExtra(AVERAGE_SPEED, currentAvgSpeed);
        sendInfo.putExtra(GPS_ACCURACY, gpsAccuracy);
        sendInfo.putExtra(ENERGY, energy);
        sendInfo.putExtra(TEN_SECOND_DONE, tenSecondTimerDone);
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
        if (timeMod == SUB_FIVE) {
            // subtract five seconds
            mainCountDownTimer.cancel();
            millisRemaining -= 5000;
            initMainCountDownTimer();
            mainCountDownTimer.start();
        } else if (timeMod == ADD_FIVE) {
            // add five seconds
            mainCountDownTimer.cancel();
            millisRemaining += 5000;
            initMainCountDownTimer();
            mainCountDownTimer.start();
        } else if (timeMod == START_RUNNING){
            // start timer
            initMainCountDownTimer();
            mainCountDownTimer.start();
            startLocationUpdates();
            SpeedTracker.serviceStatus = START_RUNNING;

            Intent notificationPauseButtonIntent = new Intent(MODIFY_TIME_BR);
            notificationPauseButtonIntent.putExtra(TIME_MODIFIER, 0);
            PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, PAUSE_PAUSED,
                    notificationPauseButtonIntent, PendingIntent.FLAG_IMMUTABLE);

            notificationBuilder.mActions.clear();
            notificationBuilder.addAction(0, getString(R.string.pause), pendingPauseIntent);
            notificationManager.notify(1, notificationBuilder.build());
        } else if (timeMod == PAUSE_PAUSED){
            // pause timer
            mainCountDownTimer.cancel();
            stopLocationUpdates();
            SpeedTracker.serviceStatus = PAUSE_PAUSED;
            broadcastInfo();

            Intent notificationStartButtonIntent = new Intent(MODIFY_TIME_BR);
            notificationStartButtonIntent.putExtra(TIME_MODIFIER, 1);
            PendingIntent pendingStartIntent = PendingIntent.getBroadcast(this, START_RUNNING,
                    notificationStartButtonIntent, PendingIntent.FLAG_IMMUTABLE);

            Intent notificationStopButtonIntent = new Intent(MODIFY_TIME_BR);
            notificationStopButtonIntent.putExtra(TIME_MODIFIER, -1);
            PendingIntent pendingStopIntent = PendingIntent.getBroadcast(this, STOP_STOPPED,
                    notificationStopButtonIntent, PendingIntent.FLAG_IMMUTABLE);

            notificationBuilder.setContentText(getString(R.string.paused));
            notificationBuilder.mActions.clear();
            notificationBuilder.addAction(0, getString(R.string.stop), pendingStopIntent);
            notificationBuilder.addAction(0, getString(R.string.start), pendingStartIntent);
            notificationManager.notify(1, notificationBuilder.build());
        } else {
            // stop
            broadcastInfo();
            SpeedTracker.serviceStatus = STOP_STOPPED;
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

        SpeedTracker.serviceStatus = STOP_STOPPED;
        unregisterReceiver(updatedTimeReceiver);
        unregisterReceiver(respondToPing);

        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}