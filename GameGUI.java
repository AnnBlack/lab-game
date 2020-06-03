import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GameGUI  implements ActionListener
{
    private int count = 0;
    private JFrame frame;
    private JLabel label;
    private JButton button;
    private JLayeredPane panel;
    private JTextField userText;
    private final ImageIcon mainBackground = new ImageIcon("./images/background.png");
    public Game game;
    private JTextArea textArea;
    private PrintStream standardOut;
    // JButton
    static JButton b, b1, b2, b3;

    public GameGUI()
    {
        this.frame = new JFrame();
        this.game = new Game();
        makeFrame();
    }

    private void makeFrame()
    {
        frame.setSize(1200,900);
        frame.setTitle("New Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JLayeredPane();

        panel.setPreferredSize(new Dimension(1200, 900));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        makeMenuBar();
        JLabel JBackground = new JLabel(mainBackground);
        JBackground.setSize(1200,900);
        panel.add(JBackground, -1);

        // Current Room Name
        JLabel roomName = new JLabel(game.player.currentRoom.getName());
        roomName.setSize(new Dimension(70,40));
        roomName.setForeground(Color.red);
        roomName.setLocation(90, 140);
        panel.add(roomName, 0);

        // description
        //JLabel label = new JLabel(game.player.currentRoom.printDescription());


        // Image of the current Room
        JPanel gamePanel = new JPanel();
        gamePanel.setSize(350, 500);
        gamePanel.setLayout(new GridLayout(1, 2));
        gamePanel.setLocation(85, 200);

        JLayeredPane roomsPanel = new JLayeredPane();
        JPanel roomImagePanel = new JPanel();
        JLabel roomImage = new JLabel(new ImageIcon("./images/1.png"));
        roomImage.setPreferredSize(new Dimension(350, 500));
        roomImagePanel.add(roomImage);
        roomImagePanel.setSize(350, 500);
        roomsPanel.add(roomImagePanel, -1);
        gamePanel.add(roomsPanel);

        // collect all together
        panel.add(gamePanel, 0);

        // redirect from System.out.println to custom text area
        textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
        standardOut = System.out;

        //re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);

        // buttons for navigation
        b = new JButton("button1");
        b1 = new JButton("button2");
        b2 = new JButton("button3");
        b3 = new JButton("button4");

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void makeMenuBar()
    {
        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        JMenu menu;
        JMenuItem item;

        // create the File menu
        menu = new JMenu("Menu");
        menubar.add(menu);

        item = new JMenuItem("Quit");
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        item.addActionListener(e -> game.player.quit(new Command(CommandWord.QUIT, null)));
        menu.add(item);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        count++;
        label.setText("Number of clicks: " + count);
    }
}
