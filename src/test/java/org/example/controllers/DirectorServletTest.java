package org.example.controllers;

import com.google.gson.Gson;

import org.example.dto.DirectorDTO;
import org.example.service.DirectorServiceImpl;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.io.*;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DirectorServletTest {

    @Mock
    DirectorServiceImpl service;

    @InjectMocks
    DirectorServlet servlet;

    private Gson gson;

    //Director director = new Director(directorId, "Christopher", "Nolan", "UK");
    @BeforeEach
    public void setUp() {
        servlet = new DirectorServlet(service);
        gson = new Gson();

    }

    @Test
    void doGet() throws IOException, ServletException {
        DirectorDTO director1 = new DirectorDTO(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),"Steven", "Spielberg", "USA");

        when(service.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")))
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


        when(service.findAll()).thenReturn(dtos);

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
        when(service.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440003")))
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

        when(service.create(any()))
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
        when(service.create(any())).thenReturn(false);

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



    @Test
    @Order(6)
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

        when(service.update(director1)).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doPut(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Director updated successfully\"}",sw.toString());
    }

    @Test
    @Order(6)
    void doPutNoUpdated() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringReader reader = new StringReader("{" +
                "\"id\":\"550e8400-e29b-41d4-a716-446655440000\","+
                "\"name\":\"Quentin\"," +
                " \"lastName\":\"Tarantino\"," +
                " \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));

        when(service.update(any())).thenReturn(false);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doPut(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"error\": \"Failed to update director\"}",sw.toString());
    }

    @Test
    @Order(7)
    void doDelete() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/550e8400-e29b-41d4-a716-446655440000");

        when(service.delete(any())).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doDelete(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Director deleted successfully\"}",sw.toString());
    }

    @Test
    @Order(8)
    void doDeleteFail() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/550e8400-e29b-41d4-a716-446655440000");

        when(service.delete(any())).thenReturn(false);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);


        servlet.doDelete(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"error\": \"Failed to delete director\"}",sw.toString());
    }

    @Test
    @Order(8)
    void doDeletePathFail() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn(null);


        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        servlet.doDelete(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"error\": \"Director ID is required\"}",sw.toString());
    }
}