package com.dimata.service.dewas.wilayah.controller;

import com.dimata.service.dewas.wilayah.model.Village;
import com.dimata.service.dewas.wilayah.service.VillageService;
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

@Path("/villages")
public class VillageController {

    @Inject
    VillageService villageService;

    /**
     * Endpoint untuk mengambil semua data desa.
     * @return Response berisi data semua desa.
     */
    @GET
    public Response getAllVillages() {
        List<Village> villages = villageService.getAllVillages();
        if (!villages.isEmpty()) {
            return Response.ok(villages).build();
        } else {
            // Kalau tidak ada data desa yang ditemukan
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("message", "Tidak ada data desa yang ditemukan.");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Endpoint untuk mengambil data desa berdasarkan ID provinsi.
     * Validasi jika ID tidak valid atau data desa tidak ditemukan.
     *
     * @param provinceId ID provinsi yang mau dicari datanya.
     * @return Response berisi data desa atau pesan error.
     */
    @GET
    @Path("/{provinceId}")
    public Response getVillagesByProvinceId(@PathParam("provinceId") String provinceId) {
        // Validasi apakah ID provinsi berupa angka
        try {
            Integer.parseInt(provinceId);
        } catch (NumberFormatException e) {
            // Kalau ID bukan angka, kirim respons error
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "ID provinsi harus berupa angka.");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Ambil data desa dari service
        List<Village> villages = villageService.getVillagesByProvinceId(provinceId);
        if (!villages.isEmpty()) {
            return Response.ok(villages).build();
        } else {
            // Kalau data desa tidak ditemukan
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("message", "Tidak ada desa untuk ID provinsi " + provinceId + ".");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Endpoint untuk import data desa dari file CSV.
     * Mengembalikan status sukses atau error saat proses import data.
     *
     * @return Response berisi status sukses atau error saat import data.
     */
    @POST
    @Path("/import")
    public Response importVillagesFromCsv() {
        try {
            villageService.importFromCsvWithUpsert(); // Memanggil method upsert di service
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", 201);
            successResponse.put("message", "Data desa berhasil diimpor dan diperbarui dari CSV.");
            successResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.CREATED)
                    .entity(successResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Gagal mengimpor data dari CSV.");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
