package com.gracelogic.platform.user.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "PASSPHRASE")
public class Passphrase extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @Access(AccessType.PROPERTY)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "com.gracelogic.platform.db.type.UUIDCustomType")
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "VALUE", nullable = false)
    private String value;

    @Column(name = "SALT", nullable = false)
    private String salt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PASSPHRASE_TYPE_ID", nullable = false)
    private PassphraseType passphraseType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PASSPHRASE_STATE_ID", nullable = false)
    private PassphraseState passphraseState;

    @Column(name = "REFERENCE_OBJECT_ID", nullable = false)
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PassphraseType getPassphraseType() {
        return passphraseType;
    }

    public void setPassphraseType(PassphraseType passphraseType) {
        this.passphraseType = passphraseType;
    }

    public PassphraseState getPassphraseState() {
        return passphraseState;
    }

    public void setPassphraseState(PassphraseState passphraseState) {
        this.passphraseState = passphraseState;
    }

    public UUID getReferenceObjectId() {
        return referenceObjectId;
    }

    public void setReferenceObjectId(UUID referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }
}
