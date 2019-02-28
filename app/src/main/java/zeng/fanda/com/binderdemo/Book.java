package zeng.fanda.com.binderdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 实体类，实现了序列化
 *
 * @author 曾凡达
 * @date 2019/2/13
 */
public class Book implements Parcelable {
    private int price;
    private String name;

    public Book(int price, String name) {
        this.price = price;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.price);
        dest.writeString(this.name);
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.price = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {

        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "Book{" +
                "price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
