package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Created by Chris on 11/2/2015.
 */
public class IndexPage implements Route {
    public Object handle(Request request, Response response) throws Exception {
        StringBuffer html = new StringBuffer();
        html.append("<head>");
        html.append("<link href='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css' rel='stylesheet'>");
    }
}
