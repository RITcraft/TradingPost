package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import static spark.Spark.*;

public class Index {
    public static void register(String ip, int port)
    {
        ipAddress(ip);
        port(port);
        get("/",new IndexPage());
        get("/view/:id",);
        get("/buy",);
        get("/login",);
    }
}
