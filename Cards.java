import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

public class Cards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }
    }

    private final String[] cardList = {
        "darkness", "double", "fairy", "fighting", "fire",
        "grass", "lightning", "metal", "psychic", "water"
    };

    private final int rows = 4;
    private final int columns = 5;
    private final int cardWidth = 90;
    private final int cardHeight = 128;
    
    private ArrayList<Card> cardSet;
    private ImageIcon cardBackImageIcon;
    
    private JFrame frame;
    private JLabel textLabel;
    private JPanel boardPanel;
    private JButton restartButton;
    
    private int errorCount = 0;
    private ArrayList<JButton> board;
    private Timer hideCardTimer;
    private boolean gameReady = false;
    private JButton card1Selected, card2Selected;

    public Cards() {
        setupCards();
        shuffleCards();
        setupGUI();
    }

    private void setupGUI() {
        frame = new JFrame("Match Cards");
        frame.setLayout(new BorderLayout());
        frame.setSize(columns * cardWidth, rows * cardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel = new JLabel("Errors: " + errorCount, JLabel.CENTER);
        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(textLabel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(rows, columns, 5, 5));
        board = new ArrayList<>();
        
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton(cardBackImageIcon);
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setFocusable(false);
            tile.addActionListener(this::handleCardClick);
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel, BorderLayout.CENTER);

        restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartGame());
        frame.add(restartButton, BorderLayout.SOUTH);

        hideCardTimer = new Timer(980, e -> hideCards());
        hideCardTimer.setRepeats(false);
        frame.pack();
        frame.setVisible(true);

        hideCardTimer.start();
    }

    private void setupCards() {
        cardSet = new ArrayList<>();
        for (String cardName : cardList) {
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
            cardSet.add(new Card(cardName, cardImageIcon));
        }
        cardSet.addAll(cardSet);
        Collections.shuffle(cardSet);

        Image cardBackImg = new ImageIcon(getClass().getResource("./img/card.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    private void shuffleCards() {
        Collections.shuffle(cardSet);
    }

    private void handleCardClick(ActionEvent e) {
        if (!gameReady) return;
        JButton tile = (JButton) e.getSource();
        if (tile.getIcon() == cardBackImageIcon) {
            int index = board.indexOf(tile);
            tile.setIcon(cardSet.get(index).cardImageIcon);
            
            if (card1Selected == null) {
                card1Selected = tile;
            } else if (card2Selected == null) {
                card2Selected = tile;
                checkMatch();
            }
        }
    }

    private void checkMatch() {
        int index1 = board.indexOf(card1Selected);
        int index2 = board.indexOf(card2Selected);

        if (!cardSet.get(index1).cardName.equals(cardSet.get(index2).cardName)) {
            errorCount++;
            textLabel.setText("Errors: " + errorCount);
            hideCardTimer.start();
        } else {
            card1Selected = null;
            card2Selected = null;
        }
    }

    private void hideCards() {
        if (card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected = null;
        } else {
            board.forEach(tile -> tile.setIcon(cardBackImageIcon));
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }

    private void restartGame() {
        gameReady = false;
        restartButton.setEnabled(false);
        card1Selected = null;
        card2Selected = null;
        shuffleCards();
        
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardBackImageIcon);
        }
        
        errorCount = 0;
        textLabel.setText("Errors: " + errorCount);
        hideCardTimer.start();
    }
}
