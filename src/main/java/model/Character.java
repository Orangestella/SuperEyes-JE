package model;

import util.PortraitLoader;

import java.nio.file.Path;

public class Character {

    private final String id;
    private final String name;
    private final String desc;
    private final double hpScalingFactor;
    private final double defenseScalingFactor;
    private final double attackScalingFactor;
    private final PortraitLoader portraitLoader;
    private final String position;

    private Path portrait;   // 缓存最近一次根据 state 查到的 portrait

    public Character(String id,
                     String name,
                     String desc,
                     double hpScalingFactor,
                     double defenseScalingFactor,
                     double attackScalingFactor,
                     Path portraitPath,
                     String position) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.hpScalingFactor = hpScalingFactor;
        this.defenseScalingFactor = defenseScalingFactor;
        this.attackScalingFactor = attackScalingFactor;
        this.position = position;
        this.portraitLoader = new PortraitLoader(portraitPath); // 内部已 reload
    }

    /* ---------- 只读属性 ---------- */
    public String getId()                  { return id; }
    public String getName()                { return name; }
    public String getDesc()                { return desc; }
    public double getHpScalingFactor()     { return hpScalingFactor; }
    public double getDefenseScalingFactor(){ return defenseScalingFactor; }
    public double getAttackScalingFactor() { return attackScalingFactor; }

    /* ---------- portrait 相关 ---------- */
    public Path getPortraitPath() {
        return portraitLoader.getPath();
    }

    public void setPortraitPath(Path newPath) {
        portraitLoader.setPath(newPath); // PortraitLoader 内部会 reload
    }

    /**
     * 根据角色当前状态更新 portrait，并返回其值
     */
    public Path updatePortrait(String state) {
        this.portrait = portraitLoader.getPortrait(state, position);
        return this.portrait;
    }

    public Path getPortrait() {
        return portrait;
    }

}
