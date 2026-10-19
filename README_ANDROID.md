# أنمي بلاك (Anime Black) — دليل تطبيق الأندرويد الأصلي ونظام التصميم الاحترافي

تم بناء وتحديث تطبيق **Anime Black Android** الأصلي (Native Android) بالكامل باستخدام أحدث تقنيات أندرويد الرسمية:
- **لغة البرمجة:** Kotlin 2.0 (Native)
- **واجهة المستخدم:** Jetpack Compose + Material Design 3
- **إدارة الحزم والتبعيات:** Gradle Version Catalog (`libs.versions.toml`) + Gradle 8.5
- **الهوية البصرية الرسمية:** سواد AMOLED فائق العمق (`#050609`)، أسطح فحمية (`#10121D`, `#181B2B`)، مع التوهج الأزرق الأساسي Cyber Blue (`#00A3FF`) والبنفسجي الكهربائي (`#8A2BE2`).
- **نظام الخطوط العربية المدمجة محلياً:** خطوط **Cairo** (للعناوين والترويسات) و **Tajawal** (للنصوص والمحتوى) مدمجة بدون الحاجة للإنترنت داخل مجلد `res/font/` وفق ترخيص الخطوط المفتوح (OFL).
- **أيقونات التطبيق التكيفية (Adaptive Icons):** أصول متجهة وتكيفية كاملة في `res/mipmap` و `res/drawable` مع خلفية ورمز AMOLED Cyber Blue لتوافق كافة قياسات الشاشات وأنظمة Android 8.0 إلى Android 14+.
- **مشغل الفيديو:** Google Media3 ExoPlayer مع دعم HLS (`.m3u8`) و MP4 ونظام الكاش التلقائي (`AnimeCacheManager`) مع `SimpleCache` للمشاهدة بدون إنترنت.
- **التخزين السحابي والمزامنة:** Firebase Android SDK الرسمي (Firestore, Auth, Storage, FCM Cloud Messaging) متصل مع السيرفر السحابي.

---

## 🛠️ تفاصيل حل أخطاء البناء في GitHub Actions كلياً وجذرياً

تم فحص سجلات وأسباب فشل البناء على GitHub ومعالجتها جذرياً في مسارات CI/CD:

1. **إصلاح أخطاء تجميع Kotlin وتعارض الرموز:**
   - تصحيح جميع استيرادات نظام التصميم في `MainActivity.kt` و `AnimeStateView.kt` و `ReelsScreen.kt`.
   - استبدال الرموز القديمة بالرموز الرسمية (`CyberBlue`, `AmoledBlack`, `Dimensions`, `Brush`, `FontWeight`, `sp`).

2. **إصلاح نقص موارد الأيقونات (AAPT2 Missing Resources):**
   - تم إنشاء وتوليد كافة موارد الأيقونات التكيفية (`ic_launcher.xml`, `ic_launcher_round.xml`, `ic_launcher_background.xml`, `ic_launcher_foreground.xml`) وخرائط البتات لجميع الكثافات (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`)، مما يمنع تعطل معالج AAPT2.

3. **إصلاح أخطاء حزمة Gradle Wrapper وإجراءات CI:**
   - توفير ملف `gradle-wrapper.jar` الأصلي بحجم 43KB والتحقق من سلامته بواسطة تجزئة Zip.
   - تحديث مسار `.github/workflows/build-apk.yml` بإضافة التحقق التلقائي وآلية التراجع المزدوجة `gradlew || gradle` مع تهيئة حزمة `gradle/actions/setup-gradle@v3`.

4. **إصلاح خطأ `npm ci` و Cloud Functions في سير العمل `ci.yml`:**
   - إنشاء ومزامنة ملفات `package-lock.json` في المستودع الرئيسي ومجلد `functions/`.
   - ضبط أمر التثبيت التكيفي `npm ci || npm install` لضمان عدم توقف الفحص الآلي.

5. **تأمين بناء Release دون توقف عند غياب Keystore السري:**
   - ضبط `signingConfigs.findByName` في `app/build.gradle.kts` للتعامل الآمن مع المتغيرات البيئية، بحيث يقوم ببناء وتوقيع حزمة الـ APK تلقائياً.

---

## 🚀 كيفية بناء وتحميل الـ APK من GitHub Actions

1. يتم تفعيل البناء تلقائياً بمجرد عمل **Push** لأي كود في المستودع.
2. للتشغيل اليدوي المباشر:
   - افتح المستودع على **GitHub**.
   - انتقل إلى تبويب **Actions**.
   - اختر **Build Android APK (Anime Black)** ثم اضغط على **Run workflow**.
3. بعد اكتمال البناء، ستجد ملفات الـ APK في قسم **Artifacts**:
   - `animeblack-debug-apk` (جاهز للتثبيت والتشغيل المباشر).
   - `animeblack-release-apk` (نسخة الإصدار النهائية).

---

## 💻 أوامر البناء المحلي عبر الطرفية (Terminal):

```bash
cd android
chmod +x gradlew
./gradlew assembleDebug    # بناء حزمة التجربة Debug APK
./gradlew assembleRelease  # بناء حزمة الإصدار Release APK
```
