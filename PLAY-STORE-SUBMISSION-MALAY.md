# Checklist Google Play — JimatMY

## 1. Ujian teknikal

- [ ] Gradle Sync tanpa ralat.
- [ ] Debug build dipasang pada telefon sebenar.
- [ ] Semua halaman pengguna boleh dibuka.
- [ ] Pautan Shopee dibuka di luar aplikasi.
- [ ] Upload gambar Admin berfungsi.
- [ ] Banner ujian muncul pada halaman pengguna.
- [ ] Banner tidak muncul pada `/admin`.
- [ ] Pull-to-refresh berfungsi.
- [ ] Skrin offline dan Cuba Lagi berfungsi.
- [ ] Native Share dan Menu berfungsi.
- [ ] UMP diuji menggunakan tetapan debug/test geography sebelum release.
- [ ] Pre-launch report Play Console tiada crash atau ANR kritikal.

## 2. Website dan domain

- [ ] Privacy Policy telah dikemas kini untuk aplikasi, AdMob, Analytics dan Supabase.
- [ ] `https://DOMAIN/app-ads.txt` memaparkan satu baris publisher JimatMY.
- [ ] `https://DOMAIN/.well-known/assetlinks.json` mempunyai SHA-256 Play App Signing.
- [ ] Developer website dalam Play Console menggunakan domain yang sama dengan `app-ads.txt`.
- [ ] Watermark/branding pihak pembina diselesaikan sebelum screenshot akhir jika boleh.

## 3. AdMob

- [ ] App ID dan Banner ID release disahkan dalam AdMob.
- [ ] Privacy & messaging telah mempunyai mesej European regulations.
- [ ] Jangan klik iklan sebenar pada peranti sendiri.
- [ ] Selepas Play listing wujud, sambungkan aplikasi AdMob kepada listing tersebut.

## 4. Store listing

- [ ] Nama: JimatMY.
- [ ] Ikon 512 × 512 PNG.
- [ ] Feature graphic 1024 × 500 JPG/PNG.
- [ ] Minimum dua screenshot telefon yang bersih.
- [ ] Short description maksimum 80 aksara.
- [ ] Full description menerangkan alat perbandingan, laporan harga, Saved Deals dan panduan.
- [ ] E-mel developer: `dady.izz85@gmail.com`.
- [ ] URL Privacy Policy dimasukkan.
- [ ] Contains ads: **Yes**.
- [ ] Target audience dipilih dengan tepat; jangan tandakan sebagai aplikasi kanak-kanak.
- [ ] Content rating dan kategori aplikasi dilengkapkan.
- [ ] App access menerangkan bahawa dashboard Admin bukan fungsi pengguna biasa.

## 5. Data Safety — semak dengan teliti dalam Play Console

SDK AdMob dan Analytics boleh memproses data seperti app interactions, diagnostics, device/advertising identifiers dan approximate location. Supabase boleh memproses laporan yang dihantar pengguna. Jawapan akhir mesti dibandingkan dengan borang semasa Play Console dan dokumentasi setiap SDK; jangan menanda **No data collected** secara automatik.

- [ ] Nyatakan bahawa data dihantar menggunakan HTTPS.
- [ ] Semak kategori data AdMob melalui halaman Google Play data disclosure rasmi.
- [ ] Semak data Analytics dan Supabase berdasarkan konfigurasi sebenar.
- [ ] Terangkan cara pengguna meminta pemadaman data.

## 6. Signing dan release

- [ ] Cipta/simpan upload keystore dan password secara peribadi.
- [ ] Aktifkan Play App Signing.
- [ ] Bina Android App Bundle `release` (`.aab`).
- [ ] Upload dahulu ke Internal testing.
- [ ] Jalankan Closed testing jika Play Console memerlukannya.
- [ ] Simpan SHA-256 Play App Signing untuk Digital Asset Links.

## 7. Risiko semakan WebView dan affiliate

Store listing hendaklah menerangkan nilai utiliti JimatMY—perbandingan, alat kiraan, Saved Deals, laporan harga dan panduan. Jangan menggambarkan aplikasi sebagai koleksi pautan affiliate sahaja. Pastikan fungsi native v3 boleh digunakan dan semua pautan affiliate dilabel dengan telus.
