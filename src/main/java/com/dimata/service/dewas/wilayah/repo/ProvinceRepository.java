package com.dimata.service.dewas.wilayah.repo;

import com.dimata.service.dewas.wilayah.model.Province;
import com.dimata.service.dewas.wilayah.orm.core.JooqRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.Record;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProvinceRepository extends JooqRepository {

    /**
     * Mengambil semua data provinsi dari tabel provinces.
     *
     * @return List of Province berisi semua data provinsi.
     */
    public List<Province> findAll() {
        // Query untuk mengambil semua data dari tabel provinces
        return dsl.select()
                .from("provinces")
                .fetch()
                .map(record -> {
                    Province province = new Province();
                    province.setId(record.get("id", String.class));
                    province.setName(record.get("name", String.class));
                    return province;
                });
    }

    /**
     * Mengambil data provinsi berdasarkan ID.
     * Kalau data provinsi tidak ditemukan, akan mengembalikan Optional kosong.
     *
     * @param id ID provinsi yang mau dicari.
     * @return Optional berisi data provinsi jika ditemukan, atau Optional kosong jika tidak ada.
     */
    public Optional<Province> findById(String id) {
        // Query untuk mengambil data provinsi berdasarkan ID
        Record record = dsl.select()
                .from("provinces")
                .where("id = ?", id)
                .fetchOne();

        // Kalau record tidak ditemukan, kembalikan Optional kosong
        if (record == null) {
            return Optional.empty();
        }

        // Map hasil query ke objek Province
        Province province = new Province();
        province.setId(record.get("id", String.class));
        province.setName(record.get("name", String.class));
        return Optional.of(province);
    }

    /**
     * Melakukan upsert (insert atau update jika sudah ada) pada tabel provinces dalam jumlah besar.
     * Menggunakan batch processing untuk efisiensi.
     *
     * @param provinces List of Province untuk diinsert atau diupdate.
     */
    public void bulkUpsert(List<Province> provinces) {
        var provincesTable = org.jooq.impl.DSL.table("provinces");
        var idField = org.jooq.impl.DSL.field("id");
        var nameField = org.jooq.impl.DSL.field("name");

        // Menggunakan parallel stream dan batch processing
        dsl.batch(
                provinces.parallelStream()
                        .map(province ->
                                dsl.insertInto(provincesTable)
                                        .set(idField, province.getId())
                                        .set(nameField, province.getName())
                                        .onDuplicateKeyUpdate()
                                        .set(nameField, province.getName())
                        ).collect(Collectors.toList())
        ).execute();
    }
}
