package com.gracelogic.platform.notification.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.notification.model.NotificationMethod;

public class NotificationMethodDTO extends IdObjectDTO {
    private String name;
    private Integer sortOrder;

    public NotificationMethodDTO prepare(NotificationMethod model) {
        NotificationMethodDTO dto = new NotificationMethodDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setName(model.getName());
        dto.setSortOrder(model.getSortOrder());
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
