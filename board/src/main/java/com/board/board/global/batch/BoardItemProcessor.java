package com.board.board.global.batch;

import com.board.board.board.repository.entity.Board;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BoardItemProcessor implements ItemProcessor<Board, Board> {

    @Override
    public Board process(Board board) {
        // TODO : 만약 board 엔티티의 데이터를 전처리하고 싶을 경우 수정하는 코드 입력
        return board;
    }
}