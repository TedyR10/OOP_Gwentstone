package minions;

import java.util.ArrayList;

public class Disciple extends Minion{
    public Disciple(int mana, int attackDamage, int health, String description, ArrayList<String> colors, String name,
                    boolean hasAttacked, boolean frozen) {
        super(mana, attackDamage, health, description, colors, name, hasAttacked, frozen);
    }

    public void action(Minion minion) {
        minion.setHealth(minion.getHealth() + 2);
    }
}
