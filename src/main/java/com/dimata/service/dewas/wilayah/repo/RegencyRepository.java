package com.dimata.service.dewas.wilayah.repo;

import com.dimata.service.dewas.wilayah.model.Regency;
import com.dimata.service.dewas.wilayah.orm.core.JooqRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RegencyRepository extends JooqRepository {

    /**
     * Mengambil semua data kabupaten dari tabel regencies.
     *
     * @return List of Regency berisi semua data kabupaten.
     */
    public List<Regency> findAll() {
        // Query untuk mengambil semua data dari tabel regencies
        return dsl.select()
                .from("regencies")
                .fetch()
                .map(record -> new Regency(
                        record.get("id", String.class),
                        record.get("province_id", String.class),
                        record.get("name", String.class)
                ));
    }

    /**
     * Mengambil data kabupaten berdasarkan ID provinsi.
     * Method ini akan mengembalikan daftar kabupaten yang sesuai dengan ID provinsi yang diberikan.
     *
     * @param provinceId ID provinsi yang mau dicari datanya.
     * @return List berisi data kabupaten yang ada di provinsi tersebut.
     */
    public List<Regency> findByProvinceId(String provinceId) {
        // Query untuk mengambil data kabupaten berdasarkan ID provinsi
        Result<Record> records = dsl.select()
                .from("regencies")
                .where("province_id = ?", provinceId)
                .fetch();

        // Map hasil query ke objek Regency
        return records.map(record -> new Regency(
                record.get("id", String.class),
                record.get("province_id", String.class),
                record.get("name", String.class)
        ));
    }

    /**
     * Melakukan upsert (insert atau update jika sudah ada) pada tabel regencies dalam jumlah besar.
     *
     * @param regencies List of Regency untuk diinsert atau diupdate.
     */
    public void bulkUpsert(List<Regency> regencies) {
        var regenciesTable = org.jooq.impl.DSL.table("regencies");
        var idField = org.jooq.impl.DSL.field("id");
        var provinceIdField = org.jooq.impl.DSL.field("province_id");
        var nameField = org.jooq.impl.DSL.field("name");

        dsl.batch(
                regencies.parallelStream()
                        .map(regency ->
                                dsl.insertInto(regenciesTable)
                                        .set(idField, regency.getId())
                                        .set(provinceIdField, regency.getProvinceId())
                                        .set(nameField, regency.getName())
                                        .onDuplicateKeyUpdate()
                                        .set(provinceIdField, regency.getProvinceId())
                                        .set(nameField, regency.getName())
                        ).collect(Collectors.toList())
        ).execute();
    }
}
