import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GameGUI extends JFrame implements ActionListener
{
    private int count = 0;
    private JLabel label;
    private JButton button;
    private JPanel panel;
    private JTextField userText;
    private final ImageIcon mainBackground = new ImageIcon("./images/background.png");
    public Game game;
    private JTextArea textArea;
    private PrintStream standardOut;
    
    public GameGUI()
    {
        super("New Game");
        this.game = new Game();
        makeFrame();
    }

    private void makeFrame()
    {
        setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Game");

        panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(6, 10, 10, 10));
        makeMenuBar();
        panel.setLayout(new BorderLayout(8, 8));

        textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
        standardOut = System.out;
        // re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);

        JPanel leftPanel = new JPanel();
        {
            leftPanel.setLayout(new BorderLayout(8, 8));
            JLabel roomName = new JLabel(game.player.currentRoom.getName());
            leftPanel.add(roomName);
        }
        panel.add(leftPanel, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    private void makeMenuBar()
    {
        final int SHORTCUT_MASK =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu menu;
        JMenuItem item;

        // create the File menu
        menu = new JMenu("File");
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
