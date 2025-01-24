package org.example.controllers;
import com.google.gson.Gson;
import org.example.database.DataSource;
import org.example.dto.DirectorDTO;
import org.example.mapper.DirectorMapperImpl;
import org.example.repository.DirectorRepositoryImpl;
import org.example.service.DirectorServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class DirectorServlet extends HttpServlet {

    private final DirectorServiceImpl directorServiceImpl;
    private static final String ERROR_TEMPLATE = "{\"error\": \"%s\"}";


    public DirectorServlet() {
        this.directorServiceImpl = new DirectorServiceImpl(new DirectorRepositoryImpl(), new DirectorMapperImpl());
    }

    public DirectorServlet(DirectorServiceImpl directorServiceImpl) {
        this.directorServiceImpl = directorServiceImpl;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String directorId = req.getParameter("id");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (directorId == null) {
                // Получение всех режиссеров
                List<DirectorDTO> directors = directorServiceImpl.findAllEntity();
                resp.getWriter().write(new Gson().toJson(directors));
            } else {
                // Получение конкретного режиссера
                UUID id = UUID.fromString(directorId);
                DirectorDTO director = directorServiceImpl.findEntityById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Director not found"));
                resp.getWriter().write(new Gson().toJson(director));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            DirectorDTO director = new Gson().fromJson(req.getReader(), DirectorDTO.class);
            boolean created = directorServiceImpl.createEntity(director);

            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"message\": \"Director created successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Failed to create director\"}");
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            DirectorDTO director = new Gson().fromJson(req.getReader(), DirectorDTO.class);
            boolean updated = directorServiceImpl.updateEntity(director);

            if (updated) {
                resp.getWriter().write("{\"message\": \"Director updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Failed to update director\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            // Получение ID из URL
            String pathInfo = req.getPathInfo(); // Получает часть пути после "/directors"
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Director ID is required\"}");
                return;
            }

            String directorId = pathInfo.substring(1); // Убирает начальный "/"
            UUID id = UUID.fromString(directorId);

            boolean deleted = directorServiceImpl.deleteEntity(id);

            if (deleted) {
                resp.getWriter().write("{\"message\": \"Director deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Failed to delete director\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));
        }
    }
}
