package com.sentimentdiary.demo.diary;

import com.sentimentdiary.demo.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.*;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary,Long> {
    Optional<Diary> findById(Long diaryId);
    Optional<Diary> findByCreatedAtAndMemberMemberId(LocalDate createdAt, long memberId);
    Page<Diary> findByMember(Member member, PageRequest pageRequest);

    List<Diary> findByMember(Member member);
}
