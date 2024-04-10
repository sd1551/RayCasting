package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class SimpleRayCastingDemo extends JFrame implements ActionListener {
    private int side;
    private final int[][] map = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };
    private final double ROTATION_SPEED_COEFFICIENT = 0.005;
    private Point mousePosition;

    private double playerX = 1;
    private double playerY = 1;
    private Timer timer;

    private double dirX = -1;
    private double dirY = 0;
    private double planeX = 0;
    private double planeY = 0.66;

    private boolean moveForward = false;
    private boolean moveBackward = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean rotateLeft = false;
    private boolean rotateRight = false;

    public SimpleRayCastingDemo() {
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        timer = new Timer(1000 / 60, this);
        timer.start();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keycode = e.getKeyCode();
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }

                if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
                    if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                        setExtendedState(JFrame.NORMAL);
                    } else {
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                    }
                }

                switch (keycode) {
                    case KeyEvent.VK_W:
                        moveForward = true;
                        break;
                    case KeyEvent.VK_S:
                        moveBackward = true;
                        break;
                    case KeyEvent.VK_A:
                        moveLeft = true;
                        break;
                    case KeyEvent.VK_D:
                        moveRight = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keycode = e.getKeyCode();
                switch (keycode) {
                    case KeyEvent.VK_W:
                        moveForward = false;
                        break;
                    case KeyEvent.VK_S:
                        moveBackward = false;
                        break;
                    case KeyEvent.VK_A:
                        moveLeft = false;
                        break;
                    case KeyEvent.VK_D:
                        moveRight = false;
                        break;
                }
            }
        });


        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (mousePosition != null) {
                    int dx = e.getXOnScreen() - mousePosition.x;
                    rotateCamera(-dx * ROTATION_SPEED_COEFFICIENT);
                }
                resetMouse();
            }
        });

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                resetMouse();
            }
        });

        setCursor(getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void rotateCamera(double angle) {
        double oldDirX = dirX;
        dirX = dirX * Math.cos(angle) - dirY * Math.sin(angle);
        dirY = oldDirX * Math.sin(angle) + dirY * Math.cos(angle);
        double oldPlaneX = planeX;
        planeX = planeX * Math.cos(angle) - planeY * Math.sin(angle);
        planeY = oldPlaneX * Math.sin(angle) + planeY * Math.cos(angle);
    }

    private void resetMouse() {
        try {
            Robot robot = new Robot();

            Point windowCenter = new Point(getWidth() / 2, getHeight() / 2);

            SwingUtilities.convertPointToScreen(windowCenter, getContentPane());
            robot.mouseMove(windowCenter.x, windowCenter.y);

            mousePosition = windowCenter;
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double moveSpeed = 0.05;
        double rotSpeed = 0.05;

        // Движение вперёд/назад
        if (moveForward) {
            if (map[(int) (playerX + dirX * moveSpeed)][(int) playerY] == 0) {
                playerX += dirX * moveSpeed;
            }
            if (map[(int) playerX][(int) (playerY + dirY * moveSpeed)] == 0) {
                playerY += dirY * moveSpeed;
            }
        }
        if (moveBackward) {
            if (map[(int) (playerX - dirX * moveSpeed)][(int) playerY] == 0) {
                playerX -= dirX * moveSpeed;
            }
            if (map[(int) playerX][(int) (playerY - dirY * moveSpeed)] == 0) {
                playerY -= dirY * moveSpeed;
            }
        }

        if (moveLeft) {
            if (map[(int) (playerX - dirY * moveSpeed)][(int) playerY] == 0) playerX -= dirY * moveSpeed;
            if (map[(int) playerX][(int) (playerY + dirX * moveSpeed)] == 0) playerY += dirX * moveSpeed;
        }
        if (moveRight) {
            if (map[(int) (playerX + dirY * moveSpeed)][(int) playerY] == 0) playerX += dirY * moveSpeed;
            if (map[(int) playerX][(int) (playerY - dirX * moveSpeed)] == 0) playerY -= dirX * moveSpeed;
        }

        if (rotateRight) {
            double oldDirX = dirX;
            dirX = dirX * Math.cos(-rotSpeed) - dirY * Math.sin(-rotSpeed);
            dirY = oldDirX * Math.sin(-rotSpeed) + dirY * Math.cos(-rotSpeed);
            double oldPlaneX = planeX;
            planeX = planeX * Math.cos(-rotSpeed) - planeY * Math.sin(-rotSpeed);
            planeY = oldPlaneX * Math.sin(-rotSpeed) + planeY * Math.cos(-rotSpeed);
        }
        if (rotateLeft) {
            double oldDirX = dirX;
            dirX = dirX * Math.cos(rotSpeed) - dirY * Math.sin(rotSpeed);
            dirY = oldDirX * Math.sin(rotSpeed) + dirY * Math.cos(rotSpeed);
            double oldPlaneX = planeX;
            planeX = planeX * Math.cos(rotSpeed) - planeY * Math.sin(rotSpeed);
            planeY = oldPlaneX * Math.sin(rotSpeed) + planeY * Math.cos(rotSpeed);
        }

        repaint();
    }

    private double[] castRay(double rayDirX, double rayDirY) {
        int mapX = (int) playerX;
        int mapY = (int) playerY;

        double sideDistX, sideDistY;

        double deltaDistX = Math.abs(1 / rayDirX);
        double deltaDistY = Math.abs(1 / rayDirY);
        double perpWallDist;

        int stepX, stepY;
        boolean hit = false;
        int side = 0;

        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (playerX - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - playerX) * deltaDistX;
        }

        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (playerY - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - playerY) * deltaDistY;
        }

        while (!hit) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = 0;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = 1;
            }
            if (map[mapX][mapY] > 0) hit = true;
        }

        if (side == 0) {
            perpWallDist = (sideDistX - deltaDistX);
        } else {
            perpWallDist = (sideDistY - deltaDistY);
        }

        return new double[]{perpWallDist, side};
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage offImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics offGraphics = offImg.getGraphics();
        int h = this.getHeight();
        int w = this.getWidth();

        offGraphics.setColor(Color.GRAY);
        offGraphics.fillRect(0, 0, w, h / 2);
        offGraphics.setColor(Color.DARK_GRAY);
        offGraphics.fillRect(0, h / 2, w, h / 2);

        for (int x = 0; x < w; x++) {
            double cameraX = 2 * x / (double) w - 1;
            double rayDirX = dirX + planeX * cameraX;
            double rayDirY = dirY + planeY * cameraX;

            double[] results = castRay(rayDirX, rayDirY);
            double perpWallDist = results[0];
            this.side = (int) results[1];

            int lineHeight = (int) (h / perpWallDist);
            int drawStart = -lineHeight / 2 + h / 2;
            if (drawStart < 0) drawStart = 0;
            int drawEnd = lineHeight / 2 + h / 2;
            if (drawEnd >= h) drawEnd = h - 1;

            // Изменение в вычислении distanceEffect для мягкого перехода цветов
            float distanceEffect = (float) Math.exp(-perpWallDist * 0.05);

            int baseColorValue = 128;
            int red = (int) (baseColorValue * distanceEffect);
            int green = (int) (baseColorValue * distanceEffect);
            int blue = (int) (baseColorValue * distanceEffect);

            Color wallColor = new Color(Math.min(red, 255), Math.min(green, 255), Math.min(blue, 255));



            offGraphics.setColor(wallColor);
            offGraphics.drawLine(x, drawStart, x, drawEnd);
        }

        g.drawImage(offImg, 0, 0, this);
    }
    public static void main(String[] args) {
        new SimpleRayCastingDemo();
    }
}