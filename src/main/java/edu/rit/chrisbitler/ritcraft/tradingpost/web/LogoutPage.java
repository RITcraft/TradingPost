package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Created by cbitler on 11/10/15.
 */
public class LogoutPage implements Route {
    public Object handle(Request request, Response response) throws Exception {
        request.session().removeAttribute("loggedIn");
        request.session().removeAttribute("uuid");
        response.redirect("/");
        return null;
    }
}
