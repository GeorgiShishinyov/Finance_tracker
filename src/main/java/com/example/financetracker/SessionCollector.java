package com.example.financetracker;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SessionCollector implements HttpSessionListener {
    private final List<HttpSession> sessions = new ArrayList<>();
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessions.add(se.getSession());
        HttpSessionListener.super.sessionCreated(se);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessions.remove(se.getSession());
        HttpSessionListener.super.sessionDestroyed(se);
    }
    public List<HttpSession> getAllSessions() {
        return new ArrayList<>(sessions);
    }
}
