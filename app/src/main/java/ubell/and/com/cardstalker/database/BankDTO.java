package ubell.and.com.cardstalker.database;

import android.content.ContentValues;

public class BankDTO {

    private int _id;
    private String bank;
    private String number;

    public BankDTO(int _id, String bank, String number) {
        this._id = _id;
        this.bank = bank;
        this.number = number;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
