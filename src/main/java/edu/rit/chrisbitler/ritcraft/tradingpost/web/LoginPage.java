package edu.rit.chrisbitler.ritcraft.tradingpost.web;

import edu.rit.chrisbitler.ritcraft.tradingpost.TradingPost;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import spark.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by cbitler on 11/10/15.
 */
public class LoginPage implements Route {
    public Object handle(Request request, Response response) throws Exception {
        if (request.queryParams("username") != null && request.queryParams("password") != null) {
            System.out.println("1");
            String user = request.queryParams("username");
            OfflinePlayer player = Bukkit.getOfflinePlayer(user);
            if (player != null) {
                System.out.println("2");
                UUID uuid = player.getUniqueId();
                Statement stmt = TradingPost.instance.getMYSQL().createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM `users` WHERE `uuid`='" + uuid.toString() + "'");
                if (rs.next()) {
                    System.out.println("3");
                    String salt = rs.getString("salt");
                    byte[] key = base64Decoder(salt);
                    if (hash(request.queryParams("password"), key).equals(rs.getString("password"))) {
                        System.out.println("4");
                        request.session().attribute("loggedIn", true);
                        request.session().attribute("uuid", uuid.toString());
                        response.redirect("/");
                    } else {
                        response.status(400);
                        response.redirect("/?error=Incorrect%20Password");
                    }
                } else {
                    response.status(401);
                    response.redirect("/?error=Non-existant user");
                }
            } else {
                response.status(401);
                response.redirect("/?error=Non-existant user");
            }
        } else {
            response.status(400);
            response.redirect("/?error=Malformed%20Request");
        }
        return null;
    }


    private String hash(String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < 1000; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return base64Encode(input);
    }

    private String base64Encode(byte[] data) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    private byte[] base64Decoder(String data) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return decoder.decodeBuffer(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
