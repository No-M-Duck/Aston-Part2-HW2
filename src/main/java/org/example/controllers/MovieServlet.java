package org.example.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.LocalDateAdapter;
import org.example.dto.MovieDTO;
import org.example.mapper.MovieMapperImpl;
import org.example.repository.MovieRepositoryImpl;
import org.example.service.MovieServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MovieServlet extends HttpServlet {

    private static final String ERROR_TEMPLATE = "{\"error\": \"%s\"}";
    private final MovieServiceImpl movieServiceImpl;

    private static Gson gson = GsonProvider.getGson();

    private HttpServletResponse settingResp(HttpServletResponse resp){
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        return resp;
    }

    public MovieServlet() {
        this.movieServiceImpl = new MovieServiceImpl(new MovieRepositoryImpl(), new MovieMapperImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String movieId = req.getParameter("id");
        resp = settingResp(resp);

        try {
            if (movieId == null) {
                // Получение всех фильмов
                List<MovieDTO> movies = movieServiceImpl.findAllEntity();
                resp.getWriter().write(gson.toJson(movies));
            } else {
                // Получение фильма по ID
                UUID id = UUID.fromString(movieId);
                MovieDTO movie = movieServiceImpl.findEntityById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
                resp.getWriter().write(gson.toJson(movie));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp = settingResp(resp);

        try {
            MovieDTO movieDTO = gson.fromJson(req.getReader(), MovieDTO.class);
            boolean created = movieServiceImpl.createEntity(movieDTO);
            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"message\": \"Movie created successfully\"}");
            } else {
                throw new IllegalStateException("Failed to create movie");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp = settingResp(resp);

        try {
            MovieDTO movieDTO = gson.fromJson(req.getReader(), MovieDTO.class);
            boolean updated = movieServiceImpl.updateEntity(movieDTO);
            if (updated) {
                resp.getWriter().write("{\"message\": \"Movie updated successfully\"}");
            } else {
                throw new IllegalStateException("Failed to update movie");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Director ID is required\"}");
                return;
            }

            String movieId = pathInfo.substring(1);
            UUID id = UUID.fromString(movieId);

            boolean deleted = movieServiceImpl.deleteEntity(id);

            if (deleted) {
                resp.getWriter().write("{\"message\": \"Movie deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Failed to delete movie\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(String.format(ERROR_TEMPLATE, e.getMessage()));
        }

    }
}

class GsonProvider {

    private GsonProvider() {
        throw new IllegalStateException("Utility class");
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }
}
