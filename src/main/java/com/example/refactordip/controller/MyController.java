package com.example.refactordip.controller;

import com.example.refactordip.Service.MyService;
import com.example.refactordip.model.MyFile;
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
@RequestMapping("/cloud")
@Slf4j
public class MyController {
    @Autowired
    MyService myService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody ReqJwt reqJwt) {
        log.info("login request");
        return ResponseEntity.ok(myService.login(reqJwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth") String auth) {
        myService.logout(auth);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<?> postFile(
            @RequestHeader("auth") String auth,
            @RequestParam("fileName") String fileName, @RequestParam("file") MultipartFile file) {
        myService.postFile(auth, fileName, file);
        return ResponseEntity.ok("Success upload");
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth") String auth, @RequestParam("fileName") String filename) {
        myService.deleteFile(auth, filename);
        return ResponseEntity.ok("Success delete");
    }

    @PutMapping("/file")
    public ResponseEntity<?> putFile(@RequestHeader("auth") String auth,
                                     @RequestParam("oldName") String oldName, @RequestParam("newName") String newName) {
        myService.putFile(auth, oldName, newName);
        return ResponseEntity.ok("Success upload");
    }
    @GetMapping("/list")
    public ResponseEntity<Page<MyFile>> getAllFiles(@RequestHeader("auth") String auth, @RequestParam("limit") int limit ){
        Page<MyFile> myFiles =  myService.getAllFileInfo(auth, limit);
        return ResponseEntity.ok(myFiles);
    }

    @GetMapping(value = "/file",produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> getFile(@RequestHeader("auth") String auth, @RequestParam("fileName") String fileName){

        return ResponseEntity.ok(myService.getFile(auth,fileName));
    }

}
