package com.chuck.catchthetime;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends Activity {
    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("HH:mm");

    private TextView status;
    private TextView nextTime;
    private Switch enabledSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(buildUi());
        refreshUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AlarmScheduler.isEnabled(this) && AlarmScheduler.canScheduleExact(this)) {
            AlarmScheduler.scheduleNext(this);
        }
        refreshUi();
    }

    private View buildUi() {
        int pad = dp(24);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, dp(40), pad, pad);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("Catch the Time");
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Tiny clock-pattern notifications. That’s the whole app.");
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subParams.setMargins(0, dp(12), 0, dp(28));
        root.addView(subtitle, subParams);

        enabledSwitch = new Switch(this);
        enabledSwitch.setText("Catch-time notifications");
        enabledSwitch.setTextSize(18);
        enabledSwitch.setChecked(AlarmScheduler.isEnabled(this));
        enabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AlarmScheduler.setEnabled(this, isChecked);
            if (isChecked) ensurePermissions();
            refreshUi();
        });
        root.addView(enabledSwitch);

        nextTime = new TextView(this);
        nextTime.setTextSize(22);
        nextTime.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        LinearLayout.LayoutParams nextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nextParams.setMargins(0, dp(32), 0, dp(8));
        root.addView(nextTime, nextParams);

        status = new TextView(this);
        status.setTextSize(14);
        status.setGravity(Gravity.CENTER);
        root.addView(status);

        Button permissionsButton = new Button(this);
        permissionsButton.setText("Fix permissions");
        permissionsButton.setOnClickListener(v -> ensurePermissions());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(0, dp(24), 0, 0);
        root.addView(permissionsButton, buttonParams);

        return root;
    }

    private void ensurePermissions() {
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST);
            return;
        }
        requestExactAlarmPermissionIfNeeded();
    }

    private void requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            requestExactAlarmPermissionIfNeeded();
            refreshUi();
        }
    }

    private void refreshUi() {
        if (enabledSwitch != null) {
            enabledSwitch.setChecked(AlarmScheduler.isEnabled(this));
        }

        LocalDateTime next = CatchTimes.nextAfter(LocalDateTime.now());
        nextTime.setText("Next: " + DISPLAY.format(next));

        if (!AlarmScheduler.isEnabled(this)) {
            status.setText("Off");
            return;
        }

        boolean exactOkay = AlarmScheduler.canScheduleExact(this);
        boolean notificationOkay = Build.VERSION.SDK_INT < 33 ||
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;

        if (exactOkay && notificationOkay) {
            status.setText("Armed ✓  •  " + CatchTimes.TIMES.size() + " catch moments/day");
        } else if (!exactOkay && !notificationOkay) {
            status.setText("Needs notification + exact alarm permission");
        } else if (!exactOkay) {
            status.setText("Needs exact alarm permission");
        } else {
            status.setText("Needs notification permission");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    "catch_times",
                    "Catch the time",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for neat-looking clock moments.");
            manager.createNotificationChannel(channel);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
