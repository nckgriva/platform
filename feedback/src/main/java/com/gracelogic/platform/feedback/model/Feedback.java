package com.gracelogic.platform.feedback.model;

import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "FEEDBACK")
public class Feedback extends IdObject<UUID> {

    @Id
    @Access(AccessType.PROPERTY)
    @Column(name = ID)
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
    @JoinColumn(name = "FEEDBACK_TYPE_ID", nullable = false)
    private FeedbackType feedbackType;

//    @Type(type = "stringJsonObject")
    @Column(columnDefinition = "json", nullable = true)
    private String fields;

    @Override
    public UUID getId() { return id; }

    @Override
    public void setId(UUID id) { this.id = id; }

    @Override
    public Date getCreated() { return created; }

    @Override
    public void setCreated(Date created) { this.created = created; }

    @Override
    public Date getChanged() { return changed; }

    @Override
    public void setChanged(Date changed) { this.changed = changed; }

    public FeedbackType getFeedbackType() { return feedbackType; }

    public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }

    public String getFields() { return fields; }

    public void setFields(String fields) { this.fields = fields; }
}
