package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.upload.UploadFileDTO;
import com.lyj.securitydomo.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequestMapping("/posting")
@RequiredArgsConstructor
public class PostController {

    @Value("${com.lyj.securitydomo.upload.path}")
    private String uploadPath;

    private final PostService postService;

    /**
     * 게시글 목록을 조회하고 모델에 전달하는 메서드
     */
    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<PostDTO> responseDTO = postService.list(pageRequestDTO);

        responseDTO.getDtoList().forEach(postDTO -> {
            log.info(postDTO.toString());
        });


        model.addAttribute("posts", responseDTO.getDtoList());
        return "posting/list";
    }

    /**
     * 게시글 등록 페이지를 보여주는 메서드
     */
    @GetMapping("/register")
    public void registerGET() {}

    /**
     * 게시글 등록을 처리하는 메서드
     */
    @PostMapping("/register")
    public String registerPost(UploadFileDTO uploadFileDTO, PostDTO postDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        List<String> fileNames = uploadFiles(uploadFileDTO);
        postDTO.setFileNames(fileNames);

        Long postId = postService.register(postDTO);
        redirectAttributes.addFlashAttribute("result", postId);

        return "redirect:/posting/list";
    }

    /**
     * 게시글 읽기 및 수정 페이지를 보여주는 메서드
     */
    @GetMapping({"/read", "/modify"})
    public void read(Long postId, PageRequestDTO pageRequestDTO, Model model) {
        PostDTO postDTO = postService.readOne(postId);
        log.info(postDTO);
        model.addAttribute("dto", postDTO);
    }

    /**
     * 게시글 수정 처리를 위한 메서드
     */
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO, UploadFileDTO uploadFileDTO, @Valid PostDTO postDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("postId", postDTO.getPostId());
            return "redirect:/posting/modify" + pageRequestDTO.getLink();
        }

        List<String> fileNames = uploadFiles(uploadFileDTO);
        postDTO.setFileNames(fileNames);

        postService.modify(postDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addFlashAttribute("postId", postDTO.getPostId());

        return "redirect:/posting/read";
    }

    /**
     * 게시글 삭제 처리를 위한 메서드
     */
    @PostMapping("/remove")
    public String remove(Long postId, RedirectAttributes redirectAttributes) {
        postService.remove(postId);
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/posting/list";
    }

    /**
     * 파일 업로드 처리 메서드
     * @param uploadFileDTO 업로드할 파일 정보
     * @return 파일 이름 리스트
     */
    private List<String> uploadFiles(UploadFileDTO uploadFileDTO) {
        List<String> fileNames = new ArrayList<>();

        if (uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach(file -> {
                String originalName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

                try {
                    file.transferTo(savePath);

                    // 이미지 파일일 경우 썸네일 생성
                    if (Files.probeContentType(savePath).startsWith("image")) {
                        File thumbnailFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 200, 200);
                    }
                    fileNames.add(uuid + "_" + originalName);
                } catch (IOException e) {
                    log.error("File upload error: {}", e.getMessage());
                }
            });
        }

        return fileNames;
    }

    /**
     * 파일을 뷰로 보여주는 메서드
     * @param fileName 파일 이름
     * @return 파일에 대한 ResponseEntity
     */
    @GetMapping("/view/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> viewFile(@PathVariable("fileName") String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}