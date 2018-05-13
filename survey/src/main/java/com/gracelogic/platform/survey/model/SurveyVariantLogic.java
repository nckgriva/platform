package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import com.gracelogic.platform.survey.RelationType;
import org.hibernate.annotations.GenericGenerator;

import javax.management.relation.Relation;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_VARIANT_LOGIC")
public class SurveyVariantLogic extends IdObject<UUID> {
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SELECTED_VARIANT_ID", nullable = false)
    private SurveyAnswerVariant selectedVariant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_TARGET_QUESTION_ID", nullable = false)
    private SurveyQuestion targetQuestion;

    @Column(name = "relation_type", nullable = false)
    private Integer relationType; //TODO: Вынести в отдельную таблицу-справочник (для примера AccountType)

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

    public SurveyAnswerVariant getSelectedVariant() {
        return selectedVariant;
    }

    public void setSelectedVariant(SurveyAnswerVariant selectedVariant) {
        this.selectedVariant = selectedVariant;
    }

    public SurveyQuestion getTargetQuestion() {
        return targetQuestion;
    }

    public void setTargetQuestion(SurveyQuestion targetQuestion) {
        this.targetQuestion = targetQuestion;
    }

    public RelationType getRelationType() {
        switch (relationType) {
            case 0: return RelationType.HIDES_QUESTION;
            case 1: return RelationType.SHOWS_QUESTION;
            default: return RelationType.HIDES_QUESTION;
        }
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType.ordinal();
    }
}
