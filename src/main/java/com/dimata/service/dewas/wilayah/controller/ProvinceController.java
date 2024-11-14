package com.dimata.service.dewas.wilayah.controller;

import com.dimata.service.dewas.wilayah.model.Province;
import com.dimata.service.dewas.wilayah.service.ProvinceService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/provinces")
public class ProvinceController {

    @Inject
    ProvinceService provinceService;

    /**
     * Mengambil semua data provinsi.
     *
     * @return Response berisi semua data provinsi atau pesan error jika terjadi masalah.
     */
    @GET
    @Path("/")
    public Response getAllProvinces() {
        try {
            List<Province> provinces = provinceService.getAllProvinces();
            return Response.ok(provinces).build();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Gagal mengambil data provinsi.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Ambil data provinsi berdasarkan ID.
     * Memvalidasi ID dan mengembalikan data provinsi jika ditemukan.
     *
     * @param id ID provinsi yang mau dicari.
     * @return Response berisi data provinsi atau pesan error jika tidak valid/tidak ditemukan.
     */
    @GET
    @Path("/{id}")
    public Response getProvinceById(@PathParam("id") String id) {
        try {
            int provinceId = Integer.parseInt(id);
            if (provinceId < 11 || provinceId > 94) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 400);
                errorResponse.put("message", "ID yang valid untuk provinsi adalah antara 11 hingga 94.");
                errorResponse.put("timestamp", LocalDateTime.now().toString());

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (NumberFormatException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "ID provinsi harus berupa angka.");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Cari data provinsi berdasarkan ID
        Optional<Province> province = provinceService.getProvinceById(id);
        if (province.isPresent()) {
            return Response.ok(province.get()).build();
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("message", "Province dengan ID " + id + " tidak ditemukan.");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Import data provinsi dari file CSV.
     * Kalau import berhasil, kirim respons sukses; kalau gagal, kirim error.
     *
     * @return Response berisi status sukses atau error saat import data.
     */
    @POST
    @Path("/import")
    public Response importProvincesFromCsv() {
        try {
            provinceService.importFromCsvWithUpsert();
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", 201);
            successResponse.put("message", "Data provinsi berhasil diimpor dan diperbarui dari CSV.");
            successResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.CREATED)
                    .entity(successResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Gagal mengimpor data dari CSV. Silakan periksa format dan lokasi file.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
