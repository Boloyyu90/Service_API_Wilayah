package com.dimata.service.dewas.wilayah.service;

import com.dimata.service.dewas.wilayah.model.Regency;
import com.dimata.service.dewas.wilayah.repo.RegencyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class RegencyService {

    private static final Logger LOGGER = Logger.getLogger(RegencyService.class.getName());
    private static final int BATCH_SIZE = 1000;

    @Inject
    RegencyRepository regencyRepository;

    /**
     * Mengambil semua data kabupaten.
     *
     * @return List of Regency berisi semua data kabupaten.
     */
    public List<Regency> getAllRegencies() {
        return regencyRepository.findAll();
    }

    /**
     * Mengambil data kabupaten berdasarkan ID provinsi.
     * Jika tidak ada data, coba import data dari CSV.
     *
     * @param provinceId ID provinsi.
     * @return List of Regency yang sesuai dengan ID provinsi.
     */
    public List<Regency> getRegenciesByProvinceId(String provinceId) {
        List<Regency> regencies = regencyRepository.findByProvinceId(provinceId);

        if (regencies.isEmpty()) {
            importFromCsvWithUpsert();
            regencies = regencyRepository.findByProvinceId(provinceId);
        }

        return regencies;
    }

    /**
     * Import data dari file CSV ke database menggunakan batch processing.
     */
    public void importFromCsvWithUpsert() {
        String resourcePath = "/file-data-wilayah/regencies.csv";

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            csvParser.getRecords().parallelStream()
                    .map(record -> new Regency(record.get(0), record.get(1), record.get(2)))
                    .collect(Collectors.groupingBy(r -> r.getId().hashCode() % BATCH_SIZE))
                    .values()
                    .forEach(batch -> {
                        regencyRepository.bulkUpsert(batch);
                        LOGGER.log(Level.INFO, "Batch upsert sukses dengan size: " + batch.size());
                    });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Gagal membaca file CSV: " + resourcePath, e);
        }
    }
}
