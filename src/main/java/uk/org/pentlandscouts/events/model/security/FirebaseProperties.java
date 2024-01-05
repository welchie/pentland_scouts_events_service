package uk.org.pentlandscouts.events.model.security;

import lombok.Data;

@Data
public class FirebaseProperties {

    int sessionExpiryInDays;
    String databaseUrl;
    boolean enableStrictServerSession;
    boolean enableCheckSessionRevoked;
    boolean enableLogoutEverywhere;

}