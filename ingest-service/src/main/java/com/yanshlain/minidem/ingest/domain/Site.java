package com.yanshlain.minidem.ingest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A monitored location (e.g. a branch office or cloud region) — the "site" a network
 * event was observed at. Backed by the "sites" table.
 *
 * @Entity tells Hibernate "this class maps to a database table" — roughly what an
 * EF Core class annotated for a DbSet does, except Hibernate discovers it via
 * classpath scanning rather than you registering a DbSet<Site> explicitly.
 */
@Entity
@Table(name = "sites")
public class Site {

    // @Id marks the primary key. @GeneratedValue(IDENTITY) means "let Postgres assign
    // it" (a SERIAL/IDENTITY column), same idea as an EF Core auto-increment key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The short, stable name we and event-generator both use to refer to this site
    // (e.g. "tel-aviv-office"). Unique so ingestion can look a site up by this code.
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String displayName;

    // Hibernate needs a no-arg constructor to build an empty instance and then fill in
    // fields via reflection when loading a row from the database. It doesn't need to be
    // public, so "protected" keeps outside code from accidentally calling it directly.
    protected Site() {
    }

    public Site(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
