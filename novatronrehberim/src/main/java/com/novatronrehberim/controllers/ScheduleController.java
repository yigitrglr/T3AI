package com.novatronrehberim.controllers;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ScheduleController extends HttpServlet {
    @Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        RequestDispatcher dispatcher = request.getRequestDispatcher("views/schedule.jsp");
        dispatcher.forward(request, response);
    }
}
