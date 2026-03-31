package component;

public interface Dish {
    String getDescription();
    double getCost();
    void execute(StringBuilder state);
}