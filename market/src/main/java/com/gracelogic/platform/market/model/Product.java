package com.gracelogic.platform.market.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "PRODUCT")
public class Product extends IdObject<UUID> {
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

    @Column(name = "NAME", nullable = true)
    private String name;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCT_TYPE_ID", nullable = false)
    private ProductType productType;

    @Column(name = "REFERENCE_OBJECT_ID", nullable = true)
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID referenceObjectId; //Если null - значит продуктом является любая сущность из таблицы заданного ProductType

    @Column(name = "LIFETIME", nullable = true)
    private Long lifetime; //Проставляется если продукт ограничен по времени использования

    @Column(name = "PRICE", nullable = true)
    private Long price;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public UUID getReferenceObjectId() {
        return referenceObjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReferenceObjectId(UUID referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    public Long getLifetime() {
        return lifetime;
    }

    public void setLifetime(Long lifetime) {
        this.lifetime = lifetime;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
