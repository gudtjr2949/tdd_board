package com.board.board.global.batch;

import com.board.board.board.repository.entity.Board;
import com.board.board.member.repository.MemberRepository;
import com.board.board.member.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardItemReader implements ItemReader<Board> {

    private long count = 0;
    private static final long LIMIT = 1_000_000;
    private final MemberRepository memberRepository;

    @Override
    public Board read() {
        if (count >= LIMIT) {
            return null; // 데이터 끝
        }

        Member member = memberRepository.findById(1L).get();
        count++;

        return Board.builder()
                .member(member)
                .title("Title " + count)
                .content("Content " + count)
                .views(0L)
                .build();
    }
}
