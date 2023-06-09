package com.sentimentdiary.demo.diary;


import com.sentimentdiary.demo.dto.MultiResponseDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.MediaSize;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.WeekFields.ISO;

@RestController
@RequestMapping("api/diary")
@RequiredArgsConstructor
@ApiOperation(value = "다이어리 정보 API", tags = {"Dairy-Controller"}) // Swagger
//@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DiaryController {
    private final DiaryService diaryService;
    private final DiaryMapper diaryMapper;
    private final DiaryRepository diaryRepository;

    // 다이어리 생성
    @PostMapping
    @ApiOperation(value = "다이어리 생성", notes = "새로운 다이어리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "다이어리 생성 완료"),
            @ApiResponse(code = 402, message = "로그인 필요")
    })
    public ResponseEntity<DiaryDto.Response> postDiary(@RequestBody DiaryDto.Post post) {

        Diary diary = diaryService.createDiary(post);
        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diary);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    // 감정 및 키워드 분석 테스트 controller
//    @PostMapping("/test")
//    @ApiOperation(value = "다이어리 생성", notes = "새로운 다이어리를 생성합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(code = 201, message = "다이어리 생성 완료"),
//            @ApiResponse(code = 402, message = "로그인 필요")
//    })
//    public ResponseEntity<DiaryDto.Response> postDiary2(@RequestBody DiaryDto.Post post) {
//        Diary diary = diaryMapper.diaryPostDtoToDiary(post);
//        DiaryDto.Response response = diaryService.createDiary2(diary);
//
//        return new ResponseEntity(response, HttpStatus.CREATED);
//    }

    //감정, 키워드 분석
//    @GetMapping("/analyze/{diary-id}")
//    @ApiOperation(value = "감정, 키워드 분석", notes = "다이어리의 감정 및 키워드를 분석한 결과를 반환합니다.")
//    public ResponseEntity<DiaryDto.Response> analyzeDiary(
//            @ApiParam(name = "diary-id", value = "다이어리 식별자", required = true, example = "1")
//            @PathVariable("diary-id") @Positive long diaryId) {
//
//        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diaryService.analyzeDiary(diaryId));
//
//        return new ResponseEntity(response, HttpStatus.OK);
//    }

    // 다이어리 단일조회
    @GetMapping("/{diary-id}")
    @ApiOperation(value = "다이어리 단일 조회", notes = "다이어리 식별자에 대한 다이어리 정보를 반환합니다.")
    public ResponseEntity<DiaryDto.Response> getDiary(
            @ApiParam(name = "diary-id", value = "다이어리 식별자", required = true, example = "1")
            @PathVariable("diary-id") @Positive long diaryId) {
        Diary diary = diaryService.findDiary(diaryId);
        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diary);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    // 날짜 및 멤버별 다이어리 조회
    @GetMapping("/date")
    @ApiOperation(value = "날짜별 다이어리 단일 조회", notes = "날짜 및 멤버별 대한 다이어리들의 정보를 반환합니다.")
    public ResponseEntity<DiaryDto.Response> getDiariesByDate(
            @ApiParam(name = "createdAt", value = "날짜", required = true, example = "2023-04-15")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAt) {
        Diary diary = diaryService.findDiary(createdAt);
        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diary);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 멤버별 다이어리 부분조회
    @GetMapping
    @ApiOperation(value = "멤버별 다이어리 부분 조회", notes = "멤버별로 다이어리에 대한 정보를 페이지화해서 반환합니다.")
    public ResponseEntity<MultiResponseDto> getDiaries(
            @ApiParam(name = "page", value = "현재 페이지", required = true, example = "1")
            @RequestParam int page,
            @ApiParam(name = "size", value = "페이지 단위", required = true, example = "10")
            @RequestParam int size) {
        Page<Diary> pageDiaries = diaryService.findDiaries(page - 1, size);
        List<Diary> diaries = pageDiaries.getContent();
        List<DiaryDto.Response> response = diaryMapper.diariesToStudyResponseDto(diaries);

        return new ResponseEntity(new MultiResponseDto<>(response, pageDiaries), HttpStatus.OK);
    }

    // 다리어리 수정
    @PatchMapping("/{diary-id}")
    @ApiOperation(value = "다이어리 수정", notes = "다이어리를 수정합니다.")
    public ResponseEntity<DiaryDto.Response> patchDiary(
            @ApiParam(name = "diary-id", value = "다이어리 식별자", required = true, example = "1")
            @PathVariable("diary-id") @Positive long diaryId,
            @RequestBody DiaryDto.Patch patch) {
        Diary diary = diaryService.updateDiary(patch,diaryId);
        DiaryDto.Response response = diaryMapper.diaryToDiaryResponseDto(diary);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    // 다이어리 삭제
    @DeleteMapping("/{diary-id}")
    @ApiOperation(value = "다이어리 삭제", notes = "다이어리를 삭제합니다.")
    public ResponseEntity deleteDiary(
            @ApiParam(name = "diary-id", value = "다이어리 식별자", required = true, example = "1")
            @PathVariable("diary-id") @Positive long diaryId) {
        diaryService.deleteDiary(diaryId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //기간으로 다이어리 조회
    @GetMapping("/term")
    @ApiOperation(value = "기간내 다이어리 조회", notes = "기간내 로그인한 유저가 작성한 다이어리 데이터들과 키워드 등장 횟수를 반환합니다.")
    public ResponseEntity<Map<String, Object>> getDiariesWithTerm(
            @ApiParam(name = "startDate", value = "검색 시작 날짜", required = true, example = "2023-04-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam(name = "endDate", value = "검색 끝 날짜", required = true, example = "2023-04-30")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Diary> diaries = diaryService.findDiaryWithTerm(startDate,endDate);
        List<DiaryDto.diaryAnalysis> response = diaryMapper.diariesToAnalysisResponseDto(diaries);
        Map<String, Integer> keyWords = diaryService.findDiaryKeyWordsWithTerm(diaries);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("diaries", response);
        resultMap.put("keyWords", keyWords);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
}
