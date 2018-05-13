package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "USER_ANSWER")
public class UserAnswer extends IdObject<UUID> {
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
    @Column(name = "survey_passing_id", nullable = false)
    private SurveyPassing surveyPassing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Column(name = "answer_variant", nullable = true)
    private SurveyAnswerVariant answerVariant;

    @Column(name = "text_answer", nullable = true)
    private String textAnswer;

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

    public SurveyPassing getSurveyPassing() {
        return surveyPassing;
    }

    public void setSurveyPassing(SurveyPassing surveyPassing) {
        this.surveyPassing = surveyPassing;
    }

    public SurveyAnswerVariant getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant(SurveyAnswerVariant answerVariant) {
        this.answerVariant = answerVariant;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }
}
