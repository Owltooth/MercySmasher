package MercyGame;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Game {
    public static final int GAME_WINDOW_WIDTH = 1000;
    public static final int GAME_WINDOW_HEIGHT = 700;
    public static final int ROW_DISTANCE = 210;
    public static final int COLUMN_DISTANCE = 320;
    public static final int ROWS = 3;
    public static final int COLUMNS = 3;
    public static final int GAME_ACCELERATION = 100;
    public static final int MAX_HOLES_PER_TICK = 2;
    public static final int GIF_SPEED = 10;
    public static final int DISAPPEAR_MULTIPLIER = 3;
    public static final int OG_SPEED = 3000;
    public static final String SUCCESSFUL_HIT_SOUND = "./resources/sounds/SuccessfulHit.wav";
    public static final String FAIL_HIT_SOUND = "./resources/sounds/Fail.wav";

    public int gameSpeed = OG_SPEED;
    public int score = 0;
    public int timeLeft = 105;
    public int misses = 0;

    JTextField textScore = new JTextField();
    JTextField textTimeLeft = new JTextField();
    JTextField congratulationsText = new JTextField();
    JTextField finalScoreText = new JTextField();
    JTextField missesText = new JTextField();

    Timer timer = new Timer(1000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            timeLeft--;
            textTimeLeft.setText(parseTime(timeLeft));
            if (timeLeft <= 0) {
                endGame();
            }
        }
        
    });

    Timer gameSpeedUpTimer = new Timer(1000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            setGameSpeed(gameSpeed - GAME_ACCELERATION);
        }
    });
    Timer gameTickTimer = new Timer(gameSpeed, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            gameTickTimer.setDelay(gameSpeed);
            gameTick();
        }
    });

    Gif mercyAppear;
    Gif mercyHurt;
    Image hurtMercyImage;
    Image enemyImage;
    Icon holeIcon;
    Image gameIcon;

    JFrame frame = new JFrame();
    List<Hole> holes = new ArrayList<Hole>();
    List<Hole> occupiedHoles = new ArrayList<Hole>();
    Gif[] enemyGifs = new Gif[] {
        new Gif("./resources/gifs/PharahGif", "png"),
        new Gif("./resources/gifs/PoliceGif", "png"),
        new Gif("./resources/gifs/ReinGif", "png")
    };

    AudioPlayer music = new AudioPlayer("./resources/sounds/Music.wav");

    public Game() {
        try {
            mercyHurt = new Gif("./resources/gifs/HurtMercyGif", "png");
            mercyAppear = new Gif("./resources/gifs/MercyGif", "png");
            holeIcon = new ImageIcon("./resources/Images/Hole.png");
            //mercyImage = ImageIO.read(new File("./rsc/gifs/MercyPic.gif"));
            gameIcon = ImageIO.read(new File("./resources/Images/GameIcon.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
        frame.setTitle("A mercy game by Barsik 62km");
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(gameIcon);
        frame.setResizable(false);

        textScore.setBounds(5, 0, 100, 50);
        textScore.setForeground(new Color(255, 0, 255));
        textScore.setFont(new Font("Arial", Font.BOLD, 40));
        textScore.setBorder(null);
        textScore.setEditable(false);
        textScore.setText("0");

        textTimeLeft.setBounds(890, 0, 100, 50);
        textTimeLeft.setForeground(new Color(255, 0, 0));
        textTimeLeft.setFont(new Font("Arial", Font.BOLD, 40));
        textTimeLeft.setBorder(null);
        textTimeLeft.setHorizontalAlignment(JTextField.CENTER);
        textTimeLeft.setEditable(false);
        textTimeLeft.setText("1:45");

        congratulationsText.setBounds(350, 200, 300, 50);
        congratulationsText.setBackground(new Color(0, 0, 0));
        congratulationsText.setForeground(new Color(0, 0, 255));
        congratulationsText.setFont(new Font("Arial", Font.BOLD, 60));
        congratulationsText.setBorder(null);
        congratulationsText.setHorizontalAlignment(JTextField.CENTER);
        congratulationsText.setEditable(false);

        finalScoreText.setBounds(350, 250, 300, 50);
        finalScoreText.setBackground(new Color(0, 0, 0));
        finalScoreText.setForeground(new Color(0, 0, 255));
        finalScoreText.setFont(new Font("Arial", Font.BOLD, 30));
        finalScoreText.setBorder(null);
        finalScoreText.setHorizontalAlignment(JTextField.CENTER);
        finalScoreText.setEditable(false);

        missesText.setBounds(350, 300, 300, 50);
        missesText.setBackground(new Color(0, 0, 0));
        missesText.setForeground(new Color(0, 0, 255));
        missesText.setFont(new Font("Arial", Font.BOLD, 30));
        missesText.setBorder(null);
        missesText.setHorizontalAlignment(JTextField.CENTER);
        missesText.setEditable(false);


        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                JLabel holeLabel = new JLabel();
                holeLabel.setIcon(holeIcon);
                holeLabel.setBounds(100 + j * COLUMN_DISTANCE, 60 + i * ROW_DISTANCE, 130, 150);
                holeLabel.setVerticalAlignment(JLabel.BOTTOM);
                Hole hole = new Hole(holeLabel, this);
                holeLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        hole.onHit();
                    }
                });
                frame.add(holeLabel);
                holes.add(hole);
            }
        }


        frame.add(textTimeLeft);
        frame.add(textScore);
        frame.setVisible(true);
        startGame();
    }
    public void startGame() {
        music.play();
        timer.start();
        gameSpeedUpTimer.start();
        gameTickTimer.start();
    }
    public void gameTick() {
        Random random = new Random();
        int numOfHoles = random.nextInt(MAX_HOLES_PER_TICK) + 1;
        for (int i = 0; i < numOfHoles; i++) {
            if (holes.size() == 0) break;
            int randomHole = Math.max(0, random.nextInt(holes.size()) - 1); 
            int sucrnd = random.nextInt(2);
            Boolean isSuccessful = sucrnd == 1 ? true : false;
            try {
                if (isSuccessful) {
                    holes.get(randomHole).activate();
                } else {
                    int rndEnemy = random.nextInt(3);
                    holes.get(randomHole).activate(enemyGifs[rndEnemy]);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println("Fuck me");
            }
        }
    }
    public String parseTime(int seconds) {
        if (seconds < 60) {
            return String.valueOf(seconds);
        } else {
            int minutes = seconds / 60;
            int secondsLeft = seconds % 60;
            if (secondsLeft < 10) {
                return String.format("%d:0%d", minutes, secondsLeft);
            } else {
                return String.format("%d:%d", minutes, secondsLeft);
            }
        }
    }
    public void endGame() {
        timer.stop();
        gameTickTimer.stop();
        gameSpeedUpTimer.stop();
        showScore();
    }
    public void showScore() {
        congratulationsText.setText("Nahera?");
        finalScoreText.setText(String.format("Final score: %d", score));
        missesText.setText(String.format("Proyebov: %d", misses));
        frame.add(congratulationsText);
        frame.add(finalScoreText);
        frame.add(missesText);
    }
    public void setGameSpeed(int newSpeed) {
        this.gameSpeed = Math.max(newSpeed, 300);
    }
    public void setScore(int newScore) {
        this.score = Math.max(newScore, 0);
        textScore.setText(String.valueOf(score));
    }
}

