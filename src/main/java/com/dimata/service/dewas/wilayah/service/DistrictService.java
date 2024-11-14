package com.dimata.service.dewas.wilayah.service;

import com.dimata.service.dewas.wilayah.model.District;
import com.dimata.service.dewas.wilayah.repo.DistrictRepository;
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
public class DistrictService {

    private static final Logger LOGGER = Logger.getLogger(DistrictService.class.getName());
    private static final int BATCH_SIZE = 1000; // Menentukan ukuran batch optimal

    @Inject
    DistrictRepository districtRepository;

    /**
     * Mengambil semua data kecamatan.
     * @return List berisi semua data kecamatan.
     */
    public List<District> getAllDistricts() {
        return districtRepository.findAllDistricts();
    }

    /**
     * Mencari kecamatan berdasarkan ID provinsi.
     * Potensial optimasi: cache untuk menghindari query berulang pada data yang sama.
     *
     * @param provinceId ID provinsi
     * @return List of Districts berdasarkan ID provinsi
     */
    public List<District> findByProvinceId(String provinceId) {
        return districtRepository.findByProvinceId(provinceId);
    }

    /**
     * Import data kecamatan dari file CSV dan melakukan upsert secara batch.
     * Menggunakan parallel stream untuk memproses data lebih cepat dan groupingBy untuk membagi batch.
     */
    public void importFromCsvWithUpsert() {
        String resourcePath = "/file-data-wilayah/districts.csv";

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            // Parsing CSV dengan parallel stream dan batch processing
            csvParser.getRecords().parallelStream()
                    .map(record -> new District(record.get(0), record.get(1), record.get(2)))
                    .collect(Collectors.groupingBy(d -> d.getId().hashCode() % BATCH_SIZE))
                    .values()
                    .forEach(batch -> {
                        districtRepository.bulkUpsert(batch);
                        LOGGER.log(Level.INFO, "Batch upsert sukses dengan size: " + batch.size());
                    });

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Gagal membaca file CSV: " + resourcePath, e);
        }
    }
}
