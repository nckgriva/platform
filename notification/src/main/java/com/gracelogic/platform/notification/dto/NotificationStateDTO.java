package com.gracelogic.platform.notification.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.notification.model.NotificationState;

public class NotificationStateDTO extends IdObjectDTO {
    private String name;
    private Integer sortOrder;

    public NotificationStateDTO prepare(NotificationState model) {
        NotificationStateDTO dto = new NotificationStateDTO();
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