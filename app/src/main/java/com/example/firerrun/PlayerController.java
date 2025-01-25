package com.example.firerrun;

public class PlayerController {
    private Player player;
    private Block block;

    public PlayerController(Player player, Block block) {
        this.player = player;
        this.block = block;
    }


    public boolean isPlayerOnBlock(Player player, Block block) {
        return (player.getY() + player.getHeight() <= block.getY() &&
                player.getY() + player.getHeight() > block.getY() &&
                player.getX() + player.getWidth() > block.getX() &&
                player.getX() < block.getX() + block.getWidth());
    }


    public void update() {
        if (isPlayerOnBlock(player, block)) {
            player.setY(block.getY() - player.getHeight());
        }
    }
}
