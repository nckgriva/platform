package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.survey.QuestionType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_QUESTION")
public class SurveyQuestion extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @Access(AccessType.PROPERTY)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_PAGE_ID", nullable = false)
    private SurveyPage surveyPage;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "hidden", nullable = false)
    private boolean hidden;

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

    public SurveyPage getSurveyPage() {
        return surveyPage;
    }

    public void setSurveyPage(SurveyPage surveyPage) {
        this.surveyPage = surveyPage;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QuestionType getType() {
        switch (type) {
            case 0: return QuestionType.RADIOBUTTON;
            case 1: return QuestionType.CHECKBOX;
            case 2: return QuestionType.TEXT;
            case 3: return QuestionType.COMBOBOX;
            case 4: return QuestionType.LISTBOX;
            case 5: return QuestionType.RATING_SCALE;
            case 6: return QuestionType.ATTACHMENT;
            default: return QuestionType.RADIOBUTTON;
        }
    }

    public void setType(QuestionType type) {
        this.type = type.ordinal();
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
