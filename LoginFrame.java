import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
public class LoginFrame extends JFrame {
    private JTextField usernameTextField;
    private Client client;

    public LoginFrame(Client client) {
        this.client = client;
        setTitle("ChatApp - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 150));

        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create the welcome label
        JLabel welcomeLabel = new JLabel("Welcome to ChatApp");
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        //welcomeLabel.setFont(new Font("MV Boli",Font.BOLD,30));

        // Create the username input components
        JPanel usernamePanel = new JPanel();
        JLabel inputLabel = new JLabel("Please enter your name: ");
        usernameTextField = new JTextField(20);
        usernamePanel.add(inputLabel);
        usernamePanel.add(usernameTextField);

        // Create the enter button
        JButton enterButton = new JButton("Let's Chat!");
        enterButton.setAlignmentX(CENTER_ALIGNMENT);

        // Add components to the main panel
        mainPanel.add(welcomeLabel);
        mainPanel.add(usernamePanel);
        mainPanel.add(enterButton);

        usernameTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                enterButton.doClick();
            }
            
        });

        // ActionListener for the enter button
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextField.getText();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Please enter your name", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (username.equals("All")) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username!s", "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                dispose();
                ChatBoxFrame chatBoxFrame = new ChatBoxFrame(username);
                chatBoxFrame.setClient(client);
                client.setChatBoxFrame(chatBoxFrame);
                client.getOutputStream().println(username);
                }
            }
        });

        // Add the main panel to the frame
        getContentPane().add(mainPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}