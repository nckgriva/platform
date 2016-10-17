package com.gracelogic.platform.db.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Igor Parkhomenko
 * Date: 22.03.12
 * Time: 17:21
 */
public abstract class IdObject<E extends Comparable> implements Serializable {
    public static final String ID = "id";
    public static final String CREATED = "created_dt";
    public static final String CHANGED = "changed_dt";

    protected IdObject() {
        setCreated(new Date());
    }

    public abstract E getId();

    public abstract void setId(E id);

    public abstract Date getChanged();

    public abstract void setChanged(Date changed);

    public abstract Date getCreated();

    public abstract void setCreated(Date created);

    @Override
    public int hashCode() {
        int result = 17;
        result = (int) (37 * result + (getId() == null ? 0 : (getId().hashCode() + 0)));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!obj.getClass().equals(this.getClass()))
            return false;

        IdObject cfg = (IdObject) obj;
        return (getId() == null && cfg.getId() == null && super.equals(obj))
                || (getId() != null && getId().equals(cfg.getId()));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{id=" + getId() + "}";
    }
}
