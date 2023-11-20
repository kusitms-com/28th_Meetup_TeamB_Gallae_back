package kusitms.gallae.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kusitms.gallae.config.BaseResponse;
import kusitms.gallae.config.BaseResponseStatus;
import kusitms.gallae.domain.Archive;
import kusitms.gallae.domain.Review;
import kusitms.gallae.dto.archive.*;
import kusitms.gallae.dto.review.ReviewDtoRes;
import kusitms.gallae.global.S3Service;
import kusitms.gallae.service.archive.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archives")
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private S3Service s3Service;

    @Operation(summary = "자료실 카테고리별 게시판 내용들 가져오기", description = """
            전체는 null로 보내주세요 
            """)
    @GetMapping("/category")
    public ResponseEntity<BaseResponse<ArchivePageRes>> getArchivesByCategory(
            @Parameter(description = "지원서 / 보고서")
            @RequestParam(value = "category", required = false)
            String category,

            @Parameter(description = "페이지 번호")
            @Positive(message = "must be greater than 0")
            @RequestParam(value = "page", defaultValue = "0")
            Integer pageNumber,

            @Parameter(description = "페이징 사이즈 (최대 100)")
            @Min(value = 1, message = "must be greater than or equal to 1")
            @Max(value = 100, message = "must be less than or equal to 100")
            @RequestParam(value = "size", defaultValue = "20")
            Integer pagingSize){

        PageRequest pageRequest = PageRequest.of(pageNumber,pagingSize);
        return ResponseEntity.ok(new BaseResponse<>(this.archiveService.getArchivesByCategory(category,pageRequest)));
    }

    @PostMapping("/saveArchive")
    public ResponseEntity<BaseResponse<Long>> saveArchive(
            Principal principal,
            @ModelAttribute
            ArchiveModel archiveModel
    ) throws IOException {
        String fileName = null;
        String fileUrl = null;
        if(archiveModel.getFile() != null && !archiveModel.getFile().isEmpty()) {
            fileName = archiveModel.getFile().getOriginalFilename();
            fileUrl = s3Service.upload(archiveModel.getFile());
        }
        ArchivePostReq archivePostReq = new ArchivePostReq();
        archivePostReq.setTitle(archiveModel.getTitle());
        archivePostReq.setCategory(archiveModel.getCategory());
        archivePostReq.setFileUrl(fileUrl);
        archivePostReq.setFileName(fileName);
        archivePostReq.setWriter(archiveModel.getWriter());
        archivePostReq.setBody(archiveModel.getBody());
        archivePostReq.setHashTags(archiveModel.getHashTags());
        return ResponseEntity.ok(new BaseResponse<>(archiveService.postArchive(archivePostReq,principal.getName())));

    }

    @Operation(summary = "자료실 정보 가져오기", description = """
            포인트가 부족하면
            \n
            "isSuccess": false,\n
            "code": 1001,\n
            "message": "포인트가 부족합니다."\n
            이 반환합니다. 
            \n
            로그인한 유저는 포인트 15포인트 차감되고 포인트 컬럼 생성됩니다.\n
            *주의* 자료실 작성한 유저가 열람 할때는 포인트 차감이 되지 않습니다.
       
            """)
    @GetMapping("/detail")
    public ResponseEntity<BaseResponse<ArchiveDetailRes>> getArchiveDetail(
            Principal principal,

            @Parameter(description = "자료실아이디")
            @RequestParam(value = "archiveId", required = true)
            Long archiveId
    ) {
        String username = null;
        if(principal != null) {
            username = principal.getName();
        }
        ArchiveDetailRes archiveDetail = archiveService.getArchiveById(archiveId, username);
        return ResponseEntity.ok(new BaseResponse<>(archiveDetail));
    }


    @Operation(summary = "자료실 좋아요순으로 게시판 내용들 가져오기")
    @GetMapping("/sorted/likes")
    public ResponseEntity<BaseResponse<List<ArchiveDtoRes>>> getAllReviewsSortedByLikes(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likes"));
        Page<Archive> archivePage = archiveService.getAllArchivesSortedByLikes(pageRequest);
        List<ArchiveDtoRes> archiveDtos = archivePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        BaseResponse<List<ArchiveDtoRes>> response = new BaseResponse<>(
                true,
                "Success",
                archivePage.getNumber(),
                archiveDtos
        );

        return ResponseEntity.ok(response);
    }

    // Review 엔티티를 ReviewDtoRes로 변환하는 메소드
    private ArchiveDtoRes convertToDto(Archive archive) {
        ArchiveDtoRes dto = new ArchiveDtoRes();
        dto.setId(archive.getId());
        dto.setCategory(archive.getCategory());
        dto.setTitle(archive.getTitle());
        dto.setWriter(archive.getWriter());
        dto.setCreatedDate(archive.getCreatedAt());
        return dto;
    }


}

