package hu.oktatas.java.ee.webshop.startup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.oktatas.java.ee.webshop.beans.MobileType;
import hu.oktatas.java.ee.webshop.beans.UserDTO;
import hu.oktatas.java.ee.webshop.db.MobileDB;
import hu.oktatas.java.ee.webshop.db.UserDB;
import hu.oktatas.java.ee.webshop.db.exceptions.UsernameAlreadyTakenException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.Serializable;

@Startup
@Singleton
public class StartUpFunctions implements Serializable {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String USER_JSON_RESOURCE = "json/users.json";
    private static final String MOBILE_JSON_RESOURCE = "json/mobiles.json";

    @Inject
    Logger logger;

    @Inject
    private UserDB userDB;

    @Inject
    private MobileDB mobileDB;

    private void addUsersFromJson() throws IOException, UsernameAlreadyTakenException {
        List<UserDTO> users = MAPPER.readValue(StartUpFunctions.class.getClassLoader()
                .getResource(USER_JSON_RESOURCE),
                new TypeReference<List<UserDTO>>() {
        });

        for (UserDTO user : users) {
            userDB.registrate(user);
        }
    }

    private void addMobilesFromJson() throws IOException {
        List<MobileType> mobiles = MAPPER.readValue(StartUpFunctions.class.getClassLoader().
                getResource(MOBILE_JSON_RESOURCE),
                new TypeReference<List<MobileType>>() {
        });

        mobiles.stream().forEach(mobile -> mobileDB.addNewMobileType(mobile));
    }

    @PostConstruct
    public void uploadData() {
        try {
            addUsersFromJson();
            addMobilesFromJson();
        } catch (IOException | UsernameAlreadyTakenException ex) {
            logger.log(Level.ALL, "Problem occorred on JSON import: {0}", ex);
        }
    }
}
