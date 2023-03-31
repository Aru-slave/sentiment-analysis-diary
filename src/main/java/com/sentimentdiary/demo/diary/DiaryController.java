package com.sentimentdiary.demo.diary;


import com.sentimentdiary.demo.dto.MultiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("api/diary")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;
    private final DiaryMapper diaryMapper;

    // 다이어리 생성
    @PostMapping
    public ResponseEntity postDiary(@RequestBody DiaryDto.Post post) {
        Diary diary = diaryMapper.diaryPostDtoToDiary(post);
        diary = diaryService.createDiary(diary);
        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diary);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    // 다이어리 단일조회
    @GetMapping("/{diary-id}")
    public ResponseEntity getDiary(@PathVariable("diary-id") @Positive long diaryId) {
        Diary diary = diaryService.findDiary(diaryId);
        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diary);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    // 다이어리 부분조회
    @GetMapping
    public ResponseEntity getDiaries(@RequestParam int page,
                                    @RequestParam int size) {
        Page<Diary> pageDiaries = diaryService.findDiaries(page-1, size);
        List<Diary> diaries = pageDiaries.getContent();
        List<DiaryDto.Response> response = diaryMapper.diariesToStudyResponseDto(diaries);

        return new ResponseEntity(new MultiResponseDto<>(response, pageDiaries), HttpStatus.OK);
    }

    // 다리어리 수정
    @PatchMapping("/{diary-id}")
    public ResponseEntity deleteDiary(@PathVariable("diary-id") @Positive long diaryId,
                                      @RequestBody DiaryDto.Patch patch) {
        Diary diary = diaryMapper.diaryPatchDtoToDiary(patch);
        diary = diaryService.updateDiary(diary);
        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diary);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    // 다이어리 삭제
    @DeleteMapping("/{diary-id}")
    public ResponseEntity deleteDiary(@PathVariable("diary-id") @Positive long diaryId) {
        diaryService.deleteDiary(diaryId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
