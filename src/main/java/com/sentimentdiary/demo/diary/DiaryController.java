package com.sentimentdiary.demo.diary;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/diary")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;
    private final DiaryMapper diaryMapper;
    @PostMapping
    public ResponseEntity postDiary(@Valid @RequestBody DiaryDto.Post requestBody) {
        Diary diary = diaryService.createDiary(requestBody);
        return ResponseEntity.ok(diaryMapper.DiaryToDiaryResponseDto(diary));
    }
    @PatchMapping
    public ResponseEntity patchDiary(@Valid @RequestBody DiaryDto.Patch requestBody) {
        Diary diary = diaryService.updateDiary(diaryMapper.DiaryPatchToDiary(requestBody));
        return ResponseEntity.ok(diaryMapper.DiaryToDiaryResponseDto(diary));
    }
}
