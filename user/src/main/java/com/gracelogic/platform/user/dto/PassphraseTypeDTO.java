package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.user.model.PassphraseType;

import java.util.UUID;

public class PassphraseTypeDTO extends IdObjectDTO {

    private String name;
    private Long lifetime;
    private String validationRegex;
    private UUID passphraseEncryptionId;
    private String passphraseEncryptionName;
    private UUID passphraseGeneratorId;
    private String passphraseGeneratorName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassphraseEncryptionName() {
        return passphraseEncryptionName;
    }

    public void setPassphraseEncryptionName(String passphraseEncryptionName) {
        this.passphraseEncryptionName = passphraseEncryptionName;
    }

    public UUID getPassphraseEncryptionId() {
        return passphraseEncryptionId;
    }

    public void setPassphraseEncryptionId(UUID passphraseEncryptionId) {
        this.passphraseEncryptionId = passphraseEncryptionId;
    }

    public String getPassphraseGeneratorName() {
        return passphraseGeneratorName;
    }

    public void setPassphraseGeneratorName(String passphraseGeneratorName) {
        this.passphraseGeneratorName = passphraseGeneratorName;
    }

    public UUID getPassphraseGeneratorId() {
        return passphraseGeneratorId;
    }

    public void setPassphraseGeneratorId(UUID passphraseGeneratorId) {
        this.passphraseGeneratorId = passphraseGeneratorId;
    }

    public static PassphraseTypeDTO prepare(PassphraseType model) {
        PassphraseTypeDTO dto = new PassphraseTypeDTO();
        IdObjectDTO.prepare(dto, model);
        dto.setName(model.getName());
        dto.setLifetime(model.getLifetime());
        dto.setValidationRegex(model.getValidationRegex());

        if (model.getPassphraseEncryption() != null) {
            dto.setPassphraseEncryptionId(model.getPassphraseEncryption().getId());
        }
        if (model.getPassphraseGenerator() != null) {
            dto.setPassphraseGeneratorId(model.getPassphraseGenerator().getId());
        }

        return dto;
    }

    public static PassphraseTypeDTO enrich(PassphraseTypeDTO dto, PassphraseType model) {
        if (model.getPassphraseEncryption() != null) {
            dto.setPassphraseEncryptionName(model.getPassphraseEncryption().getName());
        }
        if (model.getPassphraseGenerator() != null) {
            dto.setPassphraseGeneratorName(model.getPassphraseGenerator().getName());
        }

        return dto;
    }
}
