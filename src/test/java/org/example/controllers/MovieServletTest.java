package org.example.controllers;

import com.google.gson.Gson;
import org.example.dto.MovieDTO;
import org.example.entity.Movie;
import org.example.service.MovieServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.testcontainers.shaded.org.checkerframework.common.value.qual.EnsuresMinLenIf;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MovieServletTest {

    @Mock
    MovieServiceImpl service;

    @InjectMocks
    MovieServlet servlet;

    private Gson gson;

    @BeforeEach
    public void setUp() {
        servlet = new MovieServlet(service);
        gson = GsonProvider.getGson();

    }

    @Test
    void doGet() throws IOException, ServletException {
        MovieDTO movie = new MovieDTO(null, null, "The Terminal", LocalDate.of(2004, 6, 18), 128, 6);

        List<MovieDTO> movies = List.of(movie);

        when(service.findAllEntity()).thenReturn(movies);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn(null);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        servlet.doGet(request,response);

        verify(response).setContentType("application/json");

        assertEquals(gson.toJson(movies), sw.toString());
    }

    @Test
    void doGetEntity() throws IOException, ServletException {
        MovieDTO movie = new MovieDTO(null, null, "The Terminal", LocalDate.of(2004, 6, 18), 128, 6);

        when(service.findEntityById(any())).thenReturn(Optional.of(movie));

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn(String.valueOf(UUID.randomUUID()));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        servlet.doGet(request,response);

        verify(response).setContentType("application/json");

        assertEquals(gson.toJson(movie), sw.toString());
    }

    @Test
    void doPost() throws IOException, ServletException {

        MovieDTO movie = new MovieDTO(null, null, "The Terminal", LocalDate.of(2004, 6, 18), 128, 6);

        when(service.createEntity(any())).thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Movie created successfully\"}", sw.toString());
    }

    @Test
    void doPostFail() throws IOException, ServletException {
        when(service.createEntity(any())).thenReturn(false);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));
        when(response.getWriter()).thenReturn(pw);

        servlet.doPost(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"error\": \"Failed to create movie\"}", sw.toString());
    }

    @Test
    void doPut() throws IOException, ServletException {
        when(service.updateEntity(any())).thenReturn(true);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));
        when(response.getWriter()).thenReturn(pw);

        servlet.doPut(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Movie updated successfully\"}", sw.toString());
    }

    @Test
    void doPutFail() throws IOException, ServletException {
        when(service.updateEntity(any())).thenReturn(false);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        StringReader reader = new StringReader("{\"name\":\"Steven\", \"lastName\":\"Spielberg\", \"country\":\"USA\"}");

        when(request.getReader()).thenReturn(new BufferedReader(reader));
        when(response.getWriter()).thenReturn(pw);

        servlet.doPut(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"error\": \"Failed to update movie\"}", sw.toString());
    }

    @Test
    void doDelete() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/550e8400-e29b-41d4-a716-446655440000");

        when(service.deleteEntity(any())).thenReturn(true);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        servlet.doDelete(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"message\": \"Movie deleted successfully\"}",sw.toString());
    }

    @Test
    void doDeleteFail() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/550e8400-e29b-41d4-a716-446655440000");

        when(service.deleteEntity(any())).thenReturn(false);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        servlet.doDelete(request,response);

        verify(response).setContentType("application/json");

        assertEquals("{\"error\": \"Failed to delete movie\"}",sw.toString());
    }
}