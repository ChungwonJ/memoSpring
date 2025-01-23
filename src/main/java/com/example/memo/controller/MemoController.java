package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/memos")
public class MemoController {
    private final Map<Long, Memo> memoList = new HashMap<>();

    // 메모 생성 기능
    @PostMapping
    public ResponseEntity<MemoResponseDto> crateMemo(@RequestBody MemoRequestDto dto) {
        // 식별자가 1씩 증가 하도록 만듦
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;

        // 요청받은 데이터로 Memo 객체 생성
        Memo memo = new Memo(memoId, dto.getTitle(), dto.getContents());

        // Inmemory DB에 Memo 저장
        memoList.put(memoId, memo);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.CREATED);
    }

    // 메모 조회 기능
    @GetMapping("/{id}")
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {

        // 식별자의 Memo가 없다면?
        Memo memo = memoList.get(id);

        // NPE 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    //메모 수정
    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemo(
            @PathVariable Long id,
            @RequestBody MemoRequestDto memoRequestDto
    ) {

        Memo memo = memoList.get(id);

        // NPE 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 필수값 검증
        if (memoRequestDto.getTitle() == null || memoRequestDto.getContents() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // memo 수정
        memo.update(memoRequestDto);

        // 응답
        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    // 메모 일부 수정기능
    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle(
            @PathVariable Long id,
            @RequestBody MemoRequestDto requestDto
    ) {
        Memo memo = memoList.get(id);

        // NPE 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 필수값 검증
        if (requestDto.getTitle() == null || requestDto.getContents() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.updateTitle(requestDto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    // 메모 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

        // memoList의 Key값에 id를 포함하고 있다면
        if (memoList.containsKey(id)) {
            // key가 id인 value 삭제
            memoList.remove(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        // 포함하고 있지 않은 경우
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
