package Minions;

import java.util.ArrayList;
import java.util.Objects;

public class Hero extends Minion {
    private int mana;
    private int health = 30;
    private String description;
    private ArrayList<String> colors;
    private String name;
    private String specialAbility;
    private boolean hasAttacked = false;

    @Override
    public boolean isHasAttacked() {
        return hasAttacked;
    }

    @Override
    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public void setMana(int mana) {
        this.mana = mana;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ArrayList<String> getColors() {
        return colors;
    }

    @Override
    public void setColors(ArrayList<String> colors) {
        this.colors = colors;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialAbility() {
        return specialAbility;
    }

    public void setSpecialAbility(String specialAbility) {
        if (Objects.equals(this.name, "Lord Royce"))
            this.specialAbility = "Sub-Zero";
        else if (Objects.equals(this.name, "Empress Thorina"))
            this.specialAbility = "Low Blow";
        else if (Objects.equals(this.name, "King Mudface"))
            this.specialAbility = "Earth Born";
        else if (Objects.equals(this.name, "General Kocioraw"))
            this.specialAbility = "Blood Thirst";
    }
    public String toString() {
        return "{"
                +  "mana="
                + mana
                +  ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                +  ""
                + name
                + '\''
                + '}'
                + ", health="
                + health;
    }
}
