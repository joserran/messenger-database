import javax.swing.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;

/**
 * Created by joserran on 11/24/2015.
 */
public class ChatClient extends JFrame implements Runnable
{
    Socket socket;
    static final int SOCKETNUMBER = 9987;
    JTextArea ta;
    JButton send, logout, refresh;
    JTextField tf;
    Integer userId;

    Thread thread;
    DataInputStream din;
    DataOutputStream dout;
    String loginName;
    Statement stat;

    ChatClient(String login, int id) throws IOException
    {
        super(login);//call the super constructor to name the JFrame.
        loginName = login;
        userId = id;

        ta = new JTextArea(18, 50);
        tf = new JTextField(50);

        send = new JButton("Send");
        logout = new JButton("Log Out");
        refresh = new JButton("Refresh");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    dout.writeUTF(loginName +  " " + "LOGOUT");
                    System.exit(1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        tf.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try {
                        if(tf.getText().length() > 0)
                        {
                            dout.writeUTF(loginName + " DATA " + tf.getText().toString());
                            tf.setText("");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dout.writeUTF(loginName + " DATA " + tf.getText().toString());
                    tf.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        logout.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    dout.writeUTF(loginName + " LOGOUT ");
                    System.exit(1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        refresh.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                executeAvailability();
            }
        });

        socket = new Socket("localhost", SOCKETNUMBER);

        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());

        dout.writeUTF(loginName);
        dout.writeUTF(loginName +  " LOGIN");

        thread = new Thread(this);
        thread.start();
        setup() ;
    }

    private static void executeAvailability()  {
        DatabaseConnector connection = new DatabaseConnector();
        if(!connection.open())
            System.out.println("unable to connect");

        ResultSet resultSet = null;
        try
        {
            resultSet = connection.executeQuery("select * from users;");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next())
            {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.println("");
                }
            }
            /*
            resultSet = connection.executeQuery("select * from users;");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next())
            {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
            */

        }
            catch (SQLException e) {
            e.printStackTrace();
        }
        connection.close();
    }

    private void setup()
    {
        setSize(600, 400);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(ta));
        panel.add(tf);
        panel.add(send);
        panel.add(logout);
        panel.add(refresh);
        add(panel);
        setVisible(true);
    }

    @Override
    public void run()
    {
        while(true)
        {
            try {
                ta.append(" \n" + din.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException {

    }

    private static int markUserUnavailable(String loginN)
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
}
