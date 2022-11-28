package com.example.refactordip.Service;

import com.example.refactordip.exception.FileInputException;
import com.example.refactordip.exception.InsideException;
import com.example.refactordip.exception.UnauthorizedException;
import com.example.refactordip.model.ExistFile;
import com.example.refactordip.model.MyClient;
import com.example.refactordip.model.MyFile;
import com.example.refactordip.pojo.ReqJwt;
import com.example.refactordip.repository.ClientRepo;
import com.example.refactordip.repository.FileRepo;
import com.example.refactordip.repository.AuthRepositoryIml;
import com.example.refactordip.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(reqJwt.getLogin(), reqJwt.getPassword()));
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials");
            throw new UnauthorizedException("Bad credentials");
        }
        var token = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());

        repository.putMap(token, reqJwt.getLogin());

        log.info("user {} login", reqJwt.getLogin());

        HashMap<String, Object> map = new HashMap<>();
        map.put("auth-token", token);
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
    public void postFile(String auth, String filename, MultipartFile file) {

        if (file.isEmpty()) {
            log.warn("the file should not be empty");
            throw new FileInputException("the file should not be empty");
        }

        var client = getClientFromToken(auth, repository, clientRepo);

        var filePost = new File(DIR_NAME);


        if (!filePost.exists()) {
            filePost.mkdir();
        }

        if (fileRepo.findByMyClientAndFilenameAndExist(client, filename, ExistFile.EXIST) != null) {
            log.warn("the file with this name exists");
            throw new FileInputException("the file with this name exists");
        }
        // path + uuid + type
        var link = UPLOAD_PATH + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());

        try {
            file.transferTo(new File(link));
        } catch (IOException e) {
            log.warn("Error input data");
            throw new FileInputException("Error input data");
        }

        fileRepo.save(MyFile.builder()
                .date(new Date())
                .filename(filename)
                .link(link)
                .exist(ExistFile.EXIST)
                .size(file.getSize())
                .myClient(client)
                .build());
    }

    @Transactional
    public void deleteFile(String authToken, String fileName) {
        var client = getClientFromToken(authToken, repository, clientRepo);

        if (fileRepo.findByMyClientAndFilenameAndExist(client, fileName, ExistFile.EXIST) == null) {
            log.warn("Error input data");
            throw new FileInputException("Error input data");
        }
        int answer = fileRepo.falseDeletion(client, fileName, ExistFile.NOT_EXIST);
        log.info("answer: {}", answer);
        if (answer == 0) {
            log.warn("Error delete file");
            throw new InsideException("Error delete file");
        }
    }

    @Transactional
    public byte[] getFile(String authToken, String filename) {
        var client = getClientFromToken(authToken, repository, clientRepo);

        var fileFromPc = fileRepo.findByMyClientAndFilenameAndExist(client, filename, ExistFile.EXIST);
        if (fileFromPc == null) {
            log.warn("Error input data");
            throw new FileInputException("Error input data");
        }

        try {
            return Files.readAllBytes(Paths.get(fileFromPc.getLink()));
        } catch (IOException e) {
            log.error("error upload file");
            throw new InsideException("error upload file");
        }

    }

    @Transactional
    public void putFile(String authToken, String oldName, String newName) {
        var client = getClientFromToken(authToken, repository, clientRepo);

        if (fileRepo.findByMyClientAndFilenameAndExist(client, oldName, ExistFile.EXIST) == null) {
            throw new FileInputException("Error input data");
        }
        int answer = fileRepo.editFileNameByClient(client, oldName, newName, ExistFile.EXIST);

        if (answer == 0) {
            log.error("Error upload file");
            throw new InsideException("Error upload file");
        }
    }

    @Transactional
    public Page<MyFile> getAllFileInfo(String authToken, int limit) {
        var client = getClientFromToken(authToken, repository, clientRepo);

        if (limit <= 0) {
            log.error("Error getting file list");
            throw new InsideException("Error getting file list");
        }

        return fileRepo.findAll(ExistFile.EXIST, client, PageRequest.of(0, limit).withSort(Sort.by("id")));
    }

    public static MyClient getClientFromToken(String authToken, AuthRepositoryIml repository, ClientRepo clientRepo) {
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
            log.info("token: {}", authToken);
        }
        var username = repository.getMap(authToken);
        var client = clientRepo.findByName(username);
        if (client == null) {
            log.warn("Unauthorized error");
            throw new UnauthorizedException("Unauthorized error");
        }
        return client;
    }
}
