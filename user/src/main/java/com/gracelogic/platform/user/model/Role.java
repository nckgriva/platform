package com.gracelogic.platform.user.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.dictionary.model.Dictionary;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 25.06.12
 * Time: 18:38
 */
@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "ROLE", schema = JPAProperties.DEFAULT_SCHEMA)
public class Role extends IdObject<UUID> implements Dictionary {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @Column(name = NAME, nullable = false)
    private String name;

    @Column(name = CODE, nullable = false)
    private String code;

    @OneToMany(mappedBy = "role", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<RoleGrant> roleGrantSet = new HashSet<RoleGrant>();

    @Override
    public UUID getId() {
        return id;
    }

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

    public Set<RoleGrant> getRoleGrantSet() {
        return roleGrantSet;
    }

    public void setRoleGrantSet(Set<RoleGrant> roleGrantSet) {
        this.roleGrantSet = roleGrantSet;
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
}
