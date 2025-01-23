package org.example.controllers;

import com.google.gson.Gson;

import com.zaxxer.hikari.HikariConfig;
import org.example.database.DataSource;
import org.example.database.DbUtils;
import org.example.dto.DirectorDTO;
import org.example.entity.Director;
import org.example.mapper.DirectorMapper;
import org.example.repository.DirectorRepositoryImpl;
import org.example.service.DirectorServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.io.*;
import java.time.Duration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DirectorServletTest {

    @Mock
    DirectorServiceImpl service;

    @InjectMocks
    DirectorServlet servlet;

    private Gson gson;



    private static PostgreSQLContainer<?> container =new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"));

    @BeforeAll
    public static void beforeAll() {
        container
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(1)));
        container.start();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        DataSource.init(config);
        DbUtils.startTest(true);

    }

    //Director director = new Director(directorId, "Christopher", "Nolan", "UK");
    @BeforeEach
    public void setUp() {
        gson = new Gson();

    }

    @Test
    @Order(1)
    void doGet() throws IOException, ServletException {


        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("id")).thenReturn("550e8400-e29b-41d4-a716-446655440000");

        DirectorDTO director1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");


        when(service.findEntityById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(director1));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doGet(request, response);

        verify(response).setContentType("application/json");

        assertEquals(gson.toJson(director1), sw.toString());


    }
    /*
    @Test
    @Order(2)
    void doGetAll() throws IOException, ServletException {


        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("id")).thenReturn(null);

        DirectorDTO director1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");
        DirectorDTO director2 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), "Christopher", "Nolan", "UK");


        List<DirectorDTO> dtos = List.of(director1, director2);
        when(service.findAllEntity()).thenReturn(dtos);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doGet(request, response);

        verify(response).setContentType("application/json");

        assertEquals(gson.toJson(dtos), sw.toString());


    }

    @Test
    @Order(3)
    void doPost() throws IOException, ServletException {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));

        DirectorDTO director1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");

        when(service.createEntity(director1)).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doPost(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Director created successfully\"}",sw.toString());

    }

    @Test
    @Order(4)
    void doPut() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringReader reader = new StringReader("{" +
                "\"id\":\"550e8400-e29b-41d4-a716-446655440000\","+
                "\"name\":\"Quentin\"," +
                " \"lastName\":\"Tarantino\"," +
                " \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));

        DirectorDTO director1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Quentin", "Tarantino", "USA");

        when(service.updateEntity(director1)).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doPut(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Director updated successfully\"}",sw.toString());
    }

    @Test
    @Order(5)
    void doDelete() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/550e8400-e29b-41d4-a716-446655440000");

        when(service.deleteEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doDelete(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Director deleted successfully\"}",sw.toString());
    }


    @Test
    @Order(6)
    void doPostNoSave() throws IOException, ServletException {

        // Мокируем запрос и ответ
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Подготавливаем JSON для входящих данных
        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");
        when(request.getReader()).thenReturn(new BufferedReader(reader));

        // Замокировать поведение сервиса
        when(service.createEntity(any(DirectorDTO.class))).thenReturn(false);

        // Мокируем writer для ответа
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        // Вызываем doPost
        servlet.doPost(request, response);

        // Проверяем заголовок ответа
        verify(response).setContentType("application/json");

        // Проверяем содержимое ответа
        pw.flush(); // Обязательно сбрасываем writer
        assertEquals("{\"error\": \"Failed to create director\"}", sw.toString());
    }

     */
}