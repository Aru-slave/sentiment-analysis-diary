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
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final MemberService memberService;
    private final ChatgptService chatgptService;

    // 다이어리 생성
    public Diary createDiary(DiaryDto.Post requestBody) {
        Member member = memberService.getLoginMember(); //로그인 한 상태가 아닐 시 에러 메시지 출력
        if (member == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        }
        Diary diary = new Diary();
        diary.setEmotion(findEmotion(requestBody.getContent())); // 감정점수 분석
        diary.setKeywords(findKeywords(requestBody.getContent())); // 키워드 분석
        diary.setMember(member);
        diary.setCreatedAt(LocalDate.parse(requestBody.getCreatedAt()));
        member.addDiary(diary);

        return diaryRepository.save(diary);
    }
//    public Diary analyzeDiary(long diaryId) {
//        Diary diary = findDiary(diaryId);
//        diary.setEmotion(findEmotion(diary.getContent())); // 감정점수 분석
//        diary.setKeywords(findKeywords(diary.getContent())); // 키워드 분석
//        return diaryRepository.save(diary);
//    }


    public Diary updateDiary(Diary diary) {
        if (memberService.getLoginMember() == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        } else if (Objects.equals(findVerifyDiary(diary.getDiaryId()).getMember().getMemberId(), memberService.getLoginMember().getMemberId())) {
            Diary findDiary = findVerifyDiary(diary.getDiaryId());
            findDiary.setModifiedAt(LocalDate.now());
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

    // 멤버별 다이어리 조회
    public Page<Diary> findDiaries(int page, int size) {
        Member member = memberService.getLoginMember(); //로그인 한 상태가 아닐 시 에러 메시지 출력
        if (member == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        }

        return diaryRepository.findByMember(member, PageRequest.of(page, size, Sort.by("diaryId").descending()));
    }

    // 날짜 및 멤버별 다이어리 조회
    public List<Diary> findDiaries(LocalDate createdAt) {
        Member member = memberService.getLoginMember(); //로그인 한 상태가 아닐 시 에러 메시지 출력
        if (member == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        }

        List<Diary> diaries = diaryRepository.findByMember(member);
        for(Diary diary : diaries) {
            if(diary.getCreatedAt().getDayOfMonth() != createdAt.getDayOfMonth()) diaries.remove(diary);
            else if(diary.getCreatedAt().getMonth() != createdAt.getMonth()) diaries.remove(diary);
            else if(diary.getCreatedAt().getYear() != createdAt.getYear()) diaries.remove(diary);
        }

        return diaries;
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
        String[] str = chatgptService.sendMessage(question).split("카운팅:\n\n")[1].split("\n");
        for(int i=0; i<str.length; i++) {
            String[] tmp = str[i].split(": ");
            keywords.put(tmp[0], Integer.parseInt(tmp[1]));
        }

        return keywords;
    }

    // 감정점수
    public int findEmotion(String question) {
        question = "\"" + question + "\" -10 ~ +10 사이로 감정점수화";
        return  (int) Arrays.stream(chatgptService.sendMessage(question).split("\n")[2].split(", "))
                .mapToInt(Integer::parseInt)
                .average()
                .getAsDouble();
    }
}
