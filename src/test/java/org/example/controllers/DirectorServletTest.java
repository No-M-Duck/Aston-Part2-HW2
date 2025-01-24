package org.example.controllers;

import com.google.gson.Gson;

import com.zaxxer.hikari.HikariConfig;
import org.example.database.DataSource;
import org.example.database.DbUtils;
import org.example.dto.DirectorDTO;
import org.example.entity.Director;
import org.example.mapper.DirectorMapper;
import org.example.mapper.DirectorMapperImpl;
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

    @Mock
    DirectorRepositoryImpl repository;

    @Mock
    DirectorMapperImpl mapper;

    @InjectMocks
    DirectorServlet servlet;

    private Gson gson;



    private static PostgreSQLContainer<?> container =new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"));

   /* @BeforeAll
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
*/
    //Director director = new Director(directorId, "Christopher", "Nolan", "UK");
    @BeforeEach
    public void setUp() {
        gson = new Gson();

    }

    @Test
    @Order(1)
    void doGet() throws IOException, ServletException {
        when(repository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")))
                .thenReturn(Optional.of(new Director(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA")));

        DirectorDTO director1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");

        when(mapper.toDTO(any(Director.class)))
                .thenReturn(director1);

        when(service.findEntityById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")))
                .thenReturn(Optional.of(director1));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn("550e8400-e29b-41d4-a716-446655440000");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doGet(request, response);

        verify(response).setContentType("application/json");

        assertEquals(gson.toJson(director1), sw.toString());


    }

    @Test
    @Order(2)
    void doGetAll() throws IOException, ServletException {

        DirectorDTO directorDto1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");
        DirectorDTO directorDto2 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), "Christopher", "Nolan", "UK");
        List<DirectorDTO> dtos = List.of(directorDto1, directorDto2);

        Director director1 = new Director(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");
        Director director2 = new Director(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), "Christopher", "Nolan", "UK");
        List<Director> ents = List.of(director1, director2);

        when(repository.findAll()).thenReturn(ents);

        when(service.findAllEntity()).thenReturn(dtos);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("id")).thenReturn(null);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doGet(request, response);

        verify(response).setContentType("application/json");

        assertEquals(gson.toJson(dtos), sw.toString());


    }

    @Test
    @Order(3)
    void doGetNotFound() throws IOException, ServletException {
        when(repository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440003")))
                .thenReturn(Optional.empty());
        DirectorDTO director1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");

        when(mapper.toDTO(any(Director.class)))
                .thenReturn(new DirectorDTO(null,null,null,null));

        when(service.findEntityById(UUID.fromString("550e8400-e29b-41d4-a716-446655440003")))
                .thenReturn(Optional.empty());

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn("550e8400-e29b-41d4-a716-446655440003");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doGet(request, response);

        verify(response).setContentType("application/json");

        assertEquals("{\"error\": \"Director not found\"}", sw.toString());


    }

    @Test
    @Order(4)
    void doPost() throws IOException, ServletException {

        when(repository.create(new Director("Steven","Spielberg","USA"))).thenReturn(true);
        when(mapper.toDTO(any(Director.class)))
                .thenReturn(new DirectorDTO(
                        UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                        "Steven", "Spielberg", "USA"));

        when(service.createEntity(new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA")))
                .thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doPost(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Director created successfully\"}",sw.toString());

    }

    @Test
    @Order(5)
    void doPostFail() throws IOException, ServletException {
        DirectorDTO directorDTO = new DirectorDTO(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                "Steven", "Spielberg", "USA"
        );

        when(repository.create(any())).thenReturn(false);
        when(mapper.toEntity(any())).thenReturn(new Director("Steven", "Spielberg", "USA"));
        when(service.createEntity(any())).thenReturn(false);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doPost(request,response);

        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        assertEquals("{\"error\": \"Failed to create director\"}",sw.toString());

    }
/*
    @Test
    @Order(5)
    void doPut() throws IOException, ServletException {
        when(repository.update(new Director("Steven","Spielberg","USA"))).thenReturn(true);
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

        // �������� ������ � �����
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // �������������� JSON ��� �������� ������
        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");
        when(request.getReader()).thenReturn(new BufferedReader(reader));

        // ������������ ��������� �������
        when(service.createEntity(any(DirectorDTO.class))).thenReturn(false);

        // �������� writer ��� ������
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        // �������� doPost
        servlet.doPost(request, response);

        // ��������� ��������� ������
        verify(response).setContentType("application/json");

        // ��������� ���������� ������
        pw.flush(); // ����������� ���������� writer
        assertEquals("{\"error\": \"Failed to create director\"}", sw.toString());
    }

     */
}