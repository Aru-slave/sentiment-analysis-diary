package com.sentimentdiary.demo.diary;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiaryMapper {

    default Diary DiaryPatchToDiary(DiaryDto.Patch diaryPatchDto) {
        Diary diary = new Diary();
        diary.setDiaryId(diaryPatchDto.getDiaryId());
        diary.setTitle(diaryPatchDto.getTitle());
        diary.setContent(diaryPatchDto.getContent());
        return diary;
    }

    default DiaryDto.Response DiaryToDiaryResponseDto(Diary diary) {
        DiaryDto.Response response = new DiaryDto.Response();
        response.setDiaryId(diary.getDiaryId());
        response.setMemberId(diary.getMember().getMemberId());
        response.setNickName(diary.getMember().getNickName());
        response.setTitle(diary.getTitle());
        response.setContent(diary.getContent());
        response.setCreateDate(diary.getCreateDate());

        return response;
    }

    default List<DiaryDto.Response> DiariesToDiaryResponseDtos(List<Diary> diaries) {
        if (diaries == null) {
            return null;
        }

        List<DiaryDto.Response> list = new ArrayList<>();
        for (Diary diary : diaries) {
            list.add(DiaryToDiaryResponseDto(diary));
        }
        return list;
    }
}
