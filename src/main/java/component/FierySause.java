package component;

public class FierySause extends DishDecorator {
    private static final double price = 40.0;
    private static final String name = " +Огненный соус";

    public FierySause(Dish dish) {
        super(dish);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + name;
    }

    @Override
    public double getCost() {
        return super.getCost() + price;
    }

    @Override
    public void execute(StringBuilder state) {
        super.execute(state);
        state.append(name);

        System.out.println("Добавляем " + name.trim());
        System.out.println("Цена добавки: " + price + " спитимов");
        System.out.println("Блюдо: " + state.toString());
        System.out.println("Текущая стоимость: " + getCost() + " спитимов");
    }
}
