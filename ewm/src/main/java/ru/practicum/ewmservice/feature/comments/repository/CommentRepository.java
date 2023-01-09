package ru.practicum.ewmservice.feature.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmservice.feature.comments.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {


    Page<Comment> findCommentByEvent_Id(Long eventID, Pageable pageable);

    Comment findCommentByIdAndCommentatorId(Long commentId, Long commentatorId);

    @Query(value = "select new ru.practicum.ewmservice.feature.comments.model.Comment(c) " +
            "FROM Comment AS c " +
            "WHERE c.event.id = ?1 AND c.commentStatus = 'PUBLISHED' ")
    Page<Comment> findCommentByEventIdAndStatusPublished(Long eventId, Pageable pageable);

}
