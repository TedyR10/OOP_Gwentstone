package Minions;

import java.util.ArrayList;

public class Goliath extends Minion{

    public Goliath(int mana, int attackDamage, int health, String description, ArrayList<String> colors, String name,
                   boolean hasAttacked, boolean frozen) {
        super(mana, attackDamage, health, description, colors, name, hasAttacked, frozen);
    }
}
