package com.gracelogic.platform.user.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.user.model.PassphraseType;

import java.util.UUID;

public class PassphraseTypeDTO extends IdObjectDTO {

    private String name;
    private Long lifetime;
    private String validationRegex;
    private String passphraseEncryptionName;
    private UUID passphraseEncryptionId;
    private String passphraseGeneratorName;
    private UUID passphraseGeneratorId;

    public static PassphraseTypeDTO prepare(PassphraseType passphraseType, boolean enrich) {
        PassphraseTypeDTO dto = new PassphraseTypeDTO();
        IdObjectDTO.prepare(dto, passphraseType);
        dto.setName(passphraseType.getName());
        dto.setLifetime(passphraseType.getLifetime());
        dto.setValidationRegex(passphraseType.getValidationRegex());

        if (enrich) {
            if (passphraseType.getPassphraseEncryption() != null) {
                dto.setPassphraseEncryptionId(passphraseType.getPassphraseEncryption().getId());
                dto.setPassphraseEncryptionName(passphraseType.getPassphraseEncryption().getName());
            }
            if (passphraseType.getPassphraseGenerator() != null) {
                dto.setPassphraseGeneratorId(passphraseType.getPassphraseGenerator().getId());
                dto.setPassphraseGeneratorName(passphraseType.getPassphraseGenerator().getName());
            }

        }

        return dto;
    }

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
}
