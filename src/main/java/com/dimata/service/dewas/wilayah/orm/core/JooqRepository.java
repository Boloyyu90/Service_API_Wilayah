package com.dimata.service.dewas.wilayah.orm.core;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

@ApplicationScoped
public class JooqRepository {

    @Inject
    protected DSLContext dsl;  // Menggunakan DSLContext dari JOOQ
}
