package com.lyj.securitydomo.controller;

import com.lyj.securitydomo.dto.PageRequestDTO;
import com.lyj.securitydomo.dto.PageResponseDTO;
import com.lyj.securitydomo.dto.PostDTO;
import com.lyj.securitydomo.dto.upload.UploadFileDTO;
import com.lyj.securitydomo.service.PostService;
import groovy.util.logging.Log4j2;
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
import java.util.UUID;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Controller
@Log4j2
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    @Value("${com.lyj.securitydomo.upload.path}")
    private String uploadPath;

    private final PostService postService;

    @GetMapping("/list1")
    public void list1(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<PostDTO> responseDTO = postService.list(pageRequestDTO);
        log.info(responseDTO);

        // 게시글 리스트를 ""라는 이름으로 모델에 추가
        model.addAttribute("lists", responseDTO.getDtoList());
    }

    @GetMapping("/register1") //등록
    public void register1GET(){

    }
    @PostMapping("/register1") //등록
    public String register1Post(UploadFileDTO uploadFileDTO,
                                PostDTO postDTO, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        List<String> strFileNames=null;
        if(uploadFileDTO.getFiles()!=null &&
                !uploadFileDTO.getFiles().get(0).getOriginalFilename().equals("") ) {
            strFileNames =fileUpload (uploadFileDTO);
            log.info("!!!!!!!!!!!!!!!!"+strFileNames.size());
        }
        postDTO.setFileNames(strFileNames);
        log.info("board POST register.......");
//        if (bindingResult.hasErrors()) {
//            log.info("has errors.......");
//            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
//            return "redirect:/post/register1";
//        }

        log.info(postDTO);
        Long postId = postService.register(postDTO);
        redirectAttributes.addFlashAttribute("result", postId);
        return "redirect:/post/list1";
    }
//     @GetMapping("/read1") //조회처리
//        public void read1(Long postId, PageRequestDTO pageRequestDTO, Model model) {
//            PostDTO postDTO = postService.readOne(postId);
//            log.info(postDTO);
//            model.addAttribute("dto", postDTO);
//     }

    @GetMapping({"/read1","/modify1"}) //게시물의 수정/삭제 처리
    public void read(Long postId, PageRequestDTO pageRequestDTO, Model model) {
        PostDTO postDTO = postService.readOne(postId);
        log.info(postDTO);
        model.addAttribute("dto", postDTO);
    }

    @PostMapping("/modify1") //수정처리
    public String modify1 (PageRequestDTO pageRequestDTO,
                           UploadFileDTO uploadFileDTO,
                           @Valid PostDTO postDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        log.info("post modify1 post....." + postDTO);

        List<String> strFileNames=null;
        if(uploadFileDTO.getFiles()!=null &&
                !uploadFileDTO.getFiles().get(0).getOriginalFilename().equals("") ){

            List<String> fileNames = postDTO.getFileNames();

            if(fileNames != null && fileNames.size() > 0){
                removeFile(fileNames);
            }

            strFileNames=fileUpload(uploadFileDTO);
            log.info("!!!!!!!!!!!!!!!!"+strFileNames.size());
            postDTO.setFileNames(strFileNames);
        }
        if (bindingResult.hasErrors()) {
            log.info("has errors.......");
            String link = pageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("postId", postDTO.getPostId());
            return "redirect:/post/modify1"+link;
        }

        postService.modify(postDTO);
        redirectAttributes.addFlashAttribute("result","modified");
        redirectAttributes.addFlashAttribute("postId", postDTO.getPostId());
        return "redirect:/post/read1";
    }

    @PostMapping("/remove1") // 삭제처리
    public String remove1(Long postId, RedirectAttributes redirectAttributes) {
        log.info("post remove1 post....." + postId);

        postService.remove(postId);
        redirectAttributes.addFlashAttribute("result","removed");
        return "redirect:/post/list1";
    }

    private List<String> fileUpload(UploadFileDTO uploadFileDTO){

        List<String> list = new ArrayList<>();
        uploadFileDTO.getFiles().forEach(multipartFile -> {
            String originalName = multipartFile.getOriginalFilename();
            log.info(originalName);

            String uuid = UUID.randomUUID().toString();
            Path savePath = Paths.get(uploadPath, uuid+"_"+ originalName);
            boolean image = false;
            try {
                multipartFile.transferTo(savePath); // 서버에 파일저장
                //이미지 파일의 종류라면
                if(Files.probeContentType(savePath).startsWith("image")){
                    image = true;
                    File thumbFile = new File(uploadPath, "s_" + uuid+"_"+ originalName);
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            list.add(uuid+"_"+originalName);
        });
        return list;
    }

    @GetMapping("/view/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> viewFileGet(@PathVariable("fileName") String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type", Files.probeContentType( resource.getFile().toPath() ));
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    private void removeFile(List<String> fileNames){
        log.info("AAAAA"+fileNames.size());

        for(String fileName:fileNames){
            log.info("fileRemove method: "+fileName);
            Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);
            String resourceName = resource.getFilename();

            // Map<String, Boolean> resultMap = new HashMap<>();
            boolean removed = false;

            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                removed = resource.getFile().delete();

                //섬네일이 존재한다면
                if(contentType.startsWith("image")){
                    String fileName1=fileName.replace("s_","");
                    File originalFile = new File(uploadPath+File.separator + fileName1);
                    originalFile.delete();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}