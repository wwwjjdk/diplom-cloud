package com.example.refactordip.controller;

import com.example.refactordip.Service.MyService;

import com.example.refactordip.model.MyFile;
import com.example.refactordip.model.NewFileName;
import com.example.refactordip.pojo.ReqJwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@CrossOrigin
public class MyController {
    @Autowired
    MyService myService;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody ReqJwt reqJwt) {

        log.info("login request");

        return ResponseEntity.ok(myService.login(reqJwt));
    }


 @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String auth) {

        log.info("logout request");

        myService.logout(auth);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/file",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postFile(
            @RequestHeader("auth-token") String auth,
            @RequestParam("filename") String filename, @RequestBody MultipartFile file) {

        log.info("Upload file to server");

        myService.postFile(auth, filename, file);
        return ResponseEntity.ok("Success upload");
    }

    @DeleteMapping(value = "/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String auth, @RequestParam("filename") String filename) {

        log.info("delete request");

        myService.deleteFile(auth, filename);
        return ResponseEntity.ok("Success delete");
    }

    @PutMapping("/file")
    public ResponseEntity<?> putFile(@RequestHeader("auth-token") String auth,
                                     @RequestParam("filename") String filename, @RequestBody NewFileName newFileName) {

        log.info("put request");

        myService.putFile(auth, filename, newFileName.getFilename());
        return ResponseEntity.ok("Success upload");
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllFiles(@RequestHeader("auth-token") String auth, @RequestParam("limit") int limit) {

        log.info("getAllFilesInfo request");

        Page<MyFile> myFiles = myService.getAllFileInfo(auth, limit);

        return ResponseEntity.ok(myFiles.getContent());
    }

    @GetMapping(value = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getFile(@RequestHeader("auth-token") String auth, @RequestParam("filename") String filename) {

        log.info("getFile request");

        return ResponseEntity.ok(myService.getFile(auth, filename));
    }

}
