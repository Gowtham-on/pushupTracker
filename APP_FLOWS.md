# Pushup Pulse App Flows

This document maps the main runtime flows in the Android app so feature work can start from the right files.

## Engagement Feature Direction

The app now uses a personal “Momentum Quest” instead of only a static daily challenge. The feature is designed around three engagement principles from behavior-change and gamification research:

- Personal challenges can increase physical activity, but unfair competition can reduce engagement when users are mismatched.
- Streaks help form routines, but pressure-only streaks can become discouraging or unhealthy.
- Motivation is stronger when users get autonomy, competence feedback, and a clear next action.

Product decision:
- Keep the challenge personal and adaptive rather than social or leaderboard-based.
- Use a weekly 5-day quest with two rest shields so the app encourages consistency without punishing healthy rest.
- Use an adaptive daily rep target based on recent activity so the goal feels achievable but still progressive.
- Show immediate progress, streak, shields, and a simple action button on Home.

Research notes:
- A large-scale analysis of mobile walking competitions found physical activity increased during challenges, but engagement dropped when participants were mismatched, which argues for personal/adaptive challenges before social competition: https://arxiv.org/abs/1702.07437
- A large activity-tracking goal study found the first 7 days are predictive of long-term goal success, which supports making the weekly quest visible early and keeping today’s action obvious: https://arxiv.org/abs/1904.02813
- Self-determination theory emphasizes autonomy, competence, and relatedness; Momentum Quest focuses on autonomy and competence by avoiding forced leaderboards and giving achievable progress feedback: https://en.wikipedia.org/wiki/Self-determination_theory
- A gamification/flow systematic review found effects vary by context, which is why the feature is implemented as testable, local logic rather than a heavy reward economy: https://arxiv.org/abs/2106.09942

Key files:
- `app/src/main/java/com/cmp/pushuptracker/utils/MomentumQuest.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/MomentumQuestSection.kt`
- `app/src/test/java/com/cmp/pushuptracker/MomentumQuestTest.kt`

## Startup Flow

1. `MainApplication.onCreate()` initializes Firebase.
2. Crashlytics collection is disabled for debuggable builds.
3. The notification channel is created.
4. Daily reminder workers are scheduled for morning and evening.
5. `MainActivity.onCreate()` records an app launch for review-prompt eligibility.
6. Remote Config is fetched and evaluated for force-update requirements.
7. The saved theme is loaded through `UtilViewmodel`.
8. Onboarding completion is read from `PreferenceUtil`.
9. The UI routes either to onboarding or the main app navigation.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/MainApplication.kt`
- `app/src/main/java/com/cmp/pushuptracker/MainActivity.kt`
- `app/src/main/java/com/cmp/pushuptracker/utils/PreferenceUtil.kt`
- `app/src/main/java/com/cmp/pushuptracker/viewmodel/UtilViewmodel.kt`

## Onboarding Flow

1. `OnBoardingNavigation` starts at `OnBoardingScreen1`.
2. Screen 1 explains app capabilities and continues to screen 2.
3. Screen 2 explains progress tracking and asks for user weight.
4. On completion, `UserViewmodel.addUser()` creates the initial `UserEntity`.
5. `PreferenceUtil.completeOnboarding()` marks onboarding complete.
6. Navigation moves into the main `Home` route.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/onBoarding/OnBoardingNavigation.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/onBoarding/OnBoardingScreen1.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/onBoarding/OnBoardingScreen2.kt`
- `app/src/main/java/com/cmp/pushuptracker/viewmodel/UserViewmodel.kt`

## Main Navigation Flow

`PushUpAppNavigation` owns the primary app routes:

- `Home`: Momentum Quest, daily stats, quick add, weekly stats, and calendar.
- `Statistics`: heatmap and progress charts.
- `Profile`: stats, permissions, theme selection, privacy, and feature links.
- `Start Workout`: sets, reps, and rest interval setup.
- `Live Preview`: CameraX + ML Kit push-up counting session.

