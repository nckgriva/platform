package com.gracelogic.platform.user.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "PASSPHRASE_TYPE")
public class PassphraseType extends IdObject<UUID> {
    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "com.gracelogic.platform.db.type.UUIDCustomType")
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PASSPHRASE_ENCRYPTION_ID", nullable = false)
    private PassphraseEncryption passphraseEncryption;

    @Column(name = "LIFETIME", nullable = true)
    private Long lifetime;

    @Column(name = "VALIDATION_REGEX", nullable = true)
    private String validationRegex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PASSPHRASE_GENERATOR_ID", nullable = true)
    private PassphraseGenerator passphraseGenerator;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PassphraseEncryption getPassphraseEncryption() {
        return passphraseEncryption;
    }

    public void setPassphraseEncryption(PassphraseEncryption passphraseEncryption) {
        this.passphraseEncryption = passphraseEncryption;
    }

    public Long getLifetime() {
        return lifetime;
    }

    public void setLifetime(Long lifetime) {
        this.lifetime = lifetime;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public PassphraseGenerator getPassphraseGenerator() {
        return passphraseGenerator;
    }

    public void setPassphraseGenerator(PassphraseGenerator passphraseGenerator) {
        this.passphraseGenerator = passphraseGenerator;
    }
}
