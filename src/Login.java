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
                try {
                    Integer num = getUserId(loginName.getText());
                    if(num > 0)
                    {
                        updateAvailablity(num); // this will change the value of available to 0 for users already present in database.
                    }
                    else
                        addUser(loginName.getText());

                    ChatClient client = new ChatClient(loginName.getText());
                    login.setVisible(false);
                    login.dispose();
                } catch (IOException e1) {
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
                        Integer num = getUserId(loginName.getText());
                        if(num > 0)
                        {
                            updateAvailablity(num); // this will change the value of available to 0 for users already present in database.
                        }
                        else
                        {
                            addUser(loginName.getText());
                            ChatClient client = new ChatClient(loginName.getText());
                            login.setVisible(false);
                            login.dispose();
                        }
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
            String query = "insert into users(username, available) values(?, ?)";
            PreparedStatement pStatement = connection.conn.prepareStatement(query);
            //pStatement.setInt(1, 3);
            pStatement.setString(1, loginN);
            pStatement.setBoolean(2, true);
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
        catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    private static void updateAvailablity(Integer id)
    {
        //Change the availablity to true because user is signing in
        DatabaseConnector connection = new DatabaseConnector();

        if(!connection.open())
        {
            System.out.println("unable to open connection");
        }

        try
        {
            String query = "update users set available = 0 where id = ?";
            PreparedStatement pStatement = connection.conn.prepareStatement(query);
            pStatement.setInt(1, id);
            pStatement.execute();
            connection.close();
        }
        catch(Exception ex)
        {
            System.out.print("An error occured: ");
            ex.printStackTrace();
        }
    }
}
