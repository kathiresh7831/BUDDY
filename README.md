# Buddy Android prototype

An initial Android prototype for HONOR Pad 10.

## Included
- Tap-to-talk Tamil speech recognition via Android's installed recognition service
- Tamil text-to-speech via installed Android voice
- Basic greeting and time commands
- GitHub Actions workflow to build a debug APK

## Not yet included
- Always-on "Hey Buddy" wake-word detection
- Locked-screen listening or automatic boot startup
- Speaker identification, guaranteed offline recognition, or female voice selection
- App control, personal memory, and online AI

The prototype uses the device's speech service, which may require internet or language downloads. Test on a real device. No secrets or credentials should be committed.

## Build
Push the project contents to the root of the GitHub repository and open Actions > Build Buddy APK > Run workflow. Download the Buddy-debug-apk artifact when the job succeeds.
