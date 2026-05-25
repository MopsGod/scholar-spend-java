package scholarspend.model;

public enum ExpenseCategory {
    FOOD,
    TRANSPORT,
    ENTERTAINMENT,
    STUDY_MATERIALS,
    HOUSING,
    HEALTH,
    CLOTHING,
    OTHER;

    public boolean isLeisure() {
        return this == ENTERTAINMENT || this == OTHER;
    }

    public boolean isAcademic() {
        return this == STUDY_MATERIALS;
    }

    public boolean isEssential() {
        return this == FOOD || this == TRANSPORT || this == HOUSING || this == HEALTH;
    }
}
