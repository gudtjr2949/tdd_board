package com.board.board.member.repository;

import com.board.board.member.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByEmail(String email);

    Optional<Member> findMemberByPhone(String phone);

    Optional<Member> findMemberByNickname(String nickname);

    Optional<Member> findMemberByEmailAndPassword(String email, String password);

    Optional<Member> findMemberByNameAndPhone(String name, String phone);

    Optional<Member> findMemberByNameAndEmailAndPhone(String name, String email, String phone);

    Optional<Member> findMemberByNameOrEmailOrPhone(String name, String email, String phone);
}