The bottom navigation is hidden on the live preview route.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/MainActivity.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/navigationUtils/Screen.kt`

## Data Flow

The app stores local data in Room:

- `pushup_table`: one record per date.
- `user_table`: user summary/profile values.

`PushupRepository` exposes all sessions and date-specific lookups as flows. `PushupViewModel` converts these flows to `StateFlow` for Compose screens.

`UserRepository` exposes user data. `UserViewmodel` keeps the first user row in Compose state.

Important behavior:
- Push-up dates are stored as ISO `yyyy-MM-dd` strings.
- Legacy `dd/MM/yyyy` strings are normalized through `TimeUtils.toStorageDate()`.
- Database version 4 migrates old date keys from `dd/MM/yyyy` to `yyyy-MM-dd`.
- Session records are ordered by ISO date strings.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/database/module/AppDatabase.kt`
- `app/src/main/java/com/cmp/pushuptracker/database/module/DatabaseModule.kt`
- `app/src/main/java/com/cmp/pushuptracker/database/entity/PushUpEntity.kt`
- `app/src/main/java/com/cmp/pushuptracker/database/entity/UserEntity.kt`
- `app/src/main/java/com/cmp/pushuptracker/database/dao/PushUpDao.kt`
- `app/src/main/java/com/cmp/pushuptracker/database/dao/UserDao.kt`
- `app/src/main/java/com/cmp/pushuptracker/database/repository/PushupRepository.kt`
- `app/src/main/java/com/cmp/pushuptracker/database/repository/UserRepository.kt`
- `app/src/main/java/com/cmp/pushuptracker/viewmodel/PushUpViewmodel.kt`
- `app/src/main/java/com/cmp/pushuptracker/viewmodel/UserViewmodel.kt`

## Home And Quick Add Flow

1. `HomeScreen` observes today's push-up record from `PushupViewModel.todayData`.
2. It displays reps, duration, estimated calories, weekly stats, calendar, and Momentum Quest.
3. The app may show notification permission and Play Review prompts from this screen.
4. The floating action button opens quick add or routes to workout setup.
5. Quick add collects reps, sets, duration, and date.
6. `PushupUtils.addPushupInDb()` merges the new entry with any existing session for that date and updates user totals.
7. User totals are incremented only by the newly added reps and duration.

## Momentum Quest Flow

1. `MomentumQuest.build()` normalizes session dates and groups reps by day.
2. It derives the current Sunday-to-Saturday week.
3. It calculates an adaptive daily target from active days in the previous 14 days.
4. It tracks weekly quest progress toward 5 active days.
5. It gives 2 rest shields per week, consumed by missed past days.
6. It calculates the current streak from today or yesterday.
7. `MomentumQuestSection` renders the card on Home with today progress, weekly dots, streak, shields, and a Start button.
8. The Start button opens the existing workout setup route.

Quest files:
- `app/src/main/java/com/cmp/pushuptracker/utils/MomentumQuest.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/MomentumQuestSection.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/GetHomeSection.kt`
- `app/src/test/java/com/cmp/pushuptracker/MomentumQuestTest.kt`

Home files:
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/HomeScreen.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/GetHomeSection.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/GetWeeklyGoalsSection.kt`
- `app/src/main/java/com/cmp/pushuptracker/utils/pushUpUtil.kt`
- `app/src/main/java/com/cmp/pushuptracker/utils/ReviewPromptManager.kt`

## Workout Setup Flow

1. `StartWorkoutScreen` collects target sets, reps per set, and rest interval.
2. It checks camera permission before entering live preview.
3. It stores the selected workout values in `LivePreviewViewmodel`.
4. It navigates to `LivePreviewScreen`.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/StartWorkoutScreen.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/pushupPreviewScreen/viewmodel/LivePreviewViewmodel.kt`

## Push-Up Counting Flow

1. `PushUpScreen` creates a remembered `PushUpCounter`.
2. `CameraPreview` binds the front camera and an `ImageAnalysis` analyzer.
3. Each frame is passed to `processImage()`.
4. ML Kit Pose Detection extracts body landmarks.
5. During initial mode, the counter checks nose visibility for setup guidance.
6. During workout mode, the counter uses elbow angle to detect down and up phases.
7. A rep is counted when the user transitions from bent arms to extended arms.
8. The counter debounces counted reps to reduce duplicate counts from frame jitter.
9. When the target reps for a set are reached, the app moves to interval mode or completion.
10. On completion, the workout is saved through `PushupUtils.addPushupInDb()`.
11. If the user stops early, counted reps are saved as a partial workout.
12. Debug builds show phase, arm angle, and body-line angle over the camera preview.

