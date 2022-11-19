package com.example.refactordip.Service;

import com.example.refactordip.exception.FileInputException;
import com.example.refactordip.exception.InsideException;
import com.example.refactordip.exception.UnauthorizedException;
import com.example.refactordip.model.ExistFile;
import com.example.refactordip.model.MyClient;
import com.example.refactordip.model.MyFile;
import com.example.refactordip.pojo.ReqJwt;
import com.example.refactordip.pojo.ResFile;
import com.example.refactordip.repository.ClientRepo;
import com.example.refactordip.repository.FileRepo;
import com.example.refactordip.repository.AuthRepositoryIml;
import com.example.refactordip.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class MyService {
    private final static String UPLOAD_PATH = "C:\\Users\\gosha\\IdeaProjects\\refactordip\\uploadDir\\";
    private final static String DIR_NAME = "uploadDir";
    @Autowired
    private AuthRepositoryIml repository;
    @Autowired
    private ClientRepo clientRepo;
    @Autowired
    private FileRepo fileRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    public HashMap<String, Object> login(ReqJwt reqJwt) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(reqJwt.getUsername(), reqJwt.getPassword()));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Bad credentials");
        }
        var token = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());

        repository.putMap(token, reqJwt.getUsername());

        log.info("user {} login", reqJwt.getUsername());

        HashMap<String, Object> map = new HashMap<>();
        map.put("username", reqJwt.getUsername());
        map.put("token", token);
        return map;
    }

    public void logout(String auth) {
        if (auth.startsWith("Bearer ")) {
            auth = auth.substring(7);
        }
        var username = repository.getMap(auth);

        repository.deleteMap(auth);
        log.info("user {} logout", username);
    }


    @Transactional
    public void postFile(String auth, String filename, MultipartFile file)  {
        if (file.isEmpty()) {
            throw new FileInputException("the file should not be empty");
        }
        var client = getClientFromToken(auth, repository, clientRepo);
        if (client == null) {
            throw new UnauthorizedException("Unauthorized error");
        }

        var filePost = new File(DIR_NAME);

        try {
            log.info("byte size: {}", file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!filePost.exists()) {
            filePost.mkdir();
        }

        if (fileRepo.findByMyClientAndNameAndExist(client, filename, ExistFile.EXIST) != null) {
            throw new FileInputException("the file with this name exists");
        }
        // path + uuid + type
        var link = UPLOAD_PATH + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());

        try {
            file.transferTo(new File(link));
        } catch (IOException e) {
            throw new FileInputException("Error input data");
        }

        fileRepo.save(MyFile.builder()
                .date(new Date())
                .name(filename)
                .link(link)
                .exist(ExistFile.EXIST)
                .size(file.getSize())
                .myClient(client)
                .build());
    }

    @Transactional
    public void deleteFile(String authToken, String fileName) {
        var client = getClientFromToken(authToken, repository, clientRepo);
        if (client == null) {
            throw new UnauthorizedException("Unauthorized error");
        }
        if (fileRepo.findByMyClientAndNameAndExist(client, fileName, ExistFile.EXIST) == null) {
            throw new FileInputException("Error input data");
        }
        int answer = fileRepo.falseDeletion(client, fileName, ExistFile.NOT_EXIST);
        log.info("answer: {}", answer);
        if (answer == 0) {
            throw new InsideException("Error delete file");
        }
    }
    @Transactional
    public ResFile getFile(String authToken, String filename)  {
        var client = getClientFromToken(authToken, repository, clientRepo);
        if (client == null) {
            throw new UnauthorizedException("Unauthorized error");
        }
        var fileFromPc = fileRepo.findByMyClientAndNameAndExist(client,filename,ExistFile.EXIST);
        if(fileFromPc == null){
            throw new FileInputException("Error input data");
        }

        try {
            byte[] array = Files.readAllBytes(Paths.get(fileFromPc.getLink()));
            return new ResFile(array,filename);
        }catch (IOException e){
            throw new InsideException("error upload file");
        }

    }

    @Transactional
    public void putFile(String authToken, String oldName, String newName) {
        var client = getClientFromToken(authToken, repository, clientRepo);
        if (client == null) {
            throw new UnauthorizedException("Unauthorized error");
        }
        if (fileRepo.findByMyClientAndNameAndExist(client, oldName, ExistFile.EXIST) == null) {
            throw new FileInputException("Error input data");
        }
        int answer = fileRepo.editFileNameByClient(client, oldName, newName);

        if (answer == 0) {
            throw new InsideException("Error upload file");
        }
    }
     @Transactional
    public Page<MyFile> getAllFileInfo(String authToken, int limit) {
        var client = getClientFromToken(authToken, repository, clientRepo);
        if (client == null) {
            throw new UnauthorizedException("Unauthorized error");
        }
        return fileRepo.findAll(ExistFile.EXIST, client, PageRequest.of(0, limit).withSort(Sort.by("id")));
    }

    public static MyClient getClientFromToken(String authToken, AuthRepositoryIml repository, ClientRepo clientRepo) {
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
            log.info("token: {}", authToken);
        }
        var username = repository.getMap(authToken);
        return clientRepo.findByName(username);
    }
}
