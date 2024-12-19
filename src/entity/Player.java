package entity;

import game.GamePanel;
import item.Item;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Player extends Entity implements KeyListener {
    private boolean upPressed, downPressed, leftPressed, rightPressed, ePressed, spacePressed;
    private BufferedImage frontImage, backImage, leftImage, rightImage, item_box, selected_item_box, heart,empty_heart;
    public ArrayList<Item> inventory;
    private int inventory_picked =0;
    public String direction;
    public int health;
    public Player(GamePanel game_panel) {
        super(game_panel);
        direction = "front";
        health = 10;
        inventory = new ArrayList<>();
        game_panel.setFocusable(true);
        game_panel.requestFocusInWindow();
        game_panel.addKeyListener(this);
        loadPlayerImages();
    }

    public void drawInventory(Graphics2D g2d) {
        for (int i = 0; i < 5; i++) {
            // Draw the item box
            if(i==inventory_picked)
                g2d.drawImage(selected_item_box, i * game_panel.tile_size, 0, game_panel.tile_size, game_panel.tile_size, null);
            else
            g2d.drawImage(item_box, i * game_panel.tile_size, 0, game_panel.tile_size, game_panel.tile_size, null);
            
            // Draw the item if it exists
            if (i<inventory.size() && inventory.get(i) != null) {
                inventory.get(i).update(g2d);
            }
        }
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }
    private void loadPlayerImages() {
        try {
            frontImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/player/player_front.png"));
            backImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/player/player_back.png"));
            leftImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/player/player_left.png"));
            rightImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/player/player_right.png"));
            heart = ImageIO.read(getClass().getResourceAsStream("/res/player/heart.png"));
            empty_heart = ImageIO.read(getClass().getResourceAsStream("/res/player/empty_heart.png"));
            item_box = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/game/ui/item_box.png"));
            selected_item_box = ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/game/ui/selected_item_box.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void drawHealth(Graphics2D g2d) {
        int maxLife = 10; // Assuming the maximum health is 10
        int heartSize = 32; // Size of the heart image
        int x = game_panel.getWidth() - (maxLife * heartSize) - 20; // 20 pixels from the right edge
        int y = 0; // 20 pixels from the top edge
    
        for (int i = 0; i < maxLife; i++) {
            if (i < health) {
                g2d.drawImage(heart, x + (i * heartSize), y, heartSize, heartSize, null);
            } else {
                g2d.drawImage(empty_heart, x + (i * heartSize), y, heartSize, heartSize, null);
            }
        }
    }
    @Override
    public void movePosition(int x, int y) {
        this.x += x;
        this.y += y;
    }

    private boolean checkCollision(int x, int y) {
    int newX = this.x + x;
    int newY = this.y + y;
    int tileSize = game_panel.tile_size;
    int buffer = 15; // Adjust this value to allow the player to get closer to entities

    List<Entity> entities = game_panel.getEntities();
    for (int i = entities.size() - 1; i >= 0; i--) {
        Entity entity = entities.get(i);
        if (entity != this && !(entity instanceof Floor)) {
            int entityRight = entity.x + tileSize;
            int entityBottom = entity.y + tileSize;
            int playerRight = newX + tileSize;
            int playerBottom = newY + tileSize;
            if (entity instanceof Item && 
                newX + buffer >= entity.x && newX - buffer <= entityRight &&
                newY + buffer >= entity.y && newY - buffer <= entityBottom) {
                ((Item) entity).pickUp();
                return false;
            } else if (entity instanceof Item) {
                return false;
            }
            if (newX + buffer < entityRight && playerRight - buffer > entity.x &&
                newY + buffer < entityBottom && playerBottom - buffer > entity.y) {
                if (ePressed && entity.isInteractable) {
                    entity.isInteractable = false;
                    if (entity instanceof WoodenChest) {
                        ((WoodenChest) entity).isOpened = true;
                    }
                }
                return true;
            }
        }
    }
    return false;
}

    @Override
public void update(Graphics2D g2d) {
    draw(g2d);
    drawInventory(g2d);
    drawHealth(g2d);
    if (spacePressed) {
        if (inventory.size() > inventory_picked) {
            System.out.println(inventory.size());
            inventory.get(inventory_picked).use();
        }
    }
    boolean canMoveUp = upPressed && !checkCollision(0, -speed);
    boolean canMoveDown = downPressed && !checkCollision(0, speed);
    boolean canMoveLeft = leftPressed && !checkCollision(-speed, 0);
    boolean canMoveRight = rightPressed && !checkCollision(speed, 0);
    for (Entity entity : game_panel.getEntities()) {
        if (entity != this) {
            if (canMoveUp) {
                entity.movePosition(0, speed);
                direction = "back";
            }
            if (canMoveDown) {
                entity.movePosition(0, -speed);
                direction = "front";
            }
            if (canMoveLeft) {
                entity.movePosition(speed, 0);
                direction = "left";
            }
            if (canMoveRight) {
                entity.movePosition(-speed, 0);
                direction = "right";
            }
        }
    }
}
    
    private void draw(Graphics2D g2d) {
        BufferedImage currentImage = null;
        switch (direction) {
            case "front":
                currentImage = frontImage;
                break;
            case "back":
                currentImage = backImage;
                break;
            case "left":
                currentImage = leftImage;
                break;
            case "right":
                currentImage = rightImage;
                break;
        }
        if (currentImage != null) {
            g2d.drawImage(currentImage, x, y, game_panel.tile_size, game_panel.tile_size, null);
        }
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (keyCode == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (keyCode == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (keyCode == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (keyCode == KeyEvent.VK_E) {
            ePressed = true;
        }
        if (keyCode == KeyEvent.VK_1) {
            inventory_picked = 0;
        }
        if (keyCode == KeyEvent.VK_2) {
            inventory_picked = 1;
        }
        if (keyCode == KeyEvent.VK_3) {
            inventory_picked = 2;
        }
        if (keyCode == KeyEvent.VK_4) {
            inventory_picked = 3;
        }
        if (keyCode == KeyEvent.VK_5) {
            inventory_picked = 4;
        }
        if(keyCode == KeyEvent.VK_SPACE)
        {
            spacePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) {
            upPressed = false;
            direction="front";

        }
        if (keyCode == KeyEvent.VK_S) {
            downPressed = false;
            direction="front";

        }
        if (keyCode == KeyEvent.VK_A) {
            leftPressed = false;
            direction="front";

        }
        if (keyCode == KeyEvent.VK_D) {
            rightPressed = false;
            direction="front";

        }
        if (keyCode == KeyEvent.VK_E) {
            ePressed = false;
        }
        if(keyCode == KeyEvent.VK_SPACE)
        {
            spacePressed = false;
        }
    }
}