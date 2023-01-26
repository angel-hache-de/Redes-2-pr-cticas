
import dao.LoginDAOImpl;

import model.User;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginDAOTest {
    private LoginDAOImpl loginDAO;

    private User user;

    @BeforeAll
    void setUpAll (){
        loginDAO = new LoginDAOImpl();
        user = new User("user", "user");
    }

    @Test
    @Order(1)
    public void signUpTest() {
        loginDAO.signUp(user);

        assertTrue(user.getId() > 0);
        assertEquals(user.getRole(), User.USER_ROLE);
    }

    @Test
    @Order(2)
    public void loginTest() {
        User u = new User(user.getUsername(), user.getPassword());

        loginDAO.login(u);

        assertTrue(u.getId() != -1);
        assertEquals(u.getId(), user.getId());
        assertEquals(u.getRole(), User.USER_ROLE);
    }

    @Test
    @Order(3)
    public void deleteUserTest() {
        boolean flag = loginDAO.deleteUser(user.getId());

        assertTrue(flag);
    }
}
