package com.board.board.global.batch;

import com.board.board.board.repository.BoardRepository;
import com.board.board.board.repository.entity.Board;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoardItemWriter implements ItemWriter<Board> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends Board> chunk) {
        for (Board board : chunk) {
            entityManager.persist(board);
        }
        entityManager.flush();
        entityManager.clear();
    }
}