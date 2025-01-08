package com.board.board.comment.repository.entity;

import com.board.board.board.repository.entity.Board;
import com.board.board.global.entity.BaseTimeEntity;
import com.board.board.member.repository.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "COMMENT_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    private String content;

    public void setContent(String content) {
        this.content = content;
    }
}
