package com.board.board.member.repository.entity;

import com.board.board.global.entity.BaseTimeEntity;
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
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    private String email;

    private String password;

    private String name;

    private String nickname;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Authority authority;
}
