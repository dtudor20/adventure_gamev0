package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.GamePanel;

public class Floor extends Entity {
    BufferedImage image;
    public Floor(GamePanel game_panel) {
        super(game_panel);
        x=0;
        y=0;
        try{
            image=ImageIO.read(getClass().getClassLoader().getResourceAsStream("res/game/tileset/floor.png"));}
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void update(Graphics2D g2d)
    {
        this.draw(g2d);
    }
    private void draw(Graphics2D g2d)
    {
        g2d.drawImage(image, x, y, game_panel.tile_size, game_panel.tile_size, null);
    }
    
}
