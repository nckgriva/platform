package com.gracelogic.platform.filestorage.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 11.12.14
 * Time: 12:32
 */
@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "STORED_FILE", schema = JPAProperties.DEFAULT_SCHEMA)
public class StoredFile extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "STORE_MODE_ID", nullable = false)
    private StoreMode storeMode;

    @Column(name = "EXTENSION", nullable = false)
    private String extension;

    @Column(name = "IS_DATA_AVAILABLE", nullable = false)
    private Boolean dataAvailable;

    @Column(name = "REFERENCE_OBJECT_ID", nullable = true)
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID referenceObjectId;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public StoreMode getStoreMode() {
        return storeMode;
    }

    public void setStoreMode(StoreMode storeMode) {
        this.storeMode = storeMode;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public UUID getReferenceObjectId() {
        return referenceObjectId;
    }

    public void setReferenceObjectId(UUID referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    public Boolean getDataAvailable() {
        return dataAvailable;
    }

    public void setDataAvailable(Boolean dataAvailable) {
        this.dataAvailable = dataAvailable;
    }
}
