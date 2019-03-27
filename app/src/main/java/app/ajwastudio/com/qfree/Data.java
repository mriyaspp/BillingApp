package app.ajwastudio.com.qfree;

public class Data {
    private String name;
    private String amount;
    private String tax;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public Data(){

    }

    public Data(String name, String amount, String tax) {
        this.name = name;
        this.amount = amount;
        this.tax = tax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
