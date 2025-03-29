public class Collagen {
    private String name;
    private int price;

    public Collagen(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Collagen{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
