# أنمي بلاك — دليل مشروع الأندرويد (Kotlin & Jetpack Compose)

تم تحويل منصة **أنمي بلاك** إلى تطبيق أندرويد نيتف كامل (100% Native Android) مبني بأحدث معايير Google الهندسية:
- **لغة البرمجة:** Kotlin 2.0
- **واجهة المستخدم:** Jetpack Compose + Material 3
- **مشغل الفيديو:** Google Media3 ExoPlayer (دعم كامل لـ HLS `.m3u8` و MP4، تبديل الجودات، التحكم بالإيماءات، وملء الشاشة الأفقي)
- **قاعدة البيانات والسحابة:** Firebase Android SDK (Firestore, Auth, Storage, FCM)
- **البناء التلقائي:** GitHub Actions Workflow لتصدير حزم الـ APK جاهزة للتحميل والتثبيت

---

## 🚀 كيفية بناء ملفات APK عبر GitHub Actions (تلقائياً)

تم تجهيز سير عمل آلي داخل المستودع في المسار:
`.github/workflows/build-apk.yml`

### طريقة التشغيل التلقائي:
1. عند عمل **Push** لأي كود جديد إلى فرع `main` أو `master`، يبدأ البناء تلقائياً.
2. للتشغيل اليدوي بضغطة زر:
   - اذهب إلى مستودع المشروع في **GitHub**.
   - افتح تبويب **Actions**.
   - اختر سير العمل **Build Android APK (Anime Black)** من القائمة الجانبية.
   - اضغط على زر **Run workflow**.
3. بعد اكتمال البناء (عادة خلال 2-3 دقائق)، ستجد ملف الـ APK جاهزاً للتحميل المباشر في قسم **Artifacts**:
   - `animeblack-debug-apk` (جاهز للتثبيت الفوري على أي هاتف بدون توقيع).
   - `animeblack-release-apk` (نسخة الإنتاج المحسنة بواسطة ProGuard).

---

## 💻 كيفية بناء التطبيق محلياً في جهازك (CLI أو Android Studio)

### المتطلبات الأساسية:
- **JDK 17** مثبت ومضبوط في متغيرات البيئة.
- **Android SDK** (API 34).

### 1. بناء حزمة Debug APK:
```bash
cd android
./gradlew assembleDebug
```
المسار الناتج للملف:
`android/app/build/outputs/apk/debug/app-debug.apk`

### 2. تثبيت التطبيق مباشرة على الهاتف المتصل بالكمبيوتر عبر USB:
```bash
adb install -r android/app/build/outputs/apk/debug/app-debug.apk
```

### 3. بناء حزمة Release APK:
```bash
cd android
./gradlew assembleRelease
```
المسار الناتج للملف:
`android/app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 📁 هيكلية مجلدات المشروع

- `android/app/src/main/kotlin/com/animeblack/app/`:
  - `AnimeBlackApp.kt`: فئة التطبيق الرئيسية وتهيئة Firebase Firestore مع الكاش المحلي.
  - `MainActivity.kt`: النشاط الرئيسي وإدارة التنقل عبر Compose Navigation وشريط القوائم السفلي.
  - `player/AnimePlayerView.kt`: مشغل الفيديو المتطور بـ ExoPlayer مع التحكم بالإيماءات وتغيير الجودة.
  - `data/model/Models.kt`: نماذج البيانات (الأنمي، الحلقات، المنشورات، المستخدمين، الغرف، الرسائل).
  - `data/repository/FirebaseRepository.kt`: مستودع البيانات السحابية وقنوات المزامنة اللحظية (Flow).
  - `ui/screens/home/HomeScreen.kt`: الشاشة الرئيسية، الأنميات الرائجة، وفلاتر التصنيف.
  - `ui/screens/player/PlayerScreen.kt`: شاشة تشغيل الحلقة مع قائمة الحلقات والسيرفرات.
  - `ui/screens/community/CommunityScreen.kt`: حائط المنشورات وقصص الأوتاكو والتفاعلات.
  - `ui/screens/chat/ChatScreen.kt`: غرف الدردشة الجماعية الفورية.
  - `ui/screens/profile/ProfileScreen.kt`: شاشة الحساب، المستوى، الرتب، ومحفظة العملات والنجوم.
