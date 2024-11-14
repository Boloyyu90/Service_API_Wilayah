# Dokumentasi Proyek Quarkus - Wilayah
Proyek ini merupakan aplikasi berbasis Quarkus yang mengelola data wilayah administratif di Indonesia. Aplikasi ini menyediakan fungsionalitas untuk mengelola data provinsi, kabupaten, kecamatan, dan desa, dengan fitur pencarian berdasarkan ID provinsi, serta kemampuan untuk mengimpor data dari file CSV.

<h3>Fitur Utama :</h3>
1. CRUD untuk Wilayah
- Menyediakan API untuk mengambil data wilayah seperti provinsi, kabupaten, kecamatan, dan desa berdasarkan ID provinsi.
- Menyediakan API untuk mengimpor data wilayah dari file CSV dan melakukan upsert (insert/update) secara batch.

2. Batch Processing
- Menggunakan batch processing untuk efisiensi dalam mengimpor dan memperbarui data wilayah dari CSV.

3. API Endpoint
- Menggunakan REST API untuk mengakses data wilayah, dengan validasi input dan pengelolaan respons yang sesuai (error handling).

<h3>Struktur Proyek</h3>
1. VillageRepository
- Deskripsi: Menyediakan akses ke database untuk mengambil dan memperbarui data desa.
- Metode Utama:
  - findByProvinceId(String provinceId) - Mengambil daftar desa berdasarkan ID provinsi.
  - bulkUpsert(List<Village> villages) - Melakukan upsert data desa secara batch.

2. VillageService
- Deskripsi: Berfungsi sebagai layanan yang mengelola logika bisnis untuk desa.
- Metode Utama:
  - getVillagesByProvinceId(String provinceId) - Mengambil desa berdasarkan ID provinsi.
  - importFromCsvWithUpsert() - Mengimpor data desa dari file CSV dan melakukan upsert.

3. VillageController
- Deskripsi: Menyediakan API untuk mengakses data desa.
- Endpoint:
  - GET /villages/{provinceId} - Mengambil data desa berdasarkan ID provinsi.
  - POST /villages/import - Mengimpor data desa dari file CSV.

1. DistrictRepository
- Deskripsi: Menyediakan akses ke database untuk mengambil dan memperbarui data kecamatan.
- Metode Utama:
  - findByProvinceId(String provinceId) - Mengambil daftar kecamatan berdasarkan ID provinsi.
  - bulkUpsert(List<District> districts) - Melakukan upsert data kecamatan secara batch.
2. DistrictService
- Deskripsi: Berfungsi sebagai layanan untuk mengelola logika bisnis untuk kecamatan.
- Metode Utama:
  - findByProvinceId(String provinceId) - Mengambil kecamatan berdasarkan ID provinsi.
  -importFromCsvWithUpsert() - Mengimpor data kecamatan dari file CSV dan melakukan upsert.
3. DistrictController
- Deskripsi: Menyediakan API untuk mengakses data kecamatan.
- Endpoint:
  - GET /districts/{provinceId} - Mengambil data kecamatan berdasarkan ID provinsi.
  - POST /districts/import - Mengimpor data kecamatan dari file CSV.

1. RegencyRepository
- Deskripsi: Menyediakan akses ke database untuk mengambil dan memperbarui data kabupaten.
- Metode Utama:
  - findAll() - Mengambil semua data kabupaten.
  - findByProvinceId(String provinceId) - Mengambil kabupaten berdasarkan ID provinsi.
  - bulkUpsert(List<Regency> regencies) - Melakukan upsert data kabupaten secara batch.
2. RegencyService
- Deskripsi: Berfungsi sebagai layanan untuk mengelola logika bisnis untuk kabupaten.
- Metode Utama:
  - getAllRegencies() - Mengambil semua data kabupaten.
  - getRegenciesByProvinceId(String provinceId) - Mengambil kabupaten berdasarkan ID provinsi.
  - importFromCsvWithUpsert() - Mengimpor data kabupaten dari file CSV dan melakukan upsert.
3. RegencyController
- Deskripsi: Menyediakan API untuk mengakses data kabupaten.
- Endpoint:
  - GET /regencies/{provinceId} - Mengambil data kabupaten berdasarkan ID provinsi.
  - POST /regencies/import - Mengimpor data kabupaten dari file CSV.

1. ProvinceRepository
- Deskripsi: Menyediakan akses ke database untuk mengambil dan memperbarui data provinsi.
- Metode Utama:
  - findAll() - Mengambil semua data provinsi.
  - findById(String id) - Mengambil provinsi berdasarkan ID.
  - bulkUpsert(List<Province> provinces) - Melakukan upsert data provinsi secara batch.
2. ProvinceService
- Deskripsi: Berfungsi sebagai layanan untuk mengelola logika bisnis untuk provinsi.
- Metode Utama:
  - getAllProvinces() - Mengambil semua data provinsi.
  - getProvinceById(String id) - Mengambil provinsi berdasarkan ID.
  - importFromCsvWithUpsert() - Mengimpor data provinsi dari file CSV dan melakukan upsert.
3. ProvinceController
- Deskripsi: Menyediakan API untuk mengakses data provinsi.
- Endpoint:
  - GET /provinces/{id} - Mengambil data provinsi berdasarkan ID.
  - POST /provinces/import - Mengimpor data provinsi dari file CSV.

<h3>Penggunaan</h3>

1. Mengakses API
Setelah aplikasi berjalan, Anda dapat mengakses endpoint API berikut:
GET /villages/{provinceId}: Ambil data desa berdasarkan ID provinsi.
POST /villages/import: Mengimpor data desa dari CSV.
GET /districts/{provinceId}: Ambil data kecamatan berdasarkan ID provinsi.
POST /districts/import: Mengimpor data kecamatan dari CSV.
GET /regencies/{provinceId}: Ambil data kabupaten berdasarkan ID provinsi.
POST /regencies/import: Mengimpor data kabupaten dari CSV.
GET /provinces/{id}: Ambil data provinsi berdasarkan ID.
POST /provinces/import: Mengimpor data provinsi dari CSV.
File CSV

2. Pastikan file CSV untuk desa, kecamatan, kabupaten, dan provinsi berada di direktori src/main/resources/file-data-wilayah/ dengan nama file yang sesuai:
villages.csv
districts.csv
regencies.csv
provinces.csv

Link Video Demo :
https://drive.google.com/file/d/1ODgVR9fZVrvRGUzT9jjUAcY51AsrilAJ/view?usp=sharing
