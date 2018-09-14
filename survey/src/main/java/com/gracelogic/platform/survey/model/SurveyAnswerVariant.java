package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_ANSWER_VARIANT")
public class SurveyAnswerVariant extends IdObject<UUID> {
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
    @JoinColumn(name = "SURVEY_QUESTION_ID", nullable = false)
    private SurveyQuestion surveyQuestion;

    @Column(name = "TEXT", nullable = false, length=4000)
    private String text;

    @Column(name = "SORT_ORDER", nullable = false)
    private Integer sortOrder;

    @Column(name = "IS_DEFAULT_VARIANT", nullable = false)
    private Boolean defaultVariant; // Вариант выбран по умолчанию

    @Column(name = "IS_CUSTOM_VARIANT", nullable = false)
    private Boolean customVariant; // Вариант самостоятельного ответа

    @Column(name = "WEIGHT", nullable = true)
    private Integer weight; // Вес ответа для автоматической обработки

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

    public SurveyQuestion getSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getDefaultVariant() {
        return defaultVariant;
    }

    public void setDefaultVariant(Boolean defaultVariant) {
        this.defaultVariant = defaultVariant;
    }

    public Boolean getCustomVariant() {
        return customVariant;
    }

    public void setCustomVariant(Boolean customVariant) {
        this.customVariant = customVariant;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
