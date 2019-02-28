package zeng.fanda.com.binderdemo.server;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import zeng.fanda.com.binderdemo.Book;

/**
 * @author 曾凡达
 * @date 2019/2/13
 */
public class RemoteService extends Service {

    private List<Book> mBookList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(88, "三体"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return bookManager;
    }

    private final Stub bookManager = new Stub() {

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {

            //权限验证
            int check = checkCallingOrSelfPermission("zeng.fanda.com.binderdemo.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            //其他验证

            return super.onTransact(code, data, reply, flags);
        }

        //真正提供的服务功能
        @Override
        public List<Book> getBooks() throws RemoteException {
            //同步
            synchronized (mBookList) {
                for (Book book : mBookList) {
                    Log.d("server", "getBooks:  " + book.toString());
                }
                return mBookList;
            }

        }

        @Override
        public void addBook(Book book) throws RemoteException {
            //同步
            synchronized (mBookList) {
                mBookList.add(book);
                Log.d("server", "addBook:  " + book.toString());
            }
        }
    };
}
