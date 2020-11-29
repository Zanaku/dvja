package com.appsecco.dvja.services;

import com.appsecco.dvja.controllers.PingAction;
import com.appsecco.dvja.models.User;
import org.junit.Test;

import javax.persistence.Query;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JunitTest extends HibernateTest {
    User admin = new User();
    User evo = new User();
    User tom = new User();

    @Test
    public void SQLITest(){
        UserService service = new UserService();
        service.setEntityManager(em);
        User user = service.findByLogin("' or '1'='1");
        if(user!=null){
            assertNotEquals("Admin",user.getName());
        }
        assertNull(user);

    }

    @Test
    public void CommITest(){
        PingAction pa = new PingAction();
        pa.setAddress(" && echo This is unsafe operation");
        pa.execute();
        assertEquals("Output:\n" +
                "\n" +
                "This is a very safe operation From:   echo This is unsafe operation\"\n" +
                "\n" +
                "Error:\n" +
                "\n",pa.getCommandOutput());
    }

    @Test
    public void InsecureCredStorageTest() throws NoSuchAlgorithmException {
        String password = "password";
        UserService service = new UserService();
        service.setEntityManager(em);

        User demoUser = new User();
        demoUser.setPassword(password);
        demoUser.setEmail("demo@users.user");
        demoUser.setLogin("demo");
        demoUser.setName("Demo user");
        demoUser.setRole("user");

        em.getTransaction().begin();
        service.save(demoUser);
        em.flush();
        em.getTransaction().commit();

        Query query = em.createQuery("SELECT u FROM User u WHERE u.login = :login ").
                setParameter("login", "demo").
                setMaxResults(1);
        List<User> resultList = query.getResultList();

        String salt = resultList.get(0).getPassword().split(":")[0];
        byte[] byteSalt = Base64.getDecoder().decode(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(byteSalt);

        byte[] hashed_pass = md.digest(password.getBytes(StandardCharsets.UTF_8));

        String expected = salt+":"+Base64.getEncoder().encodeToString(hashed_pass);

        assertEquals(expected, resultList.get(0).getPassword());
    }
}
