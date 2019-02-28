package zeng.fanda.com.binderdemo.proxy;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.List;

import zeng.fanda.com.binderdemo.Book;
import zeng.fanda.com.binderdemo.server.IBookManager;
import zeng.fanda.com.binderdemo.server.Stub;

/**
 *  远程服务代理类，需要实现接口，才能代理服务功能
 * @author 曾凡达
 * @date 2019/2/13
 */
public class Proxy implements IBookManager {

    //定义字符符描述
    public static final String DESCRIPTOR = " zeng.fanda.com.binderdemo.BookManager";

    // 是一个 BinderProxy 对象
    private IBinder remote;

    public Proxy(IBinder remote) {
        //构造传入远程服务本地代理对象
        this.remote = remote;
    }

    @Override
    public List<Book> getBooks() throws RemoteException {
        //client端调用，底层通过binder驱动，会回调到binder实体中对应的 onTransact 对法
        //创建输入输出对象
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        //定义返回结果对象
        List<Book> result ;

        try {
            //写入ITnterface的描述
            data.writeInterfaceToken(DESCRIPTOR);
            // 发起跨进程请求，当前线程挂起，通过 Binder 驱动，会回调 Stub 的 onTransact 方法
            //注：若Server进程执行的耗时操作，请不要使用主线程，以防止ANR
            remote.transact(Stub.GET_BOOKS, data, reply, 0);
            //binder 驱动唤醒，线程继续执行，获取返回结果
            reply.readException();
            //反序列化，获取实例
            result = reply.createTypedArrayList(Book.CREATOR);
        } finally {
            reply.recycle();
            data.recycle();
        }
        return result;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        //client端调用，底层通过binder驱动，会回调到binder实体中对应的 onTransact 对法
        //创建输入输出对象
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        try {
            //写入ITnterface的描述
            data.writeInterfaceToken(DESCRIPTOR);

            //写入请求参数
            if (book != null) {
                data.writeInt(1);
                book.writeToParcel(data,0);
            } else {
                data.writeInt(0);
            }
            // 发起跨进程请求，当前线程挂起
            remote.transact(Stub.ADD_BOOK, data, reply, 0);
            //binder 驱动唤醒，线程继续执行，获取返回结果
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public String getInterfaceDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public IBinder asBinder() {
        return remote;
    }
}
