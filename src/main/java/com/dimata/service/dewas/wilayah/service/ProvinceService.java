package com.dimata.service.dewas.wilayah.service;

import com.dimata.service.dewas.wilayah.model.Province;
import com.dimata.service.dewas.wilayah.repo.ProvinceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProvinceService {

    private static final Logger LOGGER = Logger.getLogger(ProvinceService.class.getName());
    private static final int BATCH_SIZE = 1000;

    @Inject
    ProvinceRepository provinceRepository;

    /**
     * Mendapatkan semua data provinsi.
     *
     * @return List of Province berisi semua data provinsi.
     */
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    /**
     * Mencari data provinsi berdasarkan ID, dengan fallback ke import data CSV jika tidak ditemukan.
     *
     * @param id ID provinsi
     * @return Optional berisi Province jika ditemukan, atau Optional kosong jika tidak ditemukan.
     */
    public Optional<Province> getProvinceById(String id) {
        return Optional.ofNullable(provinceRepository.findById(id)
                .orElseGet(() -> {
                    importFromCsvWithUpsert();
                    return provinceRepository.findById(id).orElse(null);
                }));
    }

    /**
     * Import data dari file CSV ke database menggunakan batch processing.
     * Menggunakan parallel stream dan group by untuk meningkatkan performa.
     */
    public void importFromCsvWithUpsert() {
        String resourcePath = "/file-data-wilayah/provinces.csv";

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            // Parsing CSV dan memproses dalam batch
            csvParser.getRecords().parallelStream()
                    .map(record -> new Province(record.get(0), record.get(1)))
                    .collect(Collectors.groupingBy(p -> p.getId().hashCode() % BATCH_SIZE))
                    .values()
                    .forEach(batch -> {
                        provinceRepository.bulkUpsert(batch);
                        LOGGER.log(Level.INFO, "Batch upsert sukses dengan size: " + batch.size());
                    });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal membaca file CSV: " + resourcePath, e);
        }
    }
}
