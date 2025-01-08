package com.board.board.member.repository.entity;

import com.board.board.global.entity.BaseTimeEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
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
public class Verification extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "VERIFICATION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private String code;
}