class Hole {
    private Gif gif;
    private Game game;
    private JLabel label;
    private Timer appearTimer;
    private Timer executionTimer;
    Boolean isSuccessfulHit = false;
    Boolean isActive = false;

    public JLabel getLabel() {
        return label;
    }

    public Hole(JLabel holeLabel, Game game) {
        this.label = holeLabel;
        this.game = game;
    }

    public void activate() {
        isActive = true;
        isSuccessfulHit = true;
        this.gif = game.mercyAppear;
        game.holes.remove(this);
        game.occupiedHoles.add(this);
        GifThread mercyAppearThread = new GifThread(game.mercyAppear, label, Game.GIF_SPEED);
        mercyAppearThread.start();
        Random random = new Random();
        int additionalTIme = random.nextInt((int)(game.gameSpeed * Game.DISAPPEAR_MULTIPLIER - game.gameSpeed)) + game.gameSpeed;
        GifThread outOfTimeDisappearThread = new GifThread(gif, label, -Game.GIF_SPEED);

        executionTimer = new Timer(outOfTimeDisappearThread.getExecutionTime(), new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game.holes.add(Hole.this);
                game.occupiedHoles.remove(Hole.this);
            }

        });
        
        appearTimer = new Timer(additionalTIme, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isActive) {
                    isActive = false;
                    outOfTimeDisappearThread.start();
                    executionTimer.setRepeats(false);
                    executionTimer.start();
                }
            }
        });
        appearTimer.setRepeats(false);
        appearTimer.start();
    }

    public void activate(Gif enemyGif) {
        isActive = true;
        isSuccessfulHit = false;
        this.gif = enemyGif;
        game.holes.remove(this);
        game.occupiedHoles.add(this);
        GifThread mercyAppearThread = new GifThread(gif, label, Game.GIF_SPEED);
        mercyAppearThread.start();
        Random random = new Random();
        int additionalTIme = random.nextInt((int)(game.gameSpeed * Game.DISAPPEAR_MULTIPLIER - game.gameSpeed)) + game.gameSpeed;
        GifThread outOfTimeDisappearThread = new GifThread(gif, label, -Game.GIF_SPEED);

        executionTimer = new Timer(outOfTimeDisappearThread.getExecutionTime(), new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game.holes.add(Hole.this);
                game.occupiedHoles.remove(Hole.this);
            }

        });
        
        appearTimer = new Timer(additionalTIme, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isActive) {
                    isActive = false;
                    outOfTimeDisappearThread.start();
                    executionTimer.setRepeats(false);
                    executionTimer.start();
                }
            }
        });
        appearTimer.setRepeats(false);
        appearTimer.start();
    }

    public void onHit() {
        if (!isActive) {
            return;
        }
        appearTimer.stop();
        isActive = false;
        if (isSuccessfulHit) {
            AudioPlayer hit = new AudioPlayer(Game.SUCCESSFUL_HIT_SOUND);
            hit.play();
            game.setScore(game.score + 1);
            GifThread disappearThread = new GifThread(game.mercyHurt, label, Game.GIF_SPEED);
            disappearThread.start();
        } else {
            AudioPlayer oof = new AudioPlayer(Game.FAIL_HIT_SOUND);
            oof.play();
            game.setScore(game.score - 1);
            game.misses++;
            game.setGameSpeed(Game.OG_SPEED);
            GifThread disappearThread = new GifThread(gif, label, -Game.GIF_SPEED);
            disappearThread.start();
        }
        game.holes.add(this);
        game.occupiedHoles.remove(this);
    }
} 