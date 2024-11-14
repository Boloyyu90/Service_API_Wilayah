package com.dimata.service.dewas.wilayah.repo;

import com.dimata.service.dewas.wilayah.model.District;
import com.dimata.service.dewas.wilayah.orm.core.JooqRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DistrictRepository extends JooqRepository {

    /**
     * Mengambil semua data kecamatan tanpa filter.
     * @return List berisi semua data kecamatan.
     */
    public List<District> findAllDistricts() {
        var districtsTable = org.jooq.impl.DSL.table("districts");
        var districtIdField = org.jooq.impl.DSL.field("id", String.class);
        var regencyIdField = org.jooq.impl.DSL.field("regency_id", String.class);
        var districtNameField = org.jooq.impl.DSL.field("name", String.class);

        // Query untuk mengambil semua data kecamatan
        var records = dsl.select(districtIdField, regencyIdField, districtNameField)
                .from(districtsTable)
                .fetch();

        // Map hasil query ke objek District
        return records.stream().map(record -> new District(
                record.get(districtIdField),
                record.get(regencyIdField),
                record.get(districtNameField)
        )).collect(Collectors.toList());
    }

    /**
     * Mencari daftar kecamatan berdasarkan ID provinsi.
     * Menggunakan join untuk mendapatkan kecamatan yang terkait dengan provinsi tertentu.
     *
     * @param provinceId ID provinsi yang digunakan sebagai filter.
     * @return List of Districts yang terkait dengan ID provinsi.
     */
    public List<District> findByProvinceId(String provinceId) {
        // Inisiasi nama tabel dan kolom
        var districtsTable = org.jooq.impl.DSL.table("districts");
        var regenciesTable = org.jooq.impl.DSL.table("regencies");

        var districtIdField = org.jooq.impl.DSL.field("districts.id", String.class);
        var regencyIdField = org.jooq.impl.DSL.field("districts.regency_id", String.class);
        var districtNameField = org.jooq.impl.DSL.field("districts.name", String.class);
        var provinceIdField = org.jooq.impl.DSL.field("regencies.province_id", String.class);
        var regencyIdJoinField = org.jooq.impl.DSL.field("regencies.id", String.class);

        // Eksekusi query dengan join antar tabel districts dan regencies
        var records = dsl.select(districtIdField, regencyIdField, districtNameField)
                .from(districtsTable)
                .join(regenciesTable).on(regencyIdField.eq(regencyIdJoinField))
                .where(provinceIdField.eq(provinceId))
                .fetch();

        // Mapping hasil query ke objek District
        return records.map(record -> new District(
                record.value1(),
                record.value2(),
                record.value3()
        ));
    }

    /**
     * Melakukan upsert (insert atau update jika sudah ada) pada tabel districts dalam jumlah besar.
     * Menggunakan batch processing untuk efisiensi.
     *
     * @param districts List of Districts untuk diinsert atau diupdate.
     */
    public void bulkUpsert(List<District> districts) {
        var districtsTable = org.jooq.impl.DSL.table("districts");
        var idField = org.jooq.impl.DSL.field("id");
        var regencyIdField = org.jooq.impl.DSL.field("regency_id");
        var nameField = org.jooq.impl.DSL.field("name");

        // Menyusun batch insert atau update dengan penggunaan parallel stream untuk performa
        dsl.batch(
                districts.parallelStream()
                        .map(district ->
                                dsl.insertInto(districtsTable)
                                        .set(idField, district.getId())
                                        .set(regencyIdField, district.getRegencyId())
                                        .set(nameField, district.getName())
                                        .onDuplicateKeyUpdate()
                                        .set(regencyIdField, district.getRegencyId())
                                        .set(nameField, district.getName())
                        ).collect(Collectors.toList())
        ).execute();
    }
}
