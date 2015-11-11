package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.*;

public class Index {
    public static void register(String ip, int port)
    {
        ipAddress(ip);
        port(port);
        staticFileLocation("/public");
        get("/", new IndexPage(), new FreeMarkerEngine());
        post("/login", new LoginPage());
        get("/logout", new LogoutPage());
        get("/view/:id", new ViewPage(), new FreeMarkerEngine());
        get("/data", new DataPage());
        /*get("/buy",);
        get("/login",);*/
    }
}
