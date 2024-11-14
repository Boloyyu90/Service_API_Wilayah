package com.dimata.service.dewas.wilayah.controller;

import com.dimata.service.dewas.wilayah.model.District;
import com.dimata.service.dewas.wilayah.service.DistrictService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path("/districts")
public class DistrictController {

    @Inject
    DistrictService districtService;

    /**
     * Endpoint untuk mengambil semua data kecamatan.
     * @return Response berisi data semua kecamatan.
     */
    @GET
    public Response getAllDistricts() {
        List<District> districts = districtService.getAllDistricts();
        if (!districts.isEmpty()) {
            return Response.ok(districts).build();
        } else {
            // Kalau tidak ada data kecamatan yang ditemukan
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "message", "Tidak ada data kecamatan yang ditemukan.", "timestamp", LocalDateTime.now().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Mengambil data kecamatan berdasarkan ID provinsi.
     * Validasi dilakukan pada input ID untuk memastikan sesuai format yang diharapkan.
     *
     * @param provinceId ID provinsi yang akan dicari kecamatannya.
     * @return Response berisi data kecamatan atau pesan error jika tidak ditemukan.
     */
    @GET
    @Path("/{provinceId}")
    public Response getDistrictsByProvinceId(@PathParam("provinceId") String provinceId) {
        try {
            Integer.parseInt(provinceId); // Validasi bahwa provinceId berbentuk angka
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("status", 400, "message", "ID provinsi harus berupa angka.", "timestamp", LocalDateTime.now().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        try {
            List<District> districts = districtService.findByProvinceId(provinceId);
            if (!districts.isEmpty()) {
                return Response.ok(districts).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("status", 404, "message", "Tidak ada kecamatan untuk ID provinsi " + provinceId, "timestamp", LocalDateTime.now().toString()))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            // Penanganan error umum jika terjadi kegagalan dalam pemrosesan data
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("status", 500, "message", "Terjadi kesalahan pada server saat mengambil data kecamatan.", "error", e.getMessage(), "timestamp", LocalDateTime.now().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Endpoint untuk mengimpor data kecamatan dari file CSV.
     *
     * @return Response JSON berisi status sukses atau pesan error.
     */
    @POST
    @Path("/import")
    public Response importDistrictsFromCsv() {
        try {
            districtService.importFromCsvWithUpsert();
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of("status", 201, "message", "Data kecamatan berhasil diimpor dan diperbarui dari CSV.", "timestamp", LocalDateTime.now().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (IllegalArgumentException e) {
            // Penanganan untuk kasus data CSV yang tidak sesuai atau tidak valid
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("status", 400, "message", "Data dalam file CSV tidak valid. Periksa format dan isi file.", "error", e.getMessage(), "timestamp", LocalDateTime.now().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            // Penanganan error umum untuk kegagalan lainnya
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("status", 500, "message", "Gagal mengimpor data dari CSV. Silakan periksa format dan lokasi file.", "error", e.getMessage(), "timestamp", LocalDateTime.now().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
