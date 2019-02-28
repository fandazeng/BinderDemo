package zeng.fanda.com.binderdemo.server;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

import zeng.fanda.com.binderdemo.Book;

/**
 * 这个类用来定义服务端 RemoteService 具备什么样的能力
 *
 * @author 曾凡达
 * @date 2019/2/13
 */
public interface IBookManager extends IInterface {
    List<Book> getBooks() throws RemoteException;

    void addBook(Book book) throws RemoteException;
}