Current counter signals:
- Arm angle: shoulder, elbow, wrist.
- Body line warning: shoulder, hip, knee.
- Nose visibility: setup/user guidance only.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/pushupPreviewScreen/ui/PushUpScreen.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/pushupPreviewScreen/ui/PushupBottomSection.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/pushupPreviewScreen/viewmodel/LivePreviewViewmodel.kt`
- `app/src/main/java/com/cmp/pushuptracker/camera/CameraPreview.kt`
- `app/src/main/java/com/cmp/pushuptracker/camera/PushUpCounter.kt`
- `app/src/main/java/com/cmp/pushuptracker/camera/PushUpCounterEngine.kt`
- `app/src/main/java/com/cmp/pushuptracker/utils/pushUpUtil.kt`

## Statistics Flow

1. `HistoryScreen` observes all push-up sessions.
2. It maps records by date.
3. The screen renders heatmap and chart components.
4. Selecting a heatmap day opens an info bottom sheet for that session.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/history/HistoryScreen.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/history/GetHeatMap.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/history/GetRepsChart.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/history/GetRepsPerMinChart.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/history/GetDurationChart.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/components/InfoBottomSheet.kt`

## Profile And Theme Flow

1. `ProfileNavigation` owns nested profile routes.
2. `ProfileScreen` displays user stats, permission info, theme entry, and external links.
3. `ThemeScreen` updates `UtilViewmodel`.
4. `UtilViewmodel` saves the selected theme through `ThemePreferences`.
5. `PushupTrackerTheme` applies the selected Material 3 color scheme.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/profileScreen/ProfileScreen.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/profileScreen/ThemeScreen.kt`
- `app/src/main/java/com/cmp/pushuptracker/utils/ThemePreferences.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/theme/Theme.kt`

## Reminder Notification Flow

1. `MainApplication` schedules morning and evening periodic work.
2. Reminder settings in Profile control whether morning/evening reminders are enabled and what time each reminder should run.
3. `DailyReminderScheduler` cancels disabled reminders and schedules enabled reminders for the saved times.
4. `DailyReminderWorker` checks today's push-up session.
5. If no reps were logged today, `NotificationHelper.showReminder()` posts a reminder.
6. On Android 13+, Home asks for notification permission before reminders can show.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/notifications/DailyReminderScheduler.kt`
- `app/src/main/java/com/cmp/pushuptracker/notifications/DailyReminderWorker.kt`
- `app/src/main/java/com/cmp/pushuptracker/notifications/NotificationHelper.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/home/HomeScreen.kt`
- `app/src/main/java/com/cmp/pushuptracker/ui/screen/profileScreen/GetReminderSection.kt`

## Analytics And Force Update Flow

Analytics:
1. Main and nested navigation observe current routes.
2. Route names are mapped through `analyticsNameForRoute()`.
3. `AnalyticsLogger.logScreenView()` sends Firebase screen-view events.

Force update:
1. `MainActivity` fetches Firebase Remote Config.
2. It compares `BuildConfig.VERSION_CODE` against `min_supported_version_code`.
3. If the installed version is unsupported, it shows a force-update dialog.
4. Blocking updates cannot be dismissed; optional updates can be dismissed.

Key files:
- `app/src/main/java/com/cmp/pushuptracker/analytics/AnalyticsLogger.kt`
- `app/src/main/java/com/cmp/pushuptracker/analytics/AnalyticsScreenMapper.kt`
- `app/src/main/java/com/cmp/pushuptracker/MainActivity.kt`

## Known Flow Risks

- Room schema export is not configured, so migration history is harder to inspect.
- Tests cover core date normalization, weekly aggregation, workout merging, and counter state transitions, but not full Compose flows or real camera input.
- Push-up counting thresholds may still need real-device tuning across body types, camera positions, and lighting.
- `pushup_table` still uses a string primary key; ISO storage makes it sortable, but an epoch-day integer would be stricter.
