package settlementexpansion.map.settlement;

public enum SettlementRoomType {
    ROOM(new RoomTypeCondition[]{
            new RoomTypeCondition("bed", 1, true)
    }, 1),
    EMPTY(new RoomTypeCondition[]{}, 0);

    public final RoomTypeCondition[] conditions;
    public final int priority;

    SettlementRoomType(RoomTypeCondition[] conditions, int priority) {
        this.conditions = conditions;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public RoomTypeCondition[] getConditions() {
        return conditions;
    }
}
