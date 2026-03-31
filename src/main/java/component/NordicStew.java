package component;

public class NordicStew implements Dish {
    private static final double basePrice = 50.0;
    private static final String name = "Нордское рагу";

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public double getCost() {
        return basePrice;
    }

    @Override
    public void execute(StringBuilder state) {
        state.append(name);

        System.out.println("Готовим " + name);
        System.out.println("Базовая цена: " + basePrice + " спитимов");
        System.out.println("Блюдо: " + state.toString());
        System.out.println("Текущая стоимость: " + getCost() + " спитимов");
    }
}