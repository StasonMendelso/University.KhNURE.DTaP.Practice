package ua.nure.st.kpp.example.demo.memento;

/**
 * @author Stanislav Hlova
 */
public final class EditItemFormState {
    private final String id;
    private final String vendor;
    private final String name;
    private final String unit;
    private final String weight;
    private final String reserveRate;

    public EditItemFormState(String id, String vendor, String name, String unit, String weight, String reserveRate) {
        this.id = id;
        this.vendor = vendor;
        this.name = name;
        this.unit = unit;
        this.weight = weight;
        this.reserveRate = reserveRate;
    }

    @Override
    public String toString() {
        return "EditItemFormState{" +
                "id='" + id + '\'' +
                ", vendor='" + vendor + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", weight='" + weight + '\'' +
                ", reserveRate='" + reserveRate + '\'' +
                '}';
    }

    public String getVendor() {
        return vendor;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public String getWeight() {
        return weight;
    }

    public String getReserveRate() {
        return reserveRate;
    }

    public String getId() {
        return id;
    }
}
