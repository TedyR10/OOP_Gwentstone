package Minions;

import java.util.ArrayList;

public class TheRipper extends Minion{

    public TheRipper(int mana, int attackDamage, int health, String description, ArrayList<String> colors, String name,
                     boolean hasAttacked, boolean frozen) {
        super(mana, attackDamage, health, description, colors, name, hasAttacked, frozen);
    }

    public void action(Minion minion) {
        if (minion.getAttackDamage() <= 2)
            minion.setAttackDamage(0);
        else minion.setAttackDamage(minion.getAttackDamage() - 2);
    }
}
