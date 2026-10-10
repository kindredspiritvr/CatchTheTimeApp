# Catch the Time

A tiny Android app that posts a local notification at curated neat-looking clock moments.

Example notification:

**Catch the time!**  
`12:34`

## What it does

- Works locally. No server, Firebase, account, ads, analytics, or Internet permission.
- Schedules only the next catch-time using Android `AlarmManager`.
- Reschedules after the notification fires, after reboot, after clock/time-zone changes, and after exact-alarm permission is granted.
- Requests notification permission on Android 13+.
- Requests Android's **Alarms & reminders** special access so moments can arrive on-time even during idle/Doze.

## Default catch times

The starter set is intentionally curated in `CatchTimes.java`: obvious runs, repeats and mirror/palindrome times such as `01:11`, `01:23`, `03:21`, `04:56`, `10:01`, `12:21`, `12:34`, `22:22`, `23:45`, and `00:00`.

Edit `CatchTimes.TIMES` to change the definition. The list currently contains 38 moments per day.

## Build in Android Studio

1. Open the `CatchTheTime` folder in Android Studio.
2. Let Gradle sync and install Android SDK 35 if prompted.
3. Choose **Build > Build APK(s)**.
4. Android Studio will produce `app-debug.apk` under `app/build/outputs/apk/debug/`.

## Build with GitHub Actions

This repo includes `.github/workflows/build-apk.yml`.

1. Put the project in a GitHub repo.
2. Open **Actions > Build APK > Run workflow**.
3. Download the `CatchTheTime-debug-apk` artifact when the run completes.

## Note on Play Store distribution

This project uses `SCHEDULE_EXACT_ALARM` because the app's whole purpose is minute-accurate, user-visible clock notifications. For a personal sideloaded APK this is straightforward. If publishing through Google Play, review the current exact-alarm permission policy before release.
