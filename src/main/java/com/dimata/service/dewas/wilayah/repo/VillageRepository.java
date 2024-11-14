package com.dimata.service.dewas.wilayah.repo;

import com.dimata.service.dewas.wilayah.model.Village;
import com.dimata.service.dewas.wilayah.orm.core.JooqRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class VillageRepository extends JooqRepository {

    /**
     * Mengambil semua data desa tanpa filter.
     * @return List berisi semua data desa.
     */
    public List<Village> findAll() {
        var villagesTable = org.jooq.impl.DSL.table("villages");
        var villageIdField = org.jooq.impl.DSL.field("id", String.class);
        var districtIdField = org.jooq.impl.DSL.field("district_id", String.class);
        var villageNameField = org.jooq.impl.DSL.field("name", String.class);

        // Query untuk mengambil semua data desa
        var records = dsl.select(villageIdField, districtIdField, villageNameField)
                .from(villagesTable)
                .fetch();

        // Map hasil query ke objek Village
        return records.stream().map(record -> new Village(
                record.get(villageIdField),
                record.get(districtIdField),
                record.get(villageNameField)
        )).collect(Collectors.toList());
    }

    /**
     * Mengambil data desa berdasarkan ID provinsi dengan optimalisasi performa.
     * - Hanya memilih kolom yang diperlukan untuk mengurangi beban data.
     * - Pastikan index pada kolom 'id' di setiap tabel untuk mengoptimalkan join.
     *
     * @param provinceId ID provinsi yang mau dicari datanya.
     * @return List berisi data desa yang ada di provinsi tersebut.
     */
    public List<Village> findByProvinceId(String provinceId) {
        // Deklarasi tabel dan field yang digunakan dengan jelas
        var villagesTable = org.jooq.impl.DSL.table("villages");
        var districtsTable = org.jooq.impl.DSL.table("districts");
        var regenciesTable = org.jooq.impl.DSL.table("regencies");

        var villageIdField = org.jooq.impl.DSL.field("villages.id", String.class);
        var districtIdField = org.jooq.impl.DSL.field("villages.district_id", String.class);
        var villageNameField = org.jooq.impl.DSL.field("villages.name", String.class);
        var regencyIdField = org.jooq.impl.DSL.field("districts.regency_id", String.class);
        var provinceIdField = org.jooq.impl.DSL.field("regencies.province_id", String.class);
        var regencyIdJoinField = org.jooq.impl.DSL.field("regencies.id", String.class);

        // Optimized query for fetching villages by provinceId
        var records = dsl.select(villageIdField, districtIdField, villageNameField)
                .from(villagesTable)
                .join(districtsTable).on(districtIdField.eq(org.jooq.impl.DSL.field("districts.id", String.class)))
                .join(regenciesTable).on(regencyIdField.eq(regencyIdJoinField))
                .where(provinceIdField.eq(provinceId))
                .fetch();

        // Map hasil query ke objek Village dengan efisiensi stream
        return records.stream().map(record -> new Village(
                record.get(villageIdField),
                record.get(districtIdField),
                record.get(villageNameField)
        )).collect(Collectors.toList());
    }

    /**
     * Upsert banyak data desa sekaligus menggunakan batch untuk performa lebih baik.
     * Pastikan batch size disesuaikan dengan kapasitas server untuk menghindari overload.
     *
     * @param villages List of Village entities untuk diupsert secara batch.
     */
    public void bulkUpsert(List<Village> villages) {
        // Define table and fields used in the upsert
        var villagesTable = org.jooq.impl.DSL.table("villages");
        var idField = org.jooq.impl.DSL.field("id");
        var districtIdField = org.jooq.impl.DSL.field("district_id");
        var nameField = org.jooq.impl.DSL.field("name");

        dsl.batch(
                villages.stream()
                        .map(village ->
                                dsl.insertInto(villagesTable)
                                        .set(idField, village.getId())
                                        .set(districtIdField, village.getDistrictId())
                                        .set(nameField, village.getName())
                                        .onDuplicateKeyUpdate()
                                        .set(districtIdField, village.getDistrictId())
                                        .set(nameField, village.getName())
                        ).collect(Collectors.toList())
        ).execute();
    }
}
