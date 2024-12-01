package com.gracelogic.platform.content.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SECTION_PATTERN_FIELD", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"SECTION_PATTERN_ID", "CODE"})})
public class SectionPatternField extends IdObject<UUID> {
    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "NULLABLE", nullable = false)
    private Boolean nullable;

    @Column(name = "DICTIONARY_SERVICE_NAME", nullable = true, length = 400)
    private String dictionaryServiceName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SECTION_PATTERN_ID", nullable = false)
    private SectionPattern sectionPattern;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ELEMENT_FIELD_TYPE_ID", nullable = false)
    private ElementFieldType elementFieldType;

    @Column(name = "SORT_ORDER", nullable = true)
    private Integer sortOrder;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public SectionPattern getSectionPattern() {
        return sectionPattern;
    }

    public void setSectionPattern(SectionPattern sectionPattern) {
        this.sectionPattern = sectionPattern;
    }

    public ElementFieldType getElementFieldType() {
        return elementFieldType;
    }

    public void setElementFieldType(ElementFieldType elementFieldType) {
        this.elementFieldType = elementFieldType;
    }

    public String getDictionaryServiceName() {
        return dictionaryServiceName;
    }

    public void setDictionaryServiceName(String dictionaryServiceName) {
        this.dictionaryServiceName = dictionaryServiceName;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
