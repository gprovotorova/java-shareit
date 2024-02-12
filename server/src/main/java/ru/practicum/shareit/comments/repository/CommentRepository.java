package ru.practicum.shareit.comments.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "comment_entity-graph")
    List<Comment> getByItem_IdOrderByCreatedDesc(Long id);
}
