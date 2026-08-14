# JimatMY Android — Play Store v3

Versi ini ialah peningkatan daripada projek AdMob v2 dan disediakan untuk ujian sebelum Google Play.

- Package ID: `com.daddyizz.jimatmy`
- Version: `1.2.0` (`versionCode 4`)
- Minimum Android: API 23
- Target/Compile SDK: API 36
- Website: `https://jimatmy.lovable.app`
- AdMob App ID release: `ca-app-pub-4110950503958596~6519592683`
- AdMob banner release: `ca-app-pub-4110950503958596/1134620240`

## Fungsi v3

- Google UMP consent diperiksa sebelum AdMob dimulakan.
- Build debug menggunakan banner ujian rasmi Google; App ID sebenar digunakan supaya mesej UMP JimatMY boleh dimuatkan.
- Banner release menggunakan ID AdMob JimatMY.
- Banner disembunyikan pada semua laluan `/admin`.
- Pull-to-refresh.
- Skrin offline dengan butang **Cuba Lagi**.
- Native **Muat semula**, **Kongsi** dan **Menu**.
- Menu mempunyai Privacy Policy, pilihan privasi iklan jika diperlukan, buka browser dan versi aplikasi.
- Upload screenshot/gambar dalam dashboard Admin kekal disokong.
- Pautan Shopee dan pautan luar dibuka dalam aplikasi/browser luar.
- Status bar dan navigation bar menggunakan safe insets.
- Tekan kawasan logo JimatMY 5 kali dengan cepat untuk membuka dashboard Admin.

## Uji dalam Android Studio

1. Extract ZIP dan buka folder ini dalam Android Studio.
2. Tunggu Gradle Sync selesai.
3. Pilih build variant `debug` dan tekan **Run**.
4. Pastikan banner mempunyai label iklan ujian.
5. Uji dengan Internet hidup, kemudian matikan Internet dan tekan **Muat semula**.
6. Uji pull-to-refresh, Kongsi, Menu, Privacy Policy dan pautan Shopee.
7. Tekan logo 5 kali, login Admin dan pastikan banner hilang.

## AdMob Privacy & Messaging

Di dashboard AdMob, buka **Privacy & messaging** dan cipta mesej untuk European regulations serta kawasan lain yang diperlukan. Kod UMP dalam aplikasi akan memaparkan mesej yang aktif untuk App ID JimatMY.

## Sebelum release

1. Salin `website-ready/public/app-ads.txt` ke folder `public` website dan publish.
2. Kemas kini Privacy Policy menggunakan `website-ready/PRIVACY-POLICY-UPDATE-MALAY.md`.
3. Selepas aplikasi dibuat dalam Play Console, dapatkan SHA-256 Play App Signing.
4. Gantikan placeholder dalam `website-ready/public/.well-known/assetlinks.json` dan publish.
5. Lengkapkan checklist dalam `PLAY-STORE-SUBMISSION-MALAY.md`.
6. Android Studio → **Build → Generate Signed Bundle / APK → Android App Bundle**.

Jangan klik iklan sebenar sendiri. Ujian harian hendaklah menggunakan build `debug`.
