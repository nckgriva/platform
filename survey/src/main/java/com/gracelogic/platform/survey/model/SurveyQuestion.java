package com.gracelogic.platform.survey.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "SURVEY_QUESTION")
public class SurveyQuestion extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @Access(AccessType.PROPERTY)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_PAGE_ID", nullable = false)
    private SurveyPage surveyPage;

    @Column(name = "QUESTION_INDEX", nullable = false)
    private Integer questionIndex;

    @Column(name = "TEXT", nullable = false, length = 4000)
    private String text;

    @Column(name = "DESCRIPTION", nullable = true, length = 4000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SURVEY_QUESTION_TYPE_ID", nullable = false)
    private SurveyQuestionType surveyQuestionType;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "CATALOG_ID", nullable = true)
    private SurveyAnswerVariantCatalog catalog;

    @Column(name = "IS_REQUIRED", nullable = false)
    private Boolean required;

    @Column(name = "IS_HIDDEN", nullable = false)
    private Boolean hidden;

    @Column(name = "SCALE_MIN_VALUE", nullable = true)
    private Long scaleMinValue;

    @Column(name = "SCALE_MAX_VALUE", nullable = true)
    private Long scaleMaxValue;

    @Column(name = "SCALE_MIN_VALUE_LABEL", nullable = true)
    private String scaleMinValueLabel;

    @Column(name = "SCALE_MAX_VALUE_LABEL", nullable = true)
    private String scaleMaxValueLabel;

    @Column(name = "SCALE_STEP_VALUE", nullable = true)
    private Integer scaleStepValue;

    @Column(name = "ATTACHMENT_EXTENSIONS", nullable = true, length = 4000)
    private String attachmentExtensions;

    @Column(name = "MATRIX_ROWS", nullable = true, length = 4000)
    private String[] matrixRows;

    @Column(name = "MATRIX_COLUMNS", nullable = true, length = 4000)
    private String[] matrixColumns;

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

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SurveyQuestionType getSurveyQuestionType() {
        return surveyQuestionType;
    }

    public void setSurveyQuestionType(SurveyQuestionType surveyQuestionType) {
        this.surveyQuestionType = surveyQuestionType;
    }

    public SurveyAnswerVariantCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(SurveyAnswerVariantCatalog catalog) {
        this.catalog = catalog;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Long getScaleMinValue() {
        return scaleMinValue;
    }

    public void setScaleMinValue(Long scaleMinValue) {
        this.scaleMinValue = scaleMinValue;
    }

    public Long getScaleMaxValue() {
        return scaleMaxValue;
    }

    public void setScaleMaxValue(Long scaleMaxValue) {
        this.scaleMaxValue = scaleMaxValue;
    }

    public String getAttachmentExtensions() {
        return attachmentExtensions;
    }

    public void setAttachmentExtensions(String attachmentExtensions) {
        this.attachmentExtensions = attachmentExtensions;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getMatrixRows() {
        return matrixRows;
    }

    public void setMatrixRows(String[] matrixRows) {
        this.matrixRows = matrixRows;
    }

    public String[] getMatrixColumns() {
        return matrixColumns;
    }

    public void setMatrixColumns(String[] matrixColumns) {
        this.matrixColumns = matrixColumns;
    }

    public String getScaleMinValueLabel() {
        return scaleMinValueLabel;
    }

    public void setScaleMinValueLabel(String scaleMinValueLabel) {
        this.scaleMinValueLabel = scaleMinValueLabel;
    }

    public String getScaleMaxValueLabel() {
        return scaleMaxValueLabel;
    }

    public void setScaleMaxValueLabel(String scaleMaxValueLabel) {
        this.scaleMaxValueLabel = scaleMaxValueLabel;
    }

    public Integer getScaleStepValue() {
        return scaleStepValue;
    }

    public void setScaleStepValue(Integer scaleStepValue) {
        this.scaleStepValue = scaleStepValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SurveyQuestion that = (SurveyQuestion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }

    @Override
    public String toString() {
        String surveyPageId = surveyPage != null ? surveyPage.getId().toString() : "null";
        String surveyQuestionTypeId = surveyQuestionType != null ? surveyQuestionType.getId().toString() : "null";
        String surveyMatrixRows = matrixRows != null ? Arrays.toString(matrixRows) : "null";
        String surveyMatrixColumns = matrixColumns != null ? Arrays.toString(matrixColumns) : "null";
        return "SurveyQuestion{" +
                "id=" + id +
                ", created=" + created +
                ", changed=" + changed +
                ", surveyPage=" + surveyPageId +
                ", questionIndex=" + questionIndex +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", surveyQuestionType=" + surveyQuestionTypeId +
                ", required=" + required +
                ", hidden=" + hidden +
                ", scaleMinValue=" + scaleMinValue +
                ", scaleMaxValue=" + scaleMaxValue +
                ", scaleMinValueLabel='" + scaleMinValueLabel + '\'' +
                ", scaleMaxValueLabel='" + scaleMaxValueLabel + '\'' +
                ", scaleStepValue=" + scaleStepValue +
                ", attachmentExtensions='" + attachmentExtensions + '\'' +
                ", matrixRows=" + surveyMatrixRows +
                ", matrixColumns=" + surveyMatrixColumns +
                '}';
    }
}
