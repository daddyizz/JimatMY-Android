# Draf Jawapan Play Console — JimatMY

Dokumen ini ialah panduan berdasarkan build Android JimatMY versi 1.2.0. Semak semula jika fungsi, SDK atau cara data dikendalikan berubah sebelum submission.

## App content

- **Contains ads:** Yes.
- **Target audience:** 18 and over. JimatMY bukan aplikasi yang direka khusus untuk kanak-kanak.
- **App access:** Semua fungsi pengguna boleh digunakan tanpa login. Dashboard Admin ialah fungsi pengurusan dalaman dan bukan fungsi pengguna biasa.
- **Financial features:** None. JimatMY tidak memproses pembayaran, pinjaman, dompet digital atau pemindahan wang.
- **Category:** Shopping.
- **Privacy policy:** `https://jimatmy.lovable.app/privacy`
- **Developer website:** `https://jimatmy.lovable.app/`
- **Contains user-generated content:** No. Laporan harga dihantar secara peribadi kepada Admin dan tidak diterbitkan kepada pengguna lain.

## Data Safety — semakan awal

Jawab **Yes** apabila ditanya sama ada aplikasi mengumpul atau berkongsi data. SDK Google Mobile Ads 25.4.0 secara automatik memproses beberapa kategori data untuk pengiklanan, analitik dan pencegahan fraud.

### Google Mobile Ads

Semak dan isytiharkan kategori berikut berdasarkan panduan rasmi SDK:

- **Approximate location:** alamat IP boleh digunakan untuk menganggar lokasi umum.
- **App interactions:** pelancaran aplikasi, tap dan interaksi iklan.
- **Diagnostics:** prestasi aplikasi/SDK, masa pelancaran, hang dan penggunaan tenaga.
- **Device or other IDs:** Advertising ID, App Set ID dan pengecam berkaitan.
- **Purposes:** Advertising or marketing, Analytics, Fraud prevention, security and compliance.
- **Encrypted in transit:** Yes.

Rujukan: `https://developers.google.com/admob/android/privacy/play-data-disclosure`

### Website dalam WebView

- Google Analytics mengukur halaman yang dilawati, interaksi, maklumat peranti/pelayar, diagnostik dan anggaran lokasi berdasarkan IP.
- Supabase menerima permintaan rangkaian untuk memaparkan produk dan menghantar laporan harga.
- Laporan harga boleh mengandungi URL halaman, sebab laporan, catatan pilihan dan masa penghantaran.
- Saved Deals disimpan secara setempat pada peranti menggunakan local storage.

Google Play menganggap pengumpulan melalui WebView yang dikawal oleh aplikasi sebagai sebahagian daripada pengumpulan data aplikasi. Pastikan jawapan akhir sepadan dengan konfigurasi GA4, Supabase dan borang semasa Play Console.

Rujukan: `https://support.google.com/googleplay/android-developer/answer/10787469`

## Advertising ID

Google Mobile Ads boleh menambah kebenaran Advertising ID melalui manifest gabungan. Jangan menanda aplikasi sebagai aplikasi kanak-kanak. Semak manifest akhir daripada App Bundle Explorer selepas upload.

## Closed testing

Jika Play Console meminta Production access untuk akaun Personal baharu, jalankan Closed testing dengan jumlah tester dan tempoh yang dipaparkan oleh akaun. Pastikan tester kekal opted-in dan benar-benar membuka serta menguji aplikasi sepanjang tempoh tersebut.

## Maklumat untuk reviewer

Cadangan nota:

> JimatMY ialah utiliti membeli-belah untuk membandingkan maklumat produk, menggunakan kalkulator harga, menyimpan deal pada peranti dan melaporkan harga yang sudah berubah. Semua fungsi pengguna boleh digunakan tanpa login. Pautan marketplace dibuka menggunakan aplikasi atau browser luaran. Dashboard Admin ialah alat pengurusan dalaman dan bukan sebahagian daripada pengalaman pengguna biasa.

## Sebelum tekan Submit

- Pastikan Privacy Policy live sudah menyatakan bahawa AdMob dan GA4 aktif.
- Pastikan `https://jimatmy.lovable.app/app-ads.txt` memberi HTTP 200.
- Ambil sekurang-kurangnya dua screenshot bersih daripada release candidate.
- Upload `.aab` ke Internal testing dan selesaikan Pre-launch report.
- Semak tiada crash, ANR, pautan contoh atau data sensitif.
- Jangan klik iklan sendiri ketika menguji build production.
