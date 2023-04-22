package com.example.financetracker;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SessionCollector implements HttpSessionListener {
    private final CopyOnWriteArrayList<HttpSession> sessions = new CopyOnWriteArrayList<>();
    @Override
    public void sessionCreated(HttpSessionEvent s) {
        sessions.add(s.getSession());
        HttpSessionListener.super.sessionCreated(s);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent s) {
        sessions.remove(s.getSession());
        HttpSessionListener.super.sessionDestroyed(s);
    }
    public List<HttpSession> getAllSessions() {

        return new ArrayList<>(sessions);
    }
}
