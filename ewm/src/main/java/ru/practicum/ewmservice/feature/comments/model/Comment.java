package ru.practicum.ewmservice.feature.comments.model;

import lombok.*;

import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "comments")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @CollectionTable(name = "users", joinColumns = @JoinColumn(name = "id"))
    private User commentator;

    @ManyToOne
    @CollectionTable(name = "event", joinColumns = @JoinColumn(name = "id"))
    private Event event;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "comment_changed")
    private Boolean commentChanged;

    @Column(name = "comment_status")
    @Enumerated(EnumType.STRING)
    private CommentStatus commentStatus;

    public Comment(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.commentator = comment.getCommentator();
        this.event = comment.getEvent();
        this.createdOn = comment.getCreatedOn();
        this.commentChanged = comment.getCommentChanged();
        this.commentStatus = comment.getCommentStatus();
    }
}
