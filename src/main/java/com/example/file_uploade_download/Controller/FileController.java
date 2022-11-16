package com.example.file_uploade_download.Controller;

import com.example.file_uploade_download.Entity.FileByte;
import com.example.file_uploade_download.Entity.FileFolder;
import com.example.file_uploade_download.Entity.FileInfo;
import com.example.file_uploade_download.Repository.FileByteRepository;
import com.example.file_uploade_download.Repository.FileFolderRepository;
import com.example.file_uploade_download.Repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
public class FileController {
    @Autowired
    FileByteRepository fileByteRepository;
    @Autowired
    FileInfoRepository fileInfoRepository;
    @Autowired
    FileFolderRepository fileFolderRepository;

    @RequestMapping(value = "/FaylYuklash",method = RequestMethod.POST)
    public String FaylYuklash(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> iterator = request.getFileNames();
        //Fayl barcha ma'lumotlarini olib kelish
        MultipartFile multipartFile = request.getFile(iterator.next());
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFaylNomi(multipartFile.getOriginalFilename());
        fileInfo.setType(multipartFile.getContentType());
        fileInfo.setFaylHajmi(multipartFile.getSize());
        fileInfoRepository.save(fileInfo);
        FileByte fileByte = new FileByte();
        fileByte.setFaylBayt(multipartFile.getBytes());
        fileByte.setFileInfo(fileInfo);
        fileByteRepository.save(fileByte);
        return "Fayl saqlandi";
    }

    @RequestMapping(value = "/FaylO'qish/{id}",method = RequestMethod.GET)
    public void FaylUqish(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<FileInfo> byId = fileInfoRepository.findById(id);
        if (byId.isPresent()){
            FileInfo fileInfo = byId.get();
            Optional<FileByte> byId1 = fileByteRepository.findById(fileInfo.getId());
            if (byId1.isPresent()){
                FileByte fileByte = byId1.get();
                response.setContentType(fileInfo.getType());
                response.setHeader("Content-Disposition","attachment; filename=\""+ fileInfo.getFaylNomi()+"\"");
                FileCopyUtils.copy(fileByte.getFaylBayt(),response.getOutputStream());
            }
        }
    }

//    @RequestMapping(value = "/FaylTahrirlash/{id}",method = RequestMethod.PUT)
//    public String FaylTahrir(@PathVariable Integer id){
//        Optional<FileInfo> byId = fileInfoRepository.findById(id);
//        if ()
//    }


////////////////////////    Ma'lumotlarni papkaga saqlash       \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


    String manzil = "src\\main\\Base\\";
    @RequestMapping(value = "/FaylPapkaJoylash",method = RequestMethod.POST)
    public void PapkaFaylJoy(MultipartHttpServletRequest request) throws IOException {
       try {
           Iterator<String> iterator = request.getFileNames();
           MultipartFile multipartFile = request.getFile(iterator.next());
           FileFolder fileFolder = new FileFolder();
           fileFolder.setFaylOrginalName(multipartFile.getOriginalFilename());
           fileFolder.setFaylHajmi(multipartFile.getSize());
           fileFolder.setFaylTuri(multipartFile.getContentType());
           String[] ajratish = multipartFile.getOriginalFilename().split("\\.");
           String yangiNom = UUID.randomUUID().toString() + "." +ajratish[ajratish.length-1];
           fileFolder.setFaylYangiNomi(yangiNom);
           Path path = Paths.get(manzil + yangiNom);
           Files.copy(multipartFile.getInputStream(),path);
           fileFolderRepository.save(fileFolder);
       }catch (Exception e){
           e.getStackTrace();
           System.out.println(e.toString());
       }
    }
    
    @RequestMapping(value = "/FileDownload/{id}",method = RequestMethod.GET)
    public void FaylYuklash(@PathVariable Integer id,HttpServletResponse response) throws IOException {
        Optional<FileFolder> byId = fileFolderRepository.findById(id);
        if (byId.isPresent()){
            FileFolder fileFolder = byId.get();
            response.setContentType(fileFolder.getFaylTuri());
//            response.setHeader("Content-Disposition","attachment; filename=\""+ fileFolder.getFaylOrginalName()+"\"");
            response.setHeader("Content-Disposition","inline; filename=\""+ fileFolder.getFaylOrginalName()+"\"");
            FileInputStream fileInputStream = new FileInputStream(manzil + fileFolder.getFaylYangiNomi());
            FileCopyUtils.copy(fileInputStream,response.getOutputStream());
            System.out.println("Fayl yuklandi");
        }
    }
}
