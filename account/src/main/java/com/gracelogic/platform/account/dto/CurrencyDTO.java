package com.gracelogic.platform.account.dto;

import com.gracelogic.platform.account.model.Currency;
import com.gracelogic.platform.db.dto.IdObjectDTO;

public class CurrencyDTO extends IdObjectDTO {
    private String name;
    private String code;

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

    public static CurrencyDTO prepare(Currency model) {
        CurrencyDTO dto = new CurrencyDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setName(model.getName());
        dto.setCode(model.getCode());

        return dto;
    }
}
