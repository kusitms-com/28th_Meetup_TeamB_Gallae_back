package kusitms.gallae.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kusitms.gallae.config.BaseResponse;
import kusitms.gallae.config.BaseResponseStatus;
import kusitms.gallae.domain.Archive;
import kusitms.gallae.dto.archive.*;
import kusitms.gallae.global.S3Service;
import kusitms.gallae.service.archive.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

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

            @Parameter(description = "타이틀 검색")
            @RequestParam(value = "title", required = false) String title,

            @Parameter(description = "페이지 번호")
            @Positive(message = "must be greater than 0")
            @RequestParam(value = "page", defaultValue = "0")
            Integer pageNumber,

            @Parameter(description = "페이징 사이즈 (최대 100)")
            @Min(value = 1, message = "must be greater than or equal to 1")
            @Max(value = 100, message = "must be less than or equal to 100")
            @RequestParam(value = "size", defaultValue = "20")
            Integer pagingSize){

        PageRequest pageRequest = PageRequest.of(pageNumber, pagingSize);
        ArchivePageRes archivePageRes = archiveService.getArchivesByCategoryAndTitle(category, title, pageRequest);
        return ResponseEntity.ok(new BaseResponse<>(archivePageRes));
    }

    @PostMapping("/saveArchive")
    public ResponseEntity<BaseResponse> saveArchive(
            Principal principal,
            @ModelAttribute
            ArchiveModel archiveModel
    ) throws IOException {
        String fileName = null;
        String fileUrl = null;
        System.out.println(principal.getName());
        if(archiveModel.getFile() != null && !archiveModel.getFile().isEmpty()) {
            fileName = archiveModel.getFile().getName();
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
        archiveService.postArchive(archivePostReq,principal.getName());
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));

    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<BaseResponse<ArchiveDetailRes>> getArchiveDetail(@PathVariable Long id) {
        ArchiveDetailRes archiveDetail = archiveService.getArchiveById(id);
        {
            return ResponseEntity.ok(new BaseResponse<>(archiveDetail));

        }
    }


}

