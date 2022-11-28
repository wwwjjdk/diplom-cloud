package com.example.refactordip;

import com.example.refactordip.Service.MyService;
import com.example.refactordip.model.ExistFile;
import com.example.refactordip.model.MyClient;
import com.example.refactordip.model.MyFile;
import com.example.refactordip.repository.AuthRepositoryIml;
import com.example.refactordip.repository.ClientRepo;
import com.example.refactordip.repository.FileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MyServiceTest {
    public static final String TOKEN_1 = "token1";
    public static final String LOGIN_1 = "login1";
    public static final String FILENAME_1 = "fileName1";

    public static final String NEW_FILENAME_1 = "newFileName1";

    public static final String LINK = "link1";

    public static final Long SIZE = 30935L;

    public static final int ANSWER = 1;

    private static final MyClient MY_CLIENT = MyClient.builder()
            .name("login1")
            .password("password1")
            .date(new Date())
            .role("ROLE_CLIENT").build();

    private static final MyFile MY_FILE = MyFile.builder()
            .filename(FILENAME_1)
            .date(new Date())
            .myClient(MY_CLIENT)
            .exist(ExistFile.EXIST)
            .size(SIZE)
            .link(LINK).build();

    @InjectMocks
    private MyService myService;

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private FileRepo fileRepo;

    @Mock
    AuthRepositoryIml authRepositoryIml;

    @BeforeEach
    void setUp() {
        Mockito.when(authRepositoryIml.getMap(TOKEN_1)).thenReturn(LOGIN_1);
        Mockito.when(clientRepo.findByName(LOGIN_1)).thenReturn(MY_CLIENT);
        Mockito.when(fileRepo.findByMyClientAndFilenameAndExist(MY_CLIENT, FILENAME_1, ExistFile.EXIST)).thenReturn(MY_FILE);
        Mockito.when(fileRepo.falseDeletion(MY_CLIENT, FILENAME_1, ExistFile.NOT_EXIST)).thenReturn(ANSWER);
        Mockito.when(fileRepo.editFileNameByClient(MY_CLIENT, FILENAME_1, NEW_FILENAME_1, ExistFile.EXIST)).thenReturn(ANSWER);

    }

    @Test
    void deleteFileTest() {
        myService.deleteFile(TOKEN_1, FILENAME_1);
        Mockito.verify(fileRepo,
                        Mockito.times(1))
                .falseDeletion(MY_CLIENT, FILENAME_1, ExistFile.NOT_EXIST);
    }

    @Test
    void PutFileTest() {
        myService.putFile(TOKEN_1, FILENAME_1, NEW_FILENAME_1);
        Mockito.verify(fileRepo, Mockito.times(1)).
                editFileNameByClient(MY_CLIENT, FILENAME_1, NEW_FILENAME_1, ExistFile.EXIST);
    }

}
