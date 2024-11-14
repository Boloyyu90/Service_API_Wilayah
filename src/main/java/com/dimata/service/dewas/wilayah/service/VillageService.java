package com.dimata.service.dewas.wilayah.service;

import com.dimata.service.dewas.wilayah.model.Village;
import com.dimata.service.dewas.wilayah.repo.VillageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class VillageService {

    private static final Logger LOGGER = Logger.getLogger(VillageService.class.getName());
    private static final int BATCH_SIZE = 1000;

    @Inject
    VillageRepository villageRepository;

    /**
     * Mengambil semua data desa.
     * @return List berisi semua data desa.
     */
    public List<Village> getAllVillages() {
        return villageRepository.findAll();
    }

    /**
     * Mengambil daftar desa berdasarkan ID provinsi.
     *
     * @param provinceId ID provinsi
     * @return List desa dalam provinsi terkait
     */
    public List<Village> getVillagesByProvinceId(String provinceId) {
        return villageRepository.findByProvinceId(provinceId);
    }

    /**
     * Import data desa dari CSV menggunakan upsert dalam batch.
     * Metode ini membaca file CSV dalam mode stream dan mengelompokkan data untuk di-batch proses upsert.
     */
    public void importFromCsvWithUpsert() {
        String resourcePath = "/file-data-wilayah/villages.csv";

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            // Stream paralel untuk parsing dan batching CSV records
            csvParser.getRecords().parallelStream()
                    .map(record -> new Village(record.get(0), record.get(1), record.get(2)))
                    .collect(Collectors.groupingBy(v -> v.getId().hashCode() % BATCH_SIZE))
                    .values()
                    .forEach(batch -> {
                        villageRepository.bulkUpsert(batch);
                        LOGGER.log(Level.INFO, "Batch upsert sukses dengan size: " + batch.size());
                    });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal membaca file CSV: " + resourcePath, e);
        }
    }
}
