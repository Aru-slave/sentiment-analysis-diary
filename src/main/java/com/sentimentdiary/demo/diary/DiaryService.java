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
        diary.setModifiedAt(LocalDate.now());
        diary.setContent(requestBody.getContent());
        diary.setTitle(requestBody.getTitle());
        member.addDiary(diary);

        return diaryRepository.save(diary);
    }
    public DiaryDto.Response createDiary2(Diary diary) {
        DiaryDto.Response response = new DiaryDto.Response();
        response.setTitle(diary.getTitle());
        response.setContent(diary.getContent());
        response.setEmotion(findEmotion(diary.getContent())); // 감정점수 분석
        response.setKeywords(findKeywords(diary.getContent())); // 키워드 분석

        return response;
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
    public Diary findDiary(LocalDate createdAt) {
        Member member = memberService.getLoginMember(); //로그인 한 상태가 아닐 시 에러 메시지 출력
        if (member == null) {
            throw new BusinessLogicException(ExceptionCode.NOT_LOGIN);
        }
        return diaryRepository.findByCreatedAtAndMemberMemberId(createdAt, member.getMemberId()).get();
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
    public List<String> findKeywords(String question) {
        question = "\"" + question + "\" 문단에 포함되어 있는 단어들 중 핵심 키워드라고 판단되는 단어들을 추출해줘, 단어는 조사를 뺀 사전에 등재되어 있는 명사만 해당돼.";
        String result = chatgptService.sendMessage(question);
        String[] str = result.contains("\n\n") ?
                result.split("\n\n")[1].split(",") : result.split(",");
        List<String> list = new ArrayList<>();
        for(int i = 0; i < str.length; i++){
            list.add(str[i].replace(" ",""));
        }

        return list;
    }

    // 감정점수
    public int findEmotion(String question) {
        question = "\"" + question + "\" 문단 전체를 -10 ~ +10 사이로 감정점수화해서 점수만 알려줘";
        String result = chatgptService.sendMessage(question);
        result = result.replaceAll("[^\\d+-]", "").replaceAll("\\r|\\n", "");




        return Integer.parseInt(result);

    }
}
