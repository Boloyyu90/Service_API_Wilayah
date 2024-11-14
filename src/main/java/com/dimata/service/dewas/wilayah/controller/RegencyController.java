package com.dimata.service.dewas.wilayah.controller;

import com.dimata.service.dewas.wilayah.model.Regency;
import com.dimata.service.dewas.wilayah.service.RegencyService;
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

@Path("/regencies")
public class RegencyController {

    @Inject
    RegencyService regencyService;

    /**
     * Mengambil semua data kabupaten.
     *
     * @return Response berisi semua data kabupaten atau pesan error jika terjadi masalah.
     */
    @GET
    public Response getAllRegencies() {
        try {
            List<Regency> regencies = regencyService.getAllRegencies();
            return Response.ok(regencies).build();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Gagal mengambil data kabupaten.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Ambil data kabupaten berdasarkan ID provinsi.
     * Kalau ID provinsi tidak valid atau data kabupaten tidak ditemukan, akan mengembalikan error.
     *
     * @param provinceId ID provinsi yang mau dicari datanya.
     * @return Response berisi data kabupaten atau pesan error.
     */
    @GET
    @Path("/{provinceId}")
    public Response getRegenciesByProvinceId(@PathParam("provinceId") String provinceId) {
        try {
            Integer.parseInt(provinceId);
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

        List<Regency> regencies = regencyService.getRegenciesByProvinceId(provinceId);
        if (!regencies.isEmpty()) {
            return Response.ok(regencies).build();
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("message", "Tidak ada kabupaten untuk ID provinsi " + provinceId + ".");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Import data kabupaten dari file CSV.
     * Kalau import berhasil, kirim respons sukses; kalau gagal, kirim error.
     *
     * @return Response berisi status sukses atau error saat import data.
     */
    @POST
    @Path("/import")
    public Response importRegenciesFromCsv() {
        try {
            regencyService.importFromCsvWithUpsert();
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", 201);
            successResponse.put("message", "Data kabupaten berhasil diimpor dan diperbarui dari CSV.");
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
