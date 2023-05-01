package com.sentimentdiary.demo.diary;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiaryMapper {
    Diary diaryPostDtoToDiary(DiaryDto.Post post);
    List<DiaryDto.Response> diariesToStudyResponseDto(List<Diary> diaries);

    List<DiaryDto.diaryAnalysis> diariesToAnalysisResponseDto(List<Diary> diaries);

    default Diary diaryPatchDtoToDiary(DiaryDto.Patch diaryPatchDto) {
        Diary diary = new Diary();
        diary.setDiaryId(diaryPatchDto.getDiaryId());
        diary.setTitle(diaryPatchDto.getTitle());
        diary.setContent(diaryPatchDto.getContent());
        return diary;
    }

    default DiaryDto.Response diaryToDiaryResponseDto(Diary diary) {
        DiaryDto.Response response = new DiaryDto.Response();
        response.setDiaryId(diary.getDiaryId());
        response.setMemberId(diary.getMember().getMemberId());
        response.setNickName(diary.getMember().getNickName());
        response.setTitle(diary.getTitle());
        response.setEmotion(diary.getEmotion());
        response.setKeywords(diary.getKeywords());
        response.setContent(diary.getContent());
        response.setCreatedAt(diary.getCreatedAt());
        response.setModifiedAt(diary.getModifiedAt());

        return response;
    }

    default List<DiaryDto.Response> diariesToDiaryResponseDtos(List<Diary> diaries) {
        if (diaries == null) {
            return null;
        }

        List<DiaryDto.Response> list = new ArrayList<>();
        for (Diary diary : diaries) {
            list.add(diaryToDiaryResponseDto(diary));
        }
        return list;
    }
}
