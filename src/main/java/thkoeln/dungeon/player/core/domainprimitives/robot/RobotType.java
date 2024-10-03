package thkoeln.dungeon.player.core.domainprimitives.robot;

import thkoeln.dungeon.player.core.domainprimitives.purchasing.CapabilityType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public enum RobotType {
    MINER,
    SCOUT,
    WARRIOR;

    private static RobotType DEFAULT_TYPE;
    private static final Map<RobotType, Integer> MAX_COUNT = new EnumMap<>(RobotType.class);
    private static final Map<RobotType, Integer> PERCENTAGES = new EnumMap<>(RobotType.class);
    private static final Map<RobotType, Integer> CREATE_AFTER = new EnumMap<>(RobotType.class);
    private static final Map<RobotType, List<CapabilityType>> UPGRADE_ORDER = new EnumMap<>(RobotType.class);

    public static void setDefaultType(RobotType defaultType) {
        DEFAULT_TYPE = defaultType;
    }

    public static RobotType getDefaultType() {
        return DEFAULT_TYPE;
    }

    public static void setMaxCounts(Integer minerMaxCount, Integer scoutMaxCount, Integer warriorMaxCount) {
        MAX_COUNT.put(MINER, minerMaxCount);
        MAX_COUNT.put(SCOUT, scoutMaxCount);
        MAX_COUNT.put(WARRIOR, warriorMaxCount);
    }

    public Integer maxCount() {
        return MAX_COUNT.get(this);
    }

    public static void setPercentages(Integer minerPercentage, Integer scoutPercentage, Integer warriorPercentage) {
        PERCENTAGES.put(MINER, minerPercentage);
        PERCENTAGES.put(SCOUT, scoutPercentage);
        PERCENTAGES.put(WARRIOR, warriorPercentage);
    }

    public Integer percentage() {
        return PERCENTAGES.get(this);
    }

    public static void setCreateAfter(Integer countForMiner, Integer countForScout, Integer countForWarrior) {
        CREATE_AFTER.put(MINER, countForMiner);
        CREATE_AFTER.put(SCOUT, countForScout);
        CREATE_AFTER.put(WARRIOR, countForWarrior);
    }

    public Integer createAfter() {
        return CREATE_AFTER.get(this);
    }

    public static void setUpgradeOrder(List<CapabilityType> minerUpgradeOrder, List<CapabilityType> scoutUpgradeOrder, List<CapabilityType> warriorUpgradeOrder) {
        UPGRADE_ORDER.put(MINER, minerUpgradeOrder);
        UPGRADE_ORDER.put(SCOUT, scoutUpgradeOrder);
        UPGRADE_ORDER.put(WARRIOR, warriorUpgradeOrder);
    }

    public List<CapabilityType> upgradeOrder() {
        return UPGRADE_ORDER.get(this);
    }
}
