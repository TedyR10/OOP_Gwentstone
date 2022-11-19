package Minions;

import java.util.ArrayList;

public class Miraj extends Minion{

    public Miraj(int mana, int attackDamage, int health, String description, ArrayList<String> colors, String name,
                 boolean hasAttacked, boolean frozen) {
        super(mana, attackDamage, health, description, colors, name, hasAttacked, frozen);
    }

    public void action(Minion minion) {
        int health = this.getHealth();
        this.setHealth(minion.getHealth());
        minion.setHealth(health);
    }
}
