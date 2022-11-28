package com.example.refactordip.repository;

import com.example.refactordip.model.ExistFile;
import com.example.refactordip.model.MyClient;
import com.example.refactordip.model.MyFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepo extends JpaRepository<MyFile, Long> {

    MyFile findByMyClientAndFilenameAndExist(MyClient client, String name, ExistFile existFile);

    @Modifying
    @Query(value = "update MyFile mf set mf.exist = :existFile where mf.myClient = :myClient and mf.filename = :filename")
    int falseDeletion(@Param("myClient") MyClient myClient, @Param("filename") String filename, @Param("existFile") ExistFile existFile);

    @Modifying
    @Query(value = "update MyFile mf set mf.filename = :newName where" +
            " mf.myClient = :myClient and mf.filename = :oldName and mf.exist = :existFile")
    int editFileNameByClient(@Param("myClient") MyClient myClient, @Param("oldName") String oldName,
                             @Param("newName") String newName, @Param("existFile") ExistFile existFile);


    @Query(value = "select mf from MyFile mf where mf.exist = :exist and mf.myClient = :myClient")
    Page<MyFile> findAll(@Param("exist") ExistFile exist,@Param("myClient") MyClient myClient, PageRequest id);
}
