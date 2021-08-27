package database;

import datafiles.Rider;

import java.util.HashMap;

public class SmartSendDB {
    private static HashMap<String, Rider> riders;

    static {
        riders = new HashMap<>();
    }

    public static void addRider(Rider rider) {
        riders.put(rider.getEmail(), rider);
    }

    public static Rider getRider(String email, String password) {
        Rider rider = riders.get(email);

        if (rider != null) {
            if (!rider.getPassword().equals(password)) {
                rider = null;
            }
        }

        return rider;
    }
}
