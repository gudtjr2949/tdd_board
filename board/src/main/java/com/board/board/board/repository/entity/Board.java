package com.board.board.board.repository.entity;

import com.board.board.comment.repository.entity.Comment;
import com.board.board.global.entity.BaseTimeEntity;
import com.board.board.member.repository.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@Getter
@ToString
@Builder
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "BOARD_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    private List<Comment> comments;

    private String title;

    private String content;

    private Long views;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void increaseViews() {
        this.views++;
    }
}
