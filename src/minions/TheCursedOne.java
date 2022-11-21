package minions;

import java.util.ArrayList;

public class TheCursedOne extends Minion{
    public TheCursedOne(int mana, int attackDamage, int health, String description, ArrayList<String> colors, String name,
                        boolean hasAttacked, boolean frozen) {
        super(mana, attackDamage, health, description, colors, name, hasAttacked, frozen);
    }

    public void action(Minion minion) {
        int attack = minion.getAttackDamage();
        minion.setAttackDamage(minion.getHealth());
        minion.setHealth(attack);
    }
}
