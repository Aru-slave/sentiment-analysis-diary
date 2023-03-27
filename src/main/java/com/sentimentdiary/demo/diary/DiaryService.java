package com.sentimentdiary.demo.diary;

import com.sentimentdiary.demo.exception.BusinessLogicException;
import com.sentimentdiary.demo.exception.ExceptionCode;
import com.sentimentdiary.demo.member.Member;
import com.sentimentdiary.demo.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final MemberService memberService;

    public Diary createDiary(DiaryDto.Post diaryPost) {
        Diary diary = new Diary();
        Member member = memberService.getLoginMember(); //로그인 한 상태가 아닐 시 에러 메시지 출력
        if (member == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        }
        diary.setTitle(diaryPost.getTitle());
        diary.setContent(diaryPost.getContent());
        diary.setMember(member);

        member.addDiary(diary);
        return diaryRepository.save(diary);
    }

    public Diary updateDiary(Diary diary) {
        if (memberService.getLoginMember() == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        } else if (Objects.equals(findVerifyDiary(diary.getDiaryId()).getMember().getMemberId(), memberService.getLoginMember().getMemberId())) {
            Diary findDiary = findVerifyDiary(diary.getDiaryId());
            findDiary.setModifiedAt(LocalDateTime.now());
            Optional.ofNullable(diary.getTitle())
                    .ifPresent(findDiary::setTitle);
            Optional.ofNullable(diary.getContent())
                    .ifPresent(findDiary::setContent);

            return diaryRepository.save(findDiary);
        } else throw new BusinessLogicException(ExceptionCode.PERMISSION_DENIED);
    }

    public void deleteDiary(Long diaryId) {
        if (memberService.getLoginMember() == null)
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        Diary diary = findVerifyDiary(diaryId);
        if (Objects.equals(diary.getMember().getMemberId(), memberService.getLoginMember().getMemberId()))
            diaryRepository.delete(diary);
        else throw new BusinessLogicException(ExceptionCode.PERMISSION_DENIED);
    }

    public Diary findDiary(long diaryId) {
        return findVerifyDiary(diaryId);
    }


    private Diary findVerifyDiary(long diaryId) {
        Optional<Diary> optionalDiary = diaryRepository.findById(diaryId);
        return optionalDiary.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.DIARY_NOT_FOUND));
    }
}
