import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by joserran on 11/25/2015.
 */
public class Login
{
    public static void main(String[] args)
    {
        final JFrame login = new JFrame("Login");
        JLabel usernameLabel;
        JPanel panel = new JPanel();
        final JTextField loginName = new JTextField(20);
        JButton enter = new JButton("Login");

        panel.add(loginName);
        panel.add(enter);
        login.setSize(300, 100);
        login.add(panel);
        login.setVisible(true);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        enter.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    boolean userExistsAlready = userExists(loginName.getText());
                    if(userExistsAlready)
                    {
                        JOptionPane.showMessageDialog(null, "User " + loginName.getText() + " already exists in application, please try to login with a different username.");
                        loginName.setText("");
                        return;
                    }
                    addUser(loginName.getText());
                    Integer userId = getUserId(loginName.getText());
                    System.out.println(loginName.getText() + " " + userId);
                    new ChatClient(loginName.getText(), userId, 0);
                    login.setVisible(false);
                    login.dispose();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });

        loginName.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        boolean userExistsAlready = userExists(loginName.getText());
                        if(userExistsAlready)
                        {
                            JOptionPane.showMessageDialog(null, "User " + loginName.getText() + " already exists in application, please try to login with a different username.");
                            loginName.setText("");
                            return;
                        }
                        addUser(loginName.getText());
                        Integer userId = getUserId(loginName.getText());
                        System.out.println(loginName.getText() + " " + userId);
                        new ChatClient(loginName.getText(), userId, 0);
                        login.setVisible(false);
                        login.dispose();

                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private static void addUser(String loginN)
    {
        DatabaseConnector connection = new DatabaseConnector();
        if(!connection.open())
            System.out.println("unable to connect");

        ResultSet resultSet = null;
        try {
            String query = "insert into users(username) values(?)";
            PreparedStatement pStatement = connection.conn.prepareStatement(query);
            pStatement.setString(1, loginN);
            pStatement.execute();
            connection.conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static int getUserId(String loginN)
    {
        DatabaseConnector connection = new DatabaseConnector();
        Integer id = -1;
        if(!connection.open())
            System.out.println("unable to connect");

        ResultSet resultSet = null;
        try
        {

            String query = "select id from users where username=?";
            PreparedStatement pStatement = connection.conn.prepareStatement(query);
            pStatement.setString(1, loginN);
            ResultSet rs = pStatement.executeQuery();

            if(rs.next())
            {
                id = rs.getInt(1);
            }
            connection.conn.close();

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return id;
    }



    private static boolean userExists(String usrName)
    {
        DatabaseConnector connection = new DatabaseConnector();
        Integer id = -1;

        if(!connection.open())
        {
            System.out.println("unable to open connection");
        }

        String query = "select users.id from users where username=?";
        try
        {
            PreparedStatement preparedStatement = connection.conn.prepareStatement(query);
            preparedStatement.setString(1, usrName);
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next())
            {
                id = rs.getInt(1);
            }
            connection.conn.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return id < 0 ? false : true;
    }
}
