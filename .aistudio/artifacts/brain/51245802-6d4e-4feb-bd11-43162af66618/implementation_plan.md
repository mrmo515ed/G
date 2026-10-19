# خطة الإصلاح الجذري لبناء GitHub Actions وتنفيذ نظام التصميم الاحترافي لتطبيق Anime Black Android

## 1. تحليل أسباب فشل البناء على GitHub Actions والحلول الجذرية

من خلال فحص مستودع المشروع وملفات التكوين، تم تحديد الأسباب المباشرة لفشل البناء:
1. **غياب ملف `gradle-wrapper.jar`:**
   - مجلد `android/gradle/wrapper/` يحتوي على `gradle-wrapper.properties` بينما ملف الـ binary `gradle-wrapper.jar` غير متوفر، مما يجعل تشغيل أمر `./gradlew` على خوادم Ubuntu الخاصة بـ GitHub Actions يفشل فوراً بخطأ:
     `Error: Could not find or load main class org.gradle.wrapper.GradleWrapperMain`.
2. **فشل توقيع الـ Release APK على السيرفر:**
   - ملف `android/app/build.gradle.kts` كان يحاول تكوين `signingConfigs.release` دون اشتراط وجود متغير البيئة الفعلي للـ Keystore، مما يتسبب في توقف `./gradlew assembleRelease` بخطأ: `Keystore file not set for signing config release`.
3. **اعتماد سكريبت GitHub Actions على `gradlew` المحلي فقط دون إسناد تلقائي:**
   - سيتضمن الحل تحديث `.github/workflows/build-apk.yml` باستخدام إجراء جيت هاب الرسمي المعتمد `gradle/actions/setup-gradle@v3` وتوليد الـ wrapper jar رسمياً، مع إتاحة خيار بناء الـ APK بـ Gradle مثبت سحابياً في حال غياب الـ jar.

---

## 2. نظام التصميم التنفيذي الموحد (Design System Architecture)

تطبيق المعايير الإلزامية في البرومبت التنفيذي لهوية Anime Black:

### أ. لوحة الألوان المركزية (Color Palette):
- **الخلفية الأساسية (AMOLED Black):** `#050609` (سواد عميق موفر للطاقة وشاشات OLED).
- **الأسطح الثانوية (Dark Charcoal & Slate):** `#12141F` و `#1A1D2E`.
- **اللون الأساسي المميز (Cyber Blue):** `#00A3FF` (وفق نص البند 4 في البرومبت التنفيذي).
- **اللون الثانوي (Electric Purple):** `#8A2BE2` و `#9D4EDD`.
- **ألوان الحالات (Semantic Statuses):**
  - النجاح: `#10B981` (Emerald).
  - التحذير: `#F59E0B` (Amber).
  - الخطأ: `#EF4444` (Rose Red).
  - المعلومات: `#00A3FF` (Cyber Blue).

### ب. هيكل السمات (Material Design 3 Theme System):
- إنشاء ملفات التصميم المركزية:
  - `ui/theme/Color.kt`: جميع درجات الألوان وقيم التدرجات والشفافيات.
  - `ui/theme/Theme.kt`: إعداد `darkColorScheme` مع ضبط ألوان شريط الحالة والتنقل.
  - `ui/theme/Type.kt`: مقاييس الخطوط العربية (Display, Headline, Title, Body, Label) المتوافقة مع Cairo و Tajawal.
  - `ui/theme/Dimensions.kt`: توحيد أبعاد الحشو (Padding)، المسافات (Spacing 4dp..32dp)، والحد الأدنى لمساحة اللمس (Touch Target $\ge 48\text{dp}$).
  - `ui/theme/Shape.kt`: مقاييس الحواف الدائرية للبطاقات والأزرار (8dp, 12dp, 16dp, 24dp).

### ج. مكونات الحالات وحركات التحميل (Shimmer & State Components):
- مكوّن `ShimmerEffect.kt`: تأثير الوميض الحركي أثناء جلب البيانات للبوسترات والمنشورات.
- مكوّن `AnimeStateView.kt`:
  - حالة التحميل مع Shimmer.
  - حالة الخطأ مع زر إعادة المحاولة (Retry).
  - حالة الفراغ (Empty State) مع أيقونة معبرة وزر إجراء.
  - حالة انقطاع الاتصال مع إشعار التبديل للكاش الأوفلاين.

---

## 3. خطوات التنفيذ التفصيلية (Implementation Steps)

### المرحلة 1: معالجة أخطاء Gradle و GitHub Actions
1. **إصلاح `app/build.gradle.kts`:**
   - تعديل `signingConfigs` ليعتمد تلقائياً توقيع Debug في بيئات التطوير والاختبار عند عدم توفر Keystore إنتاجي في الـ Secrets، مما يضمن خروج ملفات الـ APK بنجاح تام دون توقف.
2. **تجهيز `gradle-wrapper.jar`:**
   - توليد وتوفير نسخة مطابقة وصالحة من Gradle Wrapper 8.5.
3. **تحديث سير عمل GitHub Actions (`.github/workflows/build-apk.yml`):**
   - استخدام `gradle/actions/setup-gradle@v3` لضبط بيئة Gradle السحابية وحل التبعيات.
   - التحقق من الصلاحيات وسلامة مسارات ملفات الـ APK الناتجة ورفعها كـ Artifacts بروابط مباشرة.

### المرحلة 2: تنفيذ نظام التصميم الموحد (Jetpack Compose M3)
1. إنشاء وتحديث حزمة `ui/theme/` بالألوان المعتمدة (`#00A3FF` و `#050609` ودرجات البنفسجي).
2. تطبيق مكوّن `Shimmer` المخصص لعناصر الواجهة.
3. دمج المكونات المشتركة `AnimeCard`, `AnimeButton`, `AnimeTopBar` في شاشات التطبيق:
   - `HomeScreen`: دمج الهوية البصرية وشريط البحث وشيمر التحميل.
   - `PlayerScreen`: مشغل Media3 مع الكاش ومؤشرات الجودة.
   - `CommunityScreen`: المنشورات والحالات التفاعلية.
   - `ChatScreen`: غرف الدردشة الفورية المتوافقة مع ألوان Cyber Blue.
   - `MangaScreen` و `ReelsScreen` و `StoreScreen`: تطبيق المظهر الموحد.

### المرحلة 3: التحقق والاختبار والأدلة
1. فحص الكود البرمجي عبر أدوات الفحص الثابت (`lint_applet` و `npm test`).
2. التحقق من بناء خادم الويب والمكتبات (`compile_applet`).
3. توفير تقرير تفصيلي يوضح حالة كل ملف ونظام وفق معايير القبول الإلزامية.
