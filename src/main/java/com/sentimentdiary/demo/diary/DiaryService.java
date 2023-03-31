package com.sentimentdiary.demo.diary;

import com.sentimentdiary.demo.exception.BusinessLogicException;
import com.sentimentdiary.demo.exception.ExceptionCode;
import com.sentimentdiary.demo.member.Member;
import com.sentimentdiary.demo.member.MemberService;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final MemberService memberService;
    private final ChatgptService chatgptService;

    // 다이어리 생성
    public Diary createDiary(Diary diary) {
        Member member = memberService.getLoginMember(); //로그인 한 상태가 아닐 시 에러 메시지 출력
        if (member == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        }
        diary.setMember(member);
        diary.setEmotion(findEmotion(diary.getContent())); // 감정점수
        diary.setKeywords(findKeywords(diary.getContent())); // 키워드
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

    public Page<Diary> findDiaries(int page, int size) {
        return diaryRepository.findAll(PageRequest.of(page, size, Sort.by("diaryId").descending()));
    }
    public Diary findDiary(long diaryId) {
        return findVerifyDiary(diaryId);
    }


    private Diary findVerifyDiary(long diaryId) {
        Optional<Diary> optionalDiary = diaryRepository.findById(diaryId);
        return optionalDiary.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.DIARY_NOT_FOUND));
    }

    // 키워드 추출하기
    public Map<String, Integer> findKeywords(String question) {
        question = "\"" + question + "\" 키워드 추출 및 카운팅해줘";
        Map<String, Integer> keywords = new HashMap<>();
        String[] str = question.split(": ")[2].split(" ");
        for(int i=0; i<str.length; i++) {
            keywords.put(str[i++], Integer.parseInt(str[i]));
        }

        return keywords;
    }

    // 감정점수
    public int findEmotion(String question) {
        question = "\"" + question + "\" -10 ~ +10 사이로 감정점수화";
        question = question.split(" 하면 ")[1];
        return Integer.parseInt(chatgptService.sendMessage(question));
    }
}
